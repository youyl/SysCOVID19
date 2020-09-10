package com.java.youyilin;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.java.youyilin.ui.data.DataSubBackend;
import com.java.youyilin.ui.news.NewsDatabase;

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

        NewsDatabase.getInstance().loadData(this).subscribe(new Consumer<Boolean>() {
            @Override
            public void accept(Boolean aBoolean) throws Exception {
                add();
            }
        });

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
                while (get() < 2 &&  System.currentTimeMillis() < endTime);
                Intent mainIntent = new Intent(CreateActivity.this, MainActivity.class);
                startActivity(mainIntent);
                finish();
            }
        });
        thread.start();
    }

    /*
    private boolean copyAssetAndWrite(String fileName){
        try {
            File cacheDir=getCacheDir();
            if (!cacheDir.exists()){
                cacheDir.mkdirs();
            }
            File outFile =new File(cacheDir,fileName);
            if (!outFile.exists()){
                boolean flag=outFile.createNewFile();
                if (!flag){
                    return false;
                }
            }else {
                if (outFile.length()>10){//表示已经写入一次
                    return true;
                }
            }
            InputStream in=getAssets().open(fileName);
            FileOutputStream fout = new FileOutputStream(outFile);
            byte[] buffer = new byte[1024];
            int byteCount;
            while ((byteCount = in.read(buffer)) != -1) {
                fout.write(buffer, 0, byteCount);
            }
            fout.flush();
            in.close();
            fout.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

     */

}
