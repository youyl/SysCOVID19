package com.example.syscovid19.ui.news;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.syscovid19.R;

import java.util.concurrent.Callable;

import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class NewsList extends Fragment {

    private String myTitle;
    private String myCode;

    private SwipeRefreshLayout mySwipeRefresh;
    private int curPage=0;

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
        //init search
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        Log.d("List CreateView","A List CreateViewed");
        View root=inflater.inflate(R.layout.fragment_news_list, container, false);
        mySwipeRefresh=root.findViewById(R.id.swipeRefreshLayout);
        mySwipeRefresh.setColorSchemeColors(getResources().getColor(R.color.colorPrimary));
        mySwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh()
            {
                mySwipeRefresh.setRefreshing(true);
                refresh();
            }
        });
        return root;
    }

    public void refresh()
    {
    }/*
    public Single<Boolean> refreshNews()
    {
        return Single.fromCallable(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                Log.d("Create Call","Created Refresh News Call - start");

                return Boolean.FALSE;
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }*/
}