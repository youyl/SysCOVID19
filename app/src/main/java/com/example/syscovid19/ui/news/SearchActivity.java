package com.example.syscovid19.ui.news;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.syscovid19.R;

public class SearchActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Button btn=findViewById(R.id.search_exit_btn);
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
        super.finish();
        overridePendingTransition(R.anim.bottom_stable_news,R.anim.bottom_down_news);
    }
}