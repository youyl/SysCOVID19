package com.example.syscovid19.ui.news;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.syscovid19.R;

public class MenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        Button btn=findViewById(R.id.return_button);
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
        this.setResult(RESULT_OK);
        super.finish();
        overridePendingTransition(R.anim.bottom_stable_news,R.anim.bottom_down_news);
    }
}