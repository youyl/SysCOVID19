package com.java.youyilin.ui.data;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Callable;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class DataSubBackend {
    protected List<DataItem> dataItemList;
    private int mode;
    public String[] stringArray;
    private static DataSubBackend[] _instance = {new DataSubBackend(0), new DataSubBackend(1)};

    public static DataSubBackend getInstance(int i){
        if (i < 0 || i > 1)
            return null;
        return _instance[i];
    }

    private DataSubBackend(int m){
        super();
        mode = m;
    }

    public Single<Boolean> refreshData(){
        return Single.fromCallable(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                try {
                    String url = "https://covid-dashboard.aminer.cn/api/dist/epidemic.json";
                    String body = getUrlBody(url);
                    if(body.equals("")) {
                        if (mode == 0)
                            Log.d("warning","Domestic dataSubBackend fetchData failed. ");
                        else
                            Log.d("warning","World dataSubBackend fetchData failed. ");
                        dataItemList =  new ArrayList<>();
                    }else{
                        JSONObject jsonData = new JSONObject(body);
                        dataItemList = getDataItemList(jsonData);
                    }
                } catch(Exception e) {
                    e.printStackTrace();
                    dataItemList = null;
                }
                return !(dataItemList == null || dataItemList.size() == 0);
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    public List<DataItem> getDataItemList(){
        if (dataItemList == null)
            return new ArrayList<>();
        return dataItemList;
    }

    public static String getUrlBody(String url) throws IOException {
        URL cs = new URL(url);
        URLConnection tc = cs.openConnection();
        tc.setConnectTimeout(5 * 1000);
        String inputLine, body = "";
        try(BufferedReader in = new BufferedReader(new InputStreamReader(tc.getInputStream()))){
            while ((inputLine = in.readLine()) != null)
                body = body + inputLine;
        }
        return body;
    }

    List<DataItem> getDataItemList(JSONObject jsonData) throws JSONException {
        List<DataItem> dataItemList = new ArrayList<>();
        for (String k:stringArray) {
                DataItem item = new DataItem();
                if (mode == 0)
                    item.name = k.substring(6);
                else
                    item.name = k;
                JSONArray jsonArray = (JSONArray) jsonData.getJSONObject(k).get("data");
                JSONArray dataArray = jsonArray.getJSONArray(jsonArray.length() - 1);
                item.confirmed = dataArray.getInt(0);
                item.cured = dataArray.getInt(2);
                item.dead = dataArray.getInt(3);
                item.now = item.confirmed - item.cured - item.dead;
                dataItemList.add(item);
            }
        Collections.sort(dataItemList, new Comparator<DataItem>() {
            public int compare(DataItem o1, DataItem o2) {
                return new Integer(o2.now).compareTo(o1.now);
            }
        });
        return dataItemList;
    }
}