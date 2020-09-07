package com.java.youyilin;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.java.youyilin.ui.news.NewsDatabase;

import java.util.Timer;
import java.util.TimerTask;

public class CreateActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);
        NewsDatabase.getInstance().loadData(this);
        // delay for initiation
        TimerTask task = new TimerTask(){
            public void run(){
                Intent mainIntent = new Intent(CreateActivity.this, MainActivity.class);
                startActivity(mainIntent);
                finish();
            }
        };
        Timer timer = new Timer();
        timer.schedule(task, 1 * 1000);
    }
}
