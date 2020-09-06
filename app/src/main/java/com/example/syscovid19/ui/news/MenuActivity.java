package com.example.syscovid19.ui.news;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.syscovid19.R;

public class MenuActivity extends AppCompatActivity {

    private int startCat;
    private int currentCat;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Log.d("Menu Create","News Menu Created");
        setTitle("设置分类列表");
        setContentView(R.layout.activity_menu);
        Button btn=findViewById(R.id.return_button);
        startCat=GlobalCategory.getInstance().getCatVal();
        currentCat=startCat;
        btn.setOnClickListener(new View.OnClickListener()
                                    {
                                        @Override
                                        public void onClick(View view) {
                                            finish();
                                        }
                                    }
        );
    }

    public void finish()
    {
        GlobalCategory.getInstance().setCat(currentCat);
        if(startCat!=currentCat)
        {
            this.setResult(RESULT_OK);
        }
        else
        {
            this.setResult(RESULT_CANCELED);
        }
        startCat=currentCat;
        super.finish();
        overridePendingTransition(R.anim.bottom_stable_news,R.anim.bottom_down_news);
    }

    @Override
    protected void onDestroy() {
        Log.d("Menu Create","News Menu Destroyed");
        super.onDestroy();
    }
}