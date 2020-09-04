package com.example.syscovid19.ui.data;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.reactivestreams.Publisher;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.reactivex.Flowable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public abstract class DataSubBackend {
    public abstract Single<List<DataItem>> fetchData();

    static String getUrlBody(String url) throws IOException {
        URL cs = new URL(url);
        URLConnection tc = cs.openConnection();
        String inputLine, body = "";
        try(BufferedReader in = new BufferedReader(new InputStreamReader(tc.getInputStream()))){
            while ((inputLine = in.readLine()) != null)
                body = body + inputLine;
        }
        return body;
    }

    static List<DataItem> getDataItemList(JSONObject jsonData, String pattern) throws JSONException {
        List<DataItem> dataItemList = new ArrayList<>();
        for (Iterator<String> it = jsonData.keys(); it.hasNext(); ) {
            String k = it.next();
            Pattern r = Pattern.compile(pattern);
            Matcher m = r.matcher(k);
            if (m.find()){
                DataItem item = new DataItem();
                item.name = m.group(1);
                JSONArray jsonArray = (JSONArray) jsonData.getJSONObject(k).get("data");
                JSONArray dataArray = jsonArray.getJSONArray(jsonArray.length() - 1);
                item.confirmed = dataArray.getInt(0);
                item.cured = dataArray.getInt(2);
                item.dead = dataArray.getInt(3);
                dataItemList.add(item);
            }
        }
        return dataItemList;
    }
}

class DomesticDataSubBackend extends DataSubBackend {
    @Override
    public Single<List<DataItem>> fetchData(){
        return Flowable.fromCallable(new Callable<List<DataItem>>() {
            @Override
            public List<DataItem> call() {
                try {
                    String url = "https://covid-dashboard.aminer.cn/api/dist/epidemic.json";
                    String body = getUrlBody(url);
                    if(body.equals("")) {
                        Log.d("warning","DomesticDataSubBackend fetchData failed. ");
                        return new ArrayList<>();
                    }
                    JSONObject jsonData = new JSONObject(body);
                    String pattern = "^China\\|([^\\|]+)$";
                    return getDataItemList(jsonData, pattern);
                } catch(Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }
        }).flatMap(new Function<List<DataItem>, Publisher<? extends DataItem>>() {
            @Override
            public Publisher<? extends DataItem> apply(@NonNull List<DataItem> dataItemList) throws Exception {
                return Flowable.fromIterable(dataItemList);
            }
        }).toList().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }
}
