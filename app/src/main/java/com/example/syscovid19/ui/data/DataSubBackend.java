package com.example.syscovid19.ui.data;

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
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.reactivex.Single;

public abstract class DataSubBackend {
    protected List<DataItem> dataItemList;
    protected boolean all;

    public abstract Single<Boolean> refreshData();

    public List<DataItem> getDataItemList(){
        if (dataItemList == null)
            return new ArrayList<>();
        if (all)
            return dataItemList;
        return dataItemList.subList(0, 10);
    }

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
                item.now = item.confirmed - item.cured - item.dead;
                dataItemList.add(item);
            }
        }
        Collections.sort(dataItemList, new Comparator<DataItem>() {
            public int compare(DataItem o1, DataItem o2) {
                return new Integer(o2.now).compareTo(o1.now);
            }
        });
        return dataItemList;
    }
}