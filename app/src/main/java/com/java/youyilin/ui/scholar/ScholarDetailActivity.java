package com.java.youyilin.ui.scholar;

import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.java.youyilin.R;
import com.java.youyilin.ui.graph.GraphBackend;

import io.reactivex.functions.Consumer;

import static android.text.Html.FROM_HTML_MODE_LEGACY;

public class ScholarDetailActivity extends AppCompatActivity {
    private Scholar scholar;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scholar_detail);
        int id =getIntent().getIntExtra("id", 0);
        final int mode = getIntent().getIntExtra("mode", 0);
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
        if (bitmap != null) {
            if (mode == 1)
                imageView.setImageBitmap(ScholarSubBackend.getGrayBitmap(bitmap));
            else
                imageView.setImageBitmap(bitmap);
        }
        else{
            GraphBackend.getBitmapFromURL(scholar.avatar).subscribe(new Consumer<Bitmap>() {
                @Override
                public void accept(Bitmap bitmap) throws Exception {
                    if (bitmap != null) {
                        if (mode == 1)
                            imageView.setImageBitmap(ScholarSubBackend.getGrayBitmap(bitmap));
                        else
                            imageView.setImageBitmap(bitmap);
                    }
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
            TextView homepageView = findViewById(R.id.scholar_homepage);
            homepageView.setText(scholar.homepage);
        }else
            findViewById(R.id.layout_detail_homepage).setVisibility(View.GONE);

        TextView profileView = findViewById(R.id.scholar_detail_profile);
        profileView.setText(String.format("该专家的H指数为%d，发表论文共有%d篇，论文总共被引用了%d次，活跃度为%.2f，多样性为%.2f。",
                scholar.hindex, scholar.pubs, scholar.citations, scholar.activity, scholar.diversity));

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

    @Override
    public void onWindowFocusChanged(boolean hasFocus){
        super.onWindowFocusChanged(hasFocus);
        if (!hasFocus)
            return;

        TextView tagsView[] = {findViewById(R.id.tag_1), findViewById(R.id.tag_2), findViewById(R.id.tag_3),
                findViewById(R.id.tag_4), findViewById(R.id.tag_5), findViewById(R.id.tag_6)};
        int num = 6;
        if (scholar.tags.size() < 6){
            num = scholar.tags.size();
            for (int i = num; i < 6; i ++)
                tagsView[i].setVisibility(View.GONE);
        }
        for (int i = 0; i < num; i ++)
            tagsView[i].setText(scholar.tags.get(i));
        float width = ((RelativeLayout) findViewById(R.id.layout_tags)).getWidth();
        TextView up = null;
        TextView start = tagsView[0];
        TextView last = tagsView[0];
        float w = tagsView[0].getPaint().measureText(scholar.tags.get(0)) + TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, getResources().getDisplayMetrics());
        Log.v("test", "0 " + w + " " + scholar.tags.get(0));
        for (int i = 1; i < num; i ++) {
            float tw = tagsView[i].getPaint().measureText(scholar.tags.get(i)) + TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, getResources().getDisplayMetrics());
            Log.v("test", "" + i + " " + tw +" " + scholar.tags.get(i));
            if (w + tw <= width){
                ((RelativeLayout.LayoutParams) tagsView[i].getLayoutParams()).addRule(RelativeLayout.RIGHT_OF, last.getId());
                if (up != null)
                    ((RelativeLayout.LayoutParams) tagsView[i].getLayoutParams()).addRule(RelativeLayout.BELOW, up.getId());
                last = tagsView[i];
                w = w + tw;
            }else{
                ((RelativeLayout.LayoutParams) tagsView[i].getLayoutParams()).addRule(RelativeLayout.BELOW, start.getId());
                up = start;
                start = last = tagsView[i];
                w = tw;
            }
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
