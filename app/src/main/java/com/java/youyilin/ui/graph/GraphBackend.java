package com.java.youyilin.ui.graph;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.java.youyilin.ui.data.DataSubBackend;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Callable;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class GraphBackend {
    public static JSONObject entity;
    public static Bitmap image;

    public static Single<Boolean> getResult(final String keyWord){
        return Single.fromCallable(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                try {
                    String url = "https://innovaapi.aminer.cn/covid/api/v1/pneumonia/entityquery?entity=" + keyWord;
                    String body = DataSubBackend.getUrlBody(url);
                    if(body.equals("")) {
                        Log.d("warning","GraphBackend getResult failed. ");
                        entity = null;
                    }
                    else{
                        JSONObject jsonData = new JSONObject(body);
                        JSONArray jsonArray = (JSONArray) jsonData.get("data");
                        entity = jsonArray.getJSONObject(0);
                    }
                } catch(Exception e) {
                    e.printStackTrace();
                    entity = null;
                }
                return entity != null;
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    public static Single<Boolean> getBitmapFromURL(final String src) {
        return Single.fromCallable(new Callable<Boolean>() {
            @Override
            public Boolean call() {
                try {
                    URL url = new URL(src);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setDoInput(true);
                    connection.connect();
                    InputStream input = connection.getInputStream();
                    image = BitmapFactory.decodeStream(input);
                } catch (IOException e) {
                    e.printStackTrace();
                    image = null;
                }
                return image != null;
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }
}
