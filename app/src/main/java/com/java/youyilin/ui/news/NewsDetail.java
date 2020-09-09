package com.java.youyilin.ui.news;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.java.youyilin.R;

public class NewsDetail extends AppCompatActivity
{

    private String title;
    private String content;
    private String source;
    private String date;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_detail);
        title=getIntent().getStringExtra("TITLE");
        content=new StringBuilder().append("    ").append(getIntent().getStringExtra("CONTENT")).toString();
        source=getIntent().getStringExtra("SOURCE");
        date=getIntent().getStringExtra("DATE");

        Button btn=findViewById(R.id.detail_exit_btn);
        btn.setOnClickListener(new View.OnClickListener()
                               {
                                   @Override
                                   public void onClick(View view) {
                                       finish();
                                   }
                               }
        );

        Button share=findViewById(R.id.share_btn);
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getBaseContext(),"Share it Off",Toast.LENGTH_SHORT).show();
            }
        });

        TextView titleview = findViewById(R.id.news_detail_title);
        TextView sourceview = findViewById(R.id.news_detail_source);
        TextView dateview = findViewById(R.id.news_detail_date);
        TextView contentview = findViewById(R.id.news_detail_content);
        titleview.setText(title);
        sourceview.setText(source);
        dateview.setText(date);
        contentview.setText(content);
    }
}