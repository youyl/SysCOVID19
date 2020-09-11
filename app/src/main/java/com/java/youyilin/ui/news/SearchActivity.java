package com.java.youyilin.ui.news;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.java.youyilin.R;

import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity {

    private NewsList nl;
    private ListView mylv;
    private HistoryAdapter myAdaptor;
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        getSupportActionBar().hide();
        setTitle("搜索");
        Log.d("Search Create","A Search Activity Created");

        FragmentManager fm=getSupportFragmentManager();
        nl=(NewsList)fm.findFragmentById(R.id.search_fragment);
        searchView=findViewById(R.id.search_window);
        mylv=findViewById(R.id.search_history_list);
        mylv.bringToFront();

        myAdaptor=new HistoryAdapter(this,this);
        mylv.setAdapter(myAdaptor);

        searchView.setSubmitButtonEnabled(true);
        searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b)
                {
                    Log.d("Focus Change Created","TRUE");
                    myAdaptor.updatelist();
                    mylv.setVisibility(View.VISIBLE);
                    nl.setVisibility(View.INVISIBLE);
                }
                else
                {
                    Log.d("Focus Change Created","FALSE");
                    mylv.setVisibility(View.GONE);
                    nl.setVisibility(View.VISIBLE);
                }
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                Log.d("Query Text Created",s);
                searchView.clearFocus();
                NewsDatabase.getInstance().addHistory(s);
                if(s.equals(nl.getKeyword())){}
                else
                {
                    nl.setKeyword(s);
                    nl.refresh();
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                Log.d("Query Text create CHANGED",s);
                return true;
            }
        });
    }

    public void finish()
    {
        super.finish();
        overridePendingTransition(R.anim.bottom_stable_news,R.anim.bottom_down_news);
    }

    public class HistoryAdapter extends BaseAdapter
    {

        private SearchActivity fat;
        private Context context;
        private ArrayList<String>historylist=new ArrayList<String>();

        public String getContent(int position)
        {
            return historylist.get(position);
        }

        HistoryAdapter(Context _context,SearchActivity _fat)
        {
            context=_context;
            fat=_fat;
        }

        public void updatelist()
        {
            historylist=NewsDatabase.getInstance().getHistory();
            this.notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return historylist.size();
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
            view=LayoutInflater.from(context).inflate(R.layout.item_search_history,viewGroup,false);
            TextView text=view.findViewById(R.id.search_item_text);
            text.setText(historylist.get(i));
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    fat.searchView.setQuery(historylist.get(i),true);
                }
            });
            return view;
        }
    }
}