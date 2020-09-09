package com.java.youyilin;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.java.youyilin.ui.news.NewsDatabase;

import com.java.youyilin.ui.data.DataSubBackend;

import io.reactivex.functions.Consumer;

public class CreateActivity extends Activity {

    static int steps = 0;
    static synchronized int get(){
        return steps;
    }
    static synchronized void add(){
        steps ++;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);
        NewsDatabase.getInstance().loadData(this);
        DataSubBackend.getInstance(0).stringArray = getResources().getStringArray(R.array.data_china);
        DataSubBackend.getInstance(1).stringArray = getResources().getStringArray(R.array.data_world);
        DataSubBackend.getInstance(0).refreshData().subscribe(new Consumer<Boolean>() {
            @Override
            public void accept(Boolean aBoolean) throws Exception {
                    add();
            }
        });
        DataSubBackend.getInstance(1).refreshData();
        // delay for initiation
        final long endTime = System.currentTimeMillis() + 5 * 1000;
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (get() < 1 &&  System.currentTimeMillis() < endTime);
                Intent mainIntent = new Intent(CreateActivity.this, MainActivity.class);
                startActivity(mainIntent);
                finish();
            }
        });
        thread.start();
    }

}
