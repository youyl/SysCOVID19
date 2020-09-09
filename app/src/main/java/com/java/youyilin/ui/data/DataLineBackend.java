package com.java.youyilin.ui.data;

import android.util.Log;

import com.github.mikephil.charting.data.Entry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class DataLineBackend {
    private List<Entry> confirmedList;
    private int mode;
    private static DataLineBackend[] _instance = {new DataLineBackend(0), new DataLineBackend(1)};

    public static DataLineBackend getInstance(int i){
        if (i < 0 || i > 1)
            return null;
        return _instance[i];
    }

    private DataLineBackend(int m){
        super();
        mode = m;
    }

    public Single<Boolean> fetchData(final String name){
        return Single.fromCallable(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                try {
                    String url = "https://covid-dashboard.aminer.cn/api/dist/epidemic.json";
                    String body = DataSubBackend.getUrlBody(url);
                    if(body.equals("")) {
                        if (mode == 0)
                            Log.d("warning","DomesticDataLineBackend fetchData failed. ");
                        else
                            Log.d("warning","ForeignDataLineBackend fetchData failed. ");
                        confirmedList =  null;
                    }
                    JSONObject jsonData = new JSONObject(body);
                    getConfirmedList(jsonData, name);
                } catch(Exception e) {
                    e.printStackTrace();
                    confirmedList = null;
                }
                return confirmedList != null;
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    public List<Entry> getConfirmedList() {
        return confirmedList;
    }

    List<Entry> getConfirmedList(JSONObject jsonData, String key) throws JSONException {
        confirmedList = new ArrayList<>();
        JSONArray jsonArray;
        if (mode == 0) {
            if (key.length() == 0)
                jsonArray = (JSONArray) jsonData.getJSONObject("China").get("data");
            else
                jsonArray = (JSONArray) jsonData.getJSONObject("China|" + key).get("data");
        }
        else {
            if (key.length() == 0)
                jsonArray = (JSONArray) jsonData.getJSONObject("World").get("data");
            else
                jsonArray = (JSONArray) jsonData.getJSONObject(key).get("data");
        }
        final int num = 30;
        int i = 0;
        if (jsonArray.length() < num){
            for (i = 0; i < num - jsonArray.length(); i ++)
                confirmedList.add(new Entry(i, 0));
        }
        JSONArray dataArray = jsonArray.getJSONArray(jsonArray.length() - num + i - 1);
        int old_confirmed = dataArray.getInt(0);
        for (; i < num; i ++){
            dataArray = jsonArray.getJSONArray(jsonArray.length() + i - num);
            int confirmed = dataArray.getInt(0);
            confirmedList.add(new Entry(i, confirmed - old_confirmed));
            old_confirmed = confirmed;
        }
        return confirmedList;
    }
}
