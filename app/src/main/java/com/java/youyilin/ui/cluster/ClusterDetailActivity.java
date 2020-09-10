package com.java.youyilin.ui.cluster;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.java.youyilin.R;

import org.w3c.dom.Text;

public class ClusterDetailActivity extends AppCompatActivity {

    private String name;
    private CDetailAdapter mydetailadp;
    private ListView detailview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        name=getIntent().getStringExtra("NAME");
        setContentView(R.layout.activity_cluster_detail);
        getSupportActionBar().hide();
        TextView text=findViewById(R.id.cluster_detail_title);
        text.setText(name);
        detailview=findViewById(R.id.cluster_detail_list);
        mydetailadp=new CDetailAdapter(this,ClusterData.getInstance().getNewsList(name));
        detailview.setAdapter(mydetailadp);
    }

    public class CDetailAdapter extends BaseAdapter
    {
        private int reading;
        private ClusterNewsList lst;
        private Context context;

        CDetailAdapter(Context _context,ClusterNewsList _lst)
        {
            context=_context;
            lst=_lst;
            reading=-1;
        }

        @Override
        public int getCount() {
            return lst.date.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        public void change()
        {
            this.notifyDataSetChanged();
        }

        @Override
        public View getView(final int i, View view, ViewGroup viewGroup) {
            view= LayoutInflater.from(context).inflate(R.layout.cluster_detail_item,viewGroup,false);
            TextView text=view.findViewById(R.id.cluster_detail_content);
            text.setText(lst.content.get(i));
            if(reading==i)
            {
                text.setSingleLine(false);
            }
            else
            {
                text.setSingleLine(true);
            }
            text=view.findViewById(R.id.cluster_detail_date);
            text.setText(lst.date.get(i));
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(reading!=i)reading=i;
                    else reading=-1;
                    change();
                }
            });
            return view;
        }
    }
}