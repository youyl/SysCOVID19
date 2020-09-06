package com.example.syscovid19.ui.news;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Context;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import com.example.syscovid19.R;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity {

    private NewsList nl;
    private ListView mylv;
    private HistoryAdapter myAdaptor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        setTitle("搜索");
        Log.d("Search Create","A Search Activity Created");

        FragmentManager fm=getSupportFragmentManager();
        nl=(NewsList)fm.findFragmentById(R.id.search_fragment);
        mylv=findViewById(R.id.search_history_list);
        mylv.bringToFront();

        myAdaptor=new HistoryAdapter(this);
        mylv.setAdapter(myAdaptor);

        Button btn=findViewById(R.id.search_exit_btn);
        btn.setOnClickListener(new View.OnClickListener()
                               {
                                   @Override
                                   public void onClick(View view) {
                                       finish();
                                   }
                               }
        );
        final SearchView searchView=findViewById(R.id.search_window);
        searchView.setSubmitButtonEnabled(true);
        searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b)
                {
                    Log.d("Focus Change Created","TRUE");
                    myAdaptor.updatelist();
                    mylv.setVisibility(View.VISIBLE);
                }
                else
                {
                    Log.d("Focus Change Created","FALSE");
                    mylv.setVisibility(View.GONE);
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

        private Context context;
        private ArrayList<String>historylist=new ArrayList<String>();

        HistoryAdapter(Context _context)
        {
            context=_context;
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
        public View getView(int i, View view, ViewGroup viewGroup) {
            view=LayoutInflater.from(context).inflate(R.layout.item_search_history,viewGroup,false);
            TextView text=view.findViewById(R.id.search_item_text);
            text.setText(historylist.get(i));
            return view;
        }
    }
}