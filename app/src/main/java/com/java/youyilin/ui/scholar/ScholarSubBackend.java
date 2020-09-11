package com.java.youyilin.ui.scholar;

import android.app.Activity;
import android.graphics.Bitmap;
import android.util.Log;

import com.java.youyilin.ui.data.DataSubBackend;
import com.java.youyilin.ui.graph.GraphBackend;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Callable;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class ScholarSubBackend {
    public List<Scholar> scholarLiveList = new ArrayList<>();
    public List<Scholar> scholarDeadList = new ArrayList<>();
    private static ScholarSubBackend _instance = new ScholarSubBackend();

    public static ScholarSubBackend getInstance(){
        return _instance;
    }

    public Single<Boolean> dealScholar(final Activity activity){
        scholarLiveList = new ArrayList<>();
        scholarDeadList = new ArrayList<>();
        return Single.fromCallable(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                try {
                    String url = "https://innovaapi.aminer.cn/predictor/api/v1/valhalla/highlight/get_ncov_expers_list?v=2";
                    String body = DataSubBackend.getUrlBody(url);
                    if(body.equals("")) {
                        Log.d("warning","ScholarSubBackend dealScholar getUrlBody failed. ");
                    }else{
                        JSONObject jsonData = new JSONObject(body);
                        JSONArray scholarJSONArray = jsonData.getJSONArray("data");
                        for (int i = 0; i < scholarJSONArray.length(); i ++) {
                            JSONObject scholarObject = scholarJSONArray.getJSONObject(i);
                            Scholar scholar = new Scholar();
                            scholar.avatar = GraphBackend.getJSONString(scholarObject, "avatar");
                            scholar.id = scholarObject.getString("id");

                            {
                                JSONObject indices = scholarObject.getJSONObject("indices");
                                scholar.activity = indices.getDouble("activity");
                                scholar.citations = indices.getInt("citations");
                                scholar.diversity = indices.getDouble("diversity");
                                scholar.gindex = indices.getInt("gindex");
                                scholar.hindex = indices.getInt("hindex");
                                scholar.pubs = indices.getInt("pubs");
                                scholar.sociability = indices.getDouble("sociability");
                            }

                            scholar.name = scholarObject.getString("name");
                            String nameZh = scholarObject.getString("name_zh");
                            if (nameZh != null && nameZh.length() > 0) {
                                scholar.nameVice = scholar.name;
                                scholar.name = nameZh;
                            }

                            scholar.numViewed = scholarObject.getInt("num_viewed");

                            {
                                JSONObject profile = scholarObject.getJSONObject("profile");
                                String affiliation = profile.getString("affiliation");
                                String affiliationZh = GraphBackend.getJSONString(profile, "affiliation_zh");
                                if (affiliationZh == null || affiliationZh.length() == 0)
                                    scholar.affiliation = affiliation;
                                else
                                    scholar.affiliation = affiliation + '/' + affiliationZh;

                                scholar.bio = GraphBackend.getJSONString(profile, "bio");
                                scholar.edu = GraphBackend.getJSONString(profile, "edu");
                                scholar.homepage = GraphBackend.getJSONString(profile, "homepage");
                                scholar.position = GraphBackend.getJSONString(profile, "position");
                                scholar.work = GraphBackend.getJSONString(profile, "work");
                            }

                            if (scholarObject.has("tags")) {
                                List<Tag> tags = new ArrayList<>();
                                JSONArray tagsJSONArray = scholarObject.getJSONArray("tags");
                                JSONArray tagsScoreArray = scholarObject.getJSONArray("tags_score");

                                int num = Math.min(tagsJSONArray.length(), tagsScoreArray.length());
                                for (int j = 0; j < num; j++) {
                                    tags.add(new Tag(tagsJSONArray.getString(j), tagsScoreArray.getInt(j)));
                                }
                                Collections.sort(tags, new Comparator<Tag>() {
                                    public int compare(Tag o1, Tag o2) {
                                        return new Integer(o2.score).compareTo(o1.score);
                                    }
                                });
                                for (int j = 0; j < tags.size(); j++) {
                                    scholar.tags.add(tags.get(j).tag);
                                }
                            }
                            if (scholarObject.getBoolean("is_passedaway"))
                                scholarDeadList.add(scholar);
                            else
                                scholarLiveList.add(scholar);
                        }
                    }
                } catch(Exception e) {
                    //e.printStackTrace();
                    Log.d("test", "ScholarSubBackend dealScholar failed. ");
                }
                Collections.sort(scholarLiveList, new Comparator<Scholar>() {
                    public int compare(Scholar o1, Scholar o2) {
                        return new Integer(o2.numViewed).compareTo(o1.numViewed);
                    }
                });
                if (scholarLiveList.size() > 50)
                    scholarLiveList = scholarLiveList.subList(0, 50);
                return scholarLiveList.size() != 0 || scholarDeadList.size() != 0;
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    public static Bitmap getGrayBitmap(Bitmap img){
        int width = img.getWidth();			//获取位图的宽
        int height = img.getHeight();		//获取位图的高

        int []pixels = new int[width * height];	//通过位图的大小创建像素点数组

        img.getPixels(pixels, 0, width, 0, 0, width, height);
        int alpha = 0xFF << 24;
        for(int i = 0; i < height; i++)	{
            for(int j = 0; j < width; j++) {
                int grey = pixels[width * i + j];

                int red = ((grey  & 0x00FF0000 ) >> 16);
                int green = ((grey & 0x0000FF00) >> 8);
                int blue = (grey & 0x000000FF);

                grey = (int)((float) red * 0.3 + (float)green * 0.59 + (float)blue * 0.11);
                grey = alpha | (grey << 16) | (grey << 8) | grey;
                pixels[width * i + j] = grey;
            }
        }
        Bitmap result = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        result.setPixels(pixels, 0, width, 0, 0, width, height);
        return result;
    }
}

class Tag{
    String tag;
    int score;
    Tag(String t, int s){
        tag = t;
        score = s;
    }
}
