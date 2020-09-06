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
        final DataSubBackend dataSubBackend[] = {DataSubBackend.getInstance(0), DataSubBackend.getInstance(1)};
        dataSubBackend[0].refreshData().subscribe(new Consumer<Boolean>() {
            @Override
            public void accept(Boolean aBoolean) throws Exception {
                if (aBoolean){
                    final String name = dataSubBackend[0].getDataItemList().get(0).name;
                    DataLineBackend.getInstance(0).fetchData(name).subscribe(new Consumer<Boolean>() {
                        @Override
                        public void accept(Boolean aBoolean) throws Exception {
                            add();
                            return;
                        }
                    });
                }
            }
        });
        dataSubBackend[1].refreshData().subscribe(new Consumer<Boolean>() {
            @Override
            public void accept(Boolean aBoolean) throws Exception {
                if (aBoolean){
                    final String name = dataSubBackend[1].getDataItemList().get(0).name;
                    DataLineBackend.getInstance(1).fetchData(name);
                }
            }
        });
    }

}
