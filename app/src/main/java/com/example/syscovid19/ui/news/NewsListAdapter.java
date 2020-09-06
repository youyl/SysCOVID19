package com.example.syscovid19.ui.news;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.syscovid19.R;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class NewsListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int ITEM_NEWS=1;
    private final int ITEM_PLACETAKER=2;

    private ArrayList<NewsData>newslist=new ArrayList<NewsData>();
    private boolean isRefreshing;
    private NewsList fat;
    private boolean isfreeze;
    private AdapterView.OnItemClickListener myItemClicker;

    public void setIsRefreshing(boolean val)
    {
        isRefreshing=val;
        this.notifyDataSetChanged();
    }

    public boolean getIsRefreshing()
    {
        return isRefreshing;
    }

    public int refreshSize()
    {
        if(isfreeze)return 0;
        else return 1;
    }

    public boolean isIsfreeze()
    {
        return isfreeze;
    }
    public void setFreeze(boolean val)
    {
        if(val!=isfreeze) {
            isfreeze = val;
            this.notifyDataSetChanged();
        }
    }

    NewsListAdapter(NewsList _Fat)
    {
        fat=_Fat;
        isRefreshing=false;
        isfreeze=true;
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view;
        if(viewType==ITEM_NEWS) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_news, parent, false);
            return new NewsViewHolder(view);
        }
        else
        {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.news_placetaker, parent, false);
            return new PlaceTakerViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position)
    {
        if(holder instanceof NewsViewHolder)
        {
            NewsData target=newslist.get(position);
            NewsViewHolder trueholder=(NewsViewHolder)holder;
            if(target.getType().equals(new String("news")))trueholder.type.setText(R.string.news_item_news);
            if(target.getType().equals(new String("event")))trueholder.type.setText(R.string.news_item_event);
            if(target.getType().equals(new String("points")))trueholder.type.setText(R.string.news_item_point);
            if(target.getType().equals(new String("paper")))trueholder.type.setText(R.string.news_item_paper);
            trueholder.title.setText(target.getTitle());
            trueholder.date.setText(target.getDate());
            trueholder.source.setText(target.getSource());
        }
    }

    @Override
    public int getItemCount()
    {
        return newslist.size()+refreshSize();
    }

    @Override
    public int getItemViewType(int position) {
        if(position==newslist.size())return ITEM_PLACETAKER;
        else return ITEM_NEWS;
    }

    public class NewsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {

        private View thisView;
        private TextView title;
        private TextView source;
        private TextView type;
        private TextView date;

        public NewsViewHolder(@NonNull View itemView) {
            super(itemView);
            title=(TextView) itemView.findViewById(R.id.news_item_text_title);
            source=(TextView) itemView.findViewById(R.id.news_item_text_source);
            type=(TextView) itemView.findViewById(R.id.news_item_text_type);
            date=(TextView) itemView.findViewById(R.id.news_item_text_time);
            thisView=itemView;
            thisView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view)
        {
            int pos=this.getLayoutPosition();
            if(pos<newslist.size())
                fat.launchDetail(newslist.get(pos),pos);
        }
    }

    public class PlaceTakerViewHolder extends RecyclerView.ViewHolder
    {
        public PlaceTakerViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    public void setNewslist(ArrayList<NewsData> _newslist)
    {
        newslist = _newslist;
        this.notifyDataSetChanged();
    }

    public void appendNewslist(ArrayList<NewsData> _newslist)
    {
        int origin=newslist.size();
        newslist.addAll(_newslist);
        Log.d("Adaptor Append Created",String.valueOf(_newslist.size()).toString());
        Log.d("Adaptor Append Created Aftermath",String.valueOf(newslist.size()).toString());
        this.notifyDataSetChanged();
    }
}
