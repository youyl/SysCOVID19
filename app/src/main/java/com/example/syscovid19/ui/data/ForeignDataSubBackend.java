package com.example.syscovid19.ui.data;

import android.util.Log;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.Callable;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class ForeignDataSubBackend extends DataSubBackend {
    private static DataSubBackend _instance;

    public static DataSubBackend getInstance(){
        if (_instance == null)
            _instance = new ForeignDataSubBackend();
        return _instance;
    }

    private ForeignDataSubBackend(){
        super();
    }

    @Override
    public Single<Boolean> refreshData(){
        return Single.fromCallable(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                try {
                    String url = "https://covid-dashboard.aminer.cn/api/dist/epidemic.json";
                    String body = getUrlBody(url);
                    if(body.equals("")) {
                        Log.d("warning","ForeignDataSubBackend fetchData failed. ");
                        dataItemList =  new ArrayList<>();
                    }
                    JSONObject jsonData = new JSONObject(body);
                    String pattern = "^([^\\|]+)$";
                    dataItemList = getDataItemList(jsonData, pattern);
                } catch(Exception e) {
                    e.printStackTrace();
                    dataItemList = null;
                }
                return !(dataItemList == null || dataItemList.size() == 0);
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }
}

