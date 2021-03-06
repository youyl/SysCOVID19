package com.java.youyilin.ui.news;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.java.youyilin.R;

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
        if(val)Log.d("TAG Created","To be Visibie prograssbar");
        if(!val)Log.d("TAG Created","Not To be Visibie prograssbar");
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
        if(fat.isOffline())val=true;
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
            if(!fat.isOffline()&&NewsDatabase.getInstance().iscached(target.getId()))
            {
                trueholder.title.setTextColor(0xffb0b0b0);
                trueholder.date.setTextColor(0xffc0c0c0);
                trueholder.source.setTextColor(0xffc0c0c0);
                trueholder.type.setTextColor(0xffa0a0a0);
            }
            else
            {
                trueholder.title.setTextColor(0xff101010);
                trueholder.date.setTextColor(0xff808080);
                trueholder.source.setTextColor(0xff808080);
                trueholder.type.setTextColor(0xff000000);
            }
        }
        else
        {
            PlaceTakerViewHolder thisviewholder=(PlaceTakerViewHolder)holder;
            View view=thisviewholder.itemview;
            TextView text=view.findViewById(R.id.news_placetaker_search);
            ProgressBar bar=view.findViewById(R.id.news_placetaker_progressbar);
            Log.d("TAG Created",String.valueOf(fat.isSearch()));
            if(fat.isSearch())
            {
                text.setVisibility(View.VISIBLE);
                Log.d("TAG Created",String.valueOf(isRefreshing));
                if(!isRefreshing) {
                    bar.setVisibility(View.GONE);
                    text.setVisibility(View.VISIBLE);
                }
                else
                {
                    text.setVisibility(View.GONE);
                    bar.setVisibility(View.VISIBLE);
                }
            }
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
        public View itemview;
        public PlaceTakerViewHolder(@NonNull View itemView) {
            super(itemView);
            itemview=itemView;
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
