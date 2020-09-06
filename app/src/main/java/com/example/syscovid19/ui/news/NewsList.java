package com.example.syscovid19.ui.news;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.syscovid19.R;

import java.util.ArrayList;
import java.util.concurrent.Callable;

import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class NewsList extends Fragment {

    private String myTitle;
    private String myCode;
    private TextView failtorefresh;
    private SwipeRefreshLayout mySwipeRefresh;
    private NewsListAdapter myAdapter;
    private RecyclerView myRecycler;
    private ArrayList<NewsData>tempList;
    private LinearLayoutManager myLayoutManager;

    private int curPage=0;
    private int curChunk=1;
    private String keyword;
    private int lastItemShown;

    public NewsList() {}

    public static NewsList newInstance(String param1, String param2)
    {
        NewsList fragment = new NewsList();
        Bundle args = new Bundle();
        args.putString("Title", param1);
        args.putString("Code", param2);
        fragment.setArguments(args);
        return fragment;
    }

    public void setKeyword(String _keyword)
    {
        keyword=_keyword;
    }

    public String getKeyword()
    {
        return keyword;
    }

    public void markVisited(NewsData news)
    {
        NewsDatabase.getInstance().addHistory(news.getId());
        NewsDatabase.getInstance().addData(news);
    }
    public void launchDetail(NewsData _input)
    {
        String content=NewsCrawler.getInstance().getNewsDetail(_input.getId());
        if(content.isEmpty())
        {
            Toast.makeText(getContext(),"无法取得条目内容",Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent=new Intent(getActivity(),NewsDetail.class);
        intent.putExtra("TITLE",_input.getTitle());
        intent.putExtra("SOURCE",_input.getSource());
        intent.putExtra("DATE",_input.getDate());
        intent.putExtra("CONTENT",content);
        markVisited(_input);
    }
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Log.d("List Create","A List Created !");
        if (getArguments() != null)
        {
            myTitle = getArguments().getString("Title");
            myCode = getArguments().getString("Code");
        }
        myAdapter=new NewsListAdapter(getContext());
        //set click listener
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        Log.d("List CreateView","A List CreateViewed");
        View root=inflater.inflate(R.layout.fragment_news_list, container, false);
        mySwipeRefresh=root.findViewById(R.id.swipeRefreshLayout);
        failtorefresh=root.findViewById(R.id.refresh_fail);
        mySwipeRefresh.setColorSchemeColors(getResources().getColor(R.color.colorPrimary));
        mySwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh()
            {
                mySwipeRefresh.setRefreshing(true);
                failtorefresh.setVisibility(View.GONE);
                refresh();
            }
        });
        if(myTitle==null)
        {
            myTitle="search";
            failtorefresh.setText(R.string.news_unable_search);
        }
        myLayoutManager=new LinearLayoutManager(getContext());
        myRecycler=root.findViewById(R.id.recycler_view);
        myRecycler.setLayoutManager(myLayoutManager);
        myRecycler.setHasFixedSize(true);
        myRecycler.setAdapter(myAdapter);
        myRecycler.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                Log.d("Scroll StateChange Created",Integer.valueOf(newState).toString());
                if(newState==RecyclerView.SCROLL_STATE_IDLE&&lastItemShown==myAdapter.getItemCount()-1)
                {
                    if(!myAdapter.getIsRefreshing()) {
                        myAdapter.setIsRefreshing(true);
                        append();
                    }
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                lastItemShown=myLayoutManager.findLastVisibleItemPosition();
                Log.d("Scroll Action Created",Integer.valueOf(lastItemShown).toString());
            }
        });
        mySwipeRefresh.setRefreshing(true);
        refresh();
        return root;
    }

    public void refresh()
    {
        curPage++;
        if(curPage>6)curPage-=6;
        if(myTitle.equals(new String("search")))curPage=1;
        refreshNews(curPage).subscribe(new Consumer<Boolean>() {
            @Override
            public void accept(Boolean aBoolean) throws Exception {
                if(aBoolean.equals(Boolean.FALSE))
                {
                    failtorefresh.setVisibility(View.VISIBLE);
                }
                else
                {
                    failtorefresh.setVisibility(View.GONE);
                }
                myAdapter.setNewslist(tempList);
                mySwipeRefresh.setRefreshing(false);
            }
        });
    }

    public void append()
    {
        curChunk+=6;
        if(myTitle.equals(new String("search")))curChunk-=5;
        refreshNews(curChunk).subscribe(new Consumer<Boolean>() {
            @Override
            public void accept(Boolean aBoolean) throws Exception {
                if(aBoolean.equals(Boolean.FALSE))
                {
                    Toast.makeText(getContext(),"无法获取更多",Toast.LENGTH_SHORT).show();
                }
                else {
                    myAdapter.appendNewslist(tempList);
                    failtorefresh.setVisibility(View.GONE);
                }
                myAdapter.setIsRefreshing(false);
            }
        });
    }

    public Single<Boolean> refreshNews(final int startPage)
    {
        return Single.fromCallable(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                ArrayList<NewsData>newslist;
                newslist=NewsCrawler.getInstance().getNews(myTitle, keyword,startPage);
                Log.d("NetworkTty Created","Able to Fetch News");
                tempList=newslist;
                if(newslist.size()==0)
                {
                    Log.d("NetworkError Created","Unable to Fetch News");
                    return Boolean.FALSE;
                }
                else return Boolean.TRUE;
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }//*/
}