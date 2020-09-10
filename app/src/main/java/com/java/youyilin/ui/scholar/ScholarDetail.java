package com.java.youyilin.ui.scholar;

import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.java.youyilin.R;
import com.java.youyilin.ui.graph.GraphBackend;

import io.reactivex.functions.Consumer;

import static android.text.Html.FROM_HTML_MODE_LEGACY;

public class ScholarDetail extends AppCompatActivity {
    private Scholar scholar;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scholar_detail);
        int id =getIntent().getIntExtra("id", 0);
        int mode = getIntent().getIntExtra("mode", 0);
        if (mode == 0)
            scholar = ScholarSubBackend.getInstance().scholarLiveList.get(id);
        else
            scholar = ScholarSubBackend.getInstance().scholarDeadList.get(id);


        TextView nameView = findViewById(R.id.scholar_detail_name);
        String name = scholar.name;
        if (scholar.nameVice != null && scholar.nameVice.length() > 0)
            name = scholar.name + '(' + scholar.nameVice + ')';
        nameView.setText(name);

        final ImageView imageView = findViewById(R.id.scholar_detail_image);
        Bitmap bitmap = ScholarSubFragment.getImageFromAssetsFile(scholar.id + ".jpg", this);
        if (bitmap != null)
            imageView.setImageBitmap(bitmap);
        else{
            GraphBackend.getBitmapFromURL(scholar.avatar).subscribe(new Consumer<Bitmap>() {
                @Override
                public void accept(Bitmap bitmap) throws Exception {
                    if (bitmap != null)
                        imageView.setImageBitmap(bitmap);
                    else
                        imageView.setVisibility(View.INVISIBLE);
                }
            });
        }

        if (scholar.position != null && scholar.position.length() > 0) {
            TextView positionView = findViewById(R.id.scholar_detail_position);
            positionView.setText(scholar.position);
        }
        else
            findViewById(R.id.layout_datail_position).setVisibility(View.GONE);

        TextView affiliationView = findViewById(R.id.scholar_detail_affiliation);
        affiliationView.setText(scholar.affiliation);

        if (scholar.homepage != null && scholar.homepage.length() > 0) {
            TextView homepageView = findViewById(R.id.scholar_detail_homepage);
            homepageView.setText(scholar.homepage);
        }else
            findViewById(R.id.layout_detail_homepage).setVisibility(View.GONE);

        TextView profileView = findViewById(R.id.scholar_detail_profile);
        profileView.setText(String.format("该专家的H指数为%d，发表论文共有%d篇，论文总共被引用了%d次，活跃度为%.2f，多样性为%.2f。",
                scholar.hindex, scholar.pubs, scholar.citations, scholar.activity, scholar.diversity));

        if (scholar.tags.size() == 0)
            findViewById(R.id.layout_research).setVisibility(View.GONE);
        else{
            TextView researchView = findViewById(R.id.scholar_detail_research);
            StringBuilder builder = new StringBuilder(scholar.tags.get(0));
            for (int i = 1; i < scholar.tags.size(); i ++)
                builder.append("、" + scholar.tags.get(i));
            String temp = builder.toString();
            researchView.setText("尤其在" + temp + "领域建树尤为突出。");
        }

        if (scholar.edu == null || scholar.edu.length() == 0)
            findViewById(R.id.layout_edu).setVisibility(View.GONE);
        else {
            TextView eduView = findViewById(R.id.scholar_detail_edu);
            eduView.setText(parseHtml(scholar.edu));
        }

        if (scholar.bio == null || scholar.bio.length() == 0)
            findViewById(R.id.layout_bio).setVisibility(View.GONE);
        else {
            TextView bioView = findViewById(R.id.scholar_detail_bio);
            bioView.setText(parseHtml(scholar.bio));
        }
    }

    public static Spanned parseHtml(String str){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return Html.fromHtml(str, FROM_HTML_MODE_LEGACY, null, null);
        } else {
            return Html.fromHtml(str);
        }
    }
}
