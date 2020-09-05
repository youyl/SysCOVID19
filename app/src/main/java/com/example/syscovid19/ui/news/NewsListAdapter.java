package com.example.syscovid19.ui.news;

import android.content.Context;
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
    private int placeTakerCnt;
    private Context context;
    private AdapterView.OnItemClickListener myItemClicker;

    NewsListAdapter(Context _context)
    {
        context=_context;
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view;
        if(viewType==ITEM_NEWS)
        {
            view=LayoutInflater.from(parent.getContext()).inflate(R.layout.item_news,parent,false);
            return new NewsViewHolder(view);
        }
        if(viewType==ITEM_PLACETAKER)
        {
            view=LayoutInflater.from(parent.getContext()).inflate(R.layout.news_placetaker,parent,false);
            return new PlaceTakerViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position)
    {

    }

    @Override
    public int getItemCount()
    {
        return newslist.size()+placeTakerCnt;
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
        }
    }
    public class PlaceTakerViewHolder extends RecyclerView.ViewHolder
    {
        public PlaceTakerViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
