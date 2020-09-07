package com.java.youyilin.ui.news;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.java.youyilin.R;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class MenuActivity extends AppCompatActivity {
    private int startCat;
    private int currentCat;
    private ListView leftview;
    private ListView rightview;
    private MyAdaptor leftadaptor;
    private MyAdaptor rightadaptor;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Log.d("Menu Create","News Menu Created");
        setTitle("设置分类列表");
        setContentView(R.layout.activity_menu);
        Button btn=findViewById(R.id.return_button);
        leftview=findViewById(R.id.left_menu);
        rightview=findViewById(R.id.right_menu);
        leftadaptor=new MyAdaptor(this);
        rightadaptor=new MyAdaptor(this);
        leftview.setAdapter(leftadaptor);
        rightview.setAdapter(rightadaptor);
        leftadaptor.setOppo(rightadaptor);
        rightadaptor.setOppo(leftadaptor);
        startCat=GlobalCategory.getInstance().getCatVal();
        currentCat=startCat;
        leftadaptor.setItem(GlobalCategory.getInstance().getCatFrommsk(startCat),startCat);
        rightadaptor.setItem(GlobalCategory.getInstance().getCatFrommsk(3-startCat),startCat);
        btn.setOnClickListener(new View.OnClickListener()
                                    {
                                        @Override
                                        public void onClick(View view) {
                                            finish();
                                        }
                                    }
        );
    }

    public class MyAdaptor extends BaseAdapter
    {

        private MyAdaptor oppo;
        private Context context;
        private AlphaAnimation fading_in;
        //private AlphaAnimation fading_out;
        private int isCreateProcess;
        private int catVal;
        private ArrayList<NewsCategory> catlist=new ArrayList<NewsCategory>();

        public int getCatVal()
        {
            return catVal;
        }

        public void setOppo(MyAdaptor _oppo)
        {
            oppo=_oppo;
        }

        public void setItem(ArrayList<NewsCategory> _list,int cat)
        {
            catlist=_list;
            catVal=cat;
            this.notifyDataSetChanged();
        }

        MyAdaptor(Context _context)
        {
            isCreateProcess=-1;
            context=_context;
            fading_in=(AlphaAnimation)AnimationUtils.loadAnimation(context,R.anim.fading_in_anime);
            //fading_out=(AlphaAnimation)AnimationUtils.loadAnimation(context,R.anim.fading_out_anime);
        }

        public void addItem(NewsCategory _news)
        {
            isCreateProcess=catlist.size();
            catlist.add(_news);
            catVal+=(_news.getIdx()-1);
            this.notifyDataSetChanged();
        }

        public void eraseItem(int x)
        {
            catVal-=(catlist.get(x).getIdx()-1);
            catlist.remove(x);
            this.notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return catlist.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(final int i, View view, ViewGroup viewGroup) {
            view= LayoutInflater.from(context).inflate(R.layout.news_menu_item,viewGroup,false);
            final TextView text=(TextView) view;
            text.setText(catlist.get(i).getTitle());
            if(isCreateProcess==i)
            {
                isCreateProcess=-1;
                text.startAnimation(fading_in);
            }
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    oppo.addItem(catlist.get(i));
                    eraseItem(i);
                }
            });
            return view;
        }
    }

    public void finish()
    {
        currentCat=leftadaptor.getCatVal();
        Log.d("Finish Menu Action Created",String.valueOf(currentCat));
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