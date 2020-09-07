package com.example.syscovid19;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.example.syscovid19.ui.data.DataLineBackend;
import com.example.syscovid19.ui.data.DataSubBackend;

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
        DataSubBackend.getInstance(0).stringArray = getResources().getStringArray(R.array.data_china);
        DataSubBackend.getInstance(1).stringArray = getResources().getStringArray(R.array.data_world);
        DataSubBackend.getInstance(0).refreshData().subscribe(new Consumer<Boolean>() {
            @Override
            public void accept(Boolean aBoolean) throws Exception {
                    add();
            }
        });
        DataLineBackend.getInstance(0).fetchData("").subscribe(new Consumer<Boolean>() {
            @Override
            public void accept(Boolean aBoolean) throws Exception {
                add();
            }
        });
        DataSubBackend.getInstance(1).refreshData();
        DataLineBackend.getInstance(1).fetchData("");
        // delay for initiation
        final long endTime = System.currentTimeMillis() + 5 * 1000;
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (get() < 2 &&  System.currentTimeMillis() < endTime);
                Intent mainIntent = new Intent(CreateActivity.this, MainActivity.class);
                startActivity(mainIntent);
                finish();
            }
        });
        thread.start();
    }

}
