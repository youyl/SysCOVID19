package com.java.youyilin.ui.cluster;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.java.youyilin.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

class ClusterNewsList
{
    public ArrayList<String> content;
    public ArrayList<String> date;
    public String name;
    ClusterNewsList(String _name,JSONArray _obj)
    {
        try {
            name = _name;
            date = new ArrayList<String>();
            content = new ArrayList<String>();
            for (int i = 0; i < _obj.length(); i++) {
                JSONObject obj = _obj.getJSONObject(i);
                date.add(obj.getString("date"));
                content.add(obj.getString("title"));
            }
        }catch (Exception e) {
            Log.d("Cluster DetailList Dataload Error Created","hahahahaha");
        }
    }
}

class ClusterData
{
    private static ClusterData instance=new ClusterData();
    private ClusterData() {}
    public static ClusterData getInstance() {
        return instance;
    }
    private ArrayList<ClusterNewsList>lst;
    private ArrayList<String>namelist;
    public void loadData(String str)
    {
        namelist=new ArrayList<String>();
        lst=new ArrayList<ClusterNewsList>();
        try {
            JSONArray largeobj = new JSONObject(str).getJSONArray("data");
            for (int i=0;i<largeobj.length();i++)
            {
                JSONObject smallobj=largeobj.getJSONObject(i);
                String name=smallobj.getString("name");
                namelist.add(name);
                JSONArray littleobj=smallobj.getJSONArray("item");
                lst.add(new ClusterNewsList(name,littleobj));
            }
        }catch (Exception e)
        {
            Log.d("Cluster Dataload Error Created",str);
        }
    }
    public ArrayList<String> getNamelist()
    {
        return namelist;
    }
}

public class ClusterFragment extends Fragment {

    private CListAdatper mylistadp;
    private CDetailAdapter mydetailadp;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        ClusterData.getInstance().loadData(getResources().getString(R.string.cluster_data));
        mylistadp=new CListAdatper();
        mydetailadp=new CDetailAdapter();
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_cluster, container, false);
        ListView listlistview=root.findViewById(R.id.cluster_list);
        ListView detaillistview=root.findViewById(R.id.cluster_detail);
        listlistview.setAdapter(mylistadp);
        detaillistview.setAdapter(mydetailadp);
        mylistadp.setNamelist(ClusterData.getInstance().getNamelist());
        return root;
    }

    public class CListAdatper extends BaseAdapter
    {
        private ArrayList<String> namelist;

        public void setNamelist(ArrayList<String>_namelist)
        {
            namelist=_namelist;
            this.notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return namelist.size();
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
            return null;
        }
    }

    public class CDetailAdapter extends BaseAdapter
    {
        private ClusterNewsList lst;

        @Override
        public int getCount() {
            return 0;
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            return null;
        }
    }
}
