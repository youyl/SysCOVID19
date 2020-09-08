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

import io.reactivex.Maybe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class GraphBackend {

    public static Maybe<JSONArray> getResult(final String keyWord){
        return Maybe.fromCallable(new Callable<JSONArray>() {
            @Override
            public JSONArray call() throws Exception {
                try {
                    String url = "https://innovaapi.aminer.cn/covid/api/v1/pneumonia/entityquery?entity=" + keyWord;
                    String body = DataSubBackend.getUrlBody(url);
                    if(body.equals("")) {
                        Log.d("warning","GraphBackend getResult failed. ");
                        return null;
                    }
                    else{
                        JSONObject jsonData = new JSONObject(body);
                        return (JSONArray)jsonData.get("data");
                    }
                } catch(Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    public static Maybe<Bitmap> getBitmapFromURL(final String src) {
        return Maybe.fromCallable(new Callable<Bitmap>() {
            @Override
            public Bitmap call() {
                try {
                    URL url = new URL(src);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setDoInput(true);
                    connection.connect();
                    InputStream input = connection.getInputStream();
                    return BitmapFactory.decodeStream(input);
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }
}
