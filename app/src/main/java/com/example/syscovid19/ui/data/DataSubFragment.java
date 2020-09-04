package com.example.syscovid19.ui.data;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.syscovid19.R;

import java.util.List;

import io.reactivex.Single;
import io.reactivex.functions.Consumer;

class DataItem{
    public String name;
    public int confirmed;
    public int cured;
    public int dead;
}

public class DataSubFragment extends Fragment {

    private SwipeRefreshLayout swipeRefreshLayout;
    private DataSubBackend dataSubBackend;
    List<DataItem> dataItemList;

    DataSubFragment(DataSubBackend d){
        dataSubBackend = d;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fetchData();
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_sub_data, container, false);
        // Inflate the layout for this fragment
        swipeRefreshLayout = (SwipeRefreshLayout) root.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                fetchData();
            }
        });
        return root;
    }

    public void onSuccess() {
        swipeRefreshLayout.setRefreshing(false);
    }

    public void onError() {
        swipeRefreshLayout.setRefreshing(false);
        Toast.makeText(getContext(), "获取疫情数据失败", Toast.LENGTH_SHORT).show();
    }

    public void fetchData() {
        Single<List<DataItem>> single = dataSubBackend.fetchData();
        single.subscribe(new Consumer<List<DataItem>>() {
            @Override
            public void accept(List<DataItem> d) {
                if (d == null || d.size() == 0)
                    onError();
                else{
                    onSuccess();
                    dataItemList = d;
                }
            }
        });
    }
}
