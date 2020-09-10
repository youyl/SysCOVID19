package com.java.youyilin.ui.cluster;


import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.java.youyilin.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
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
    private ClusterData() {
    }
    public static ClusterData getInstance() {
        return instance;
    }
    private ArrayList<ClusterNewsList>lst;
    private ArrayList<String>namelist;
    public void createData(Context context)
    {
        try {
            StringBuilder bd = new StringBuilder();
            AssetManager assetManager = context.getAssets();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(assetManager.open("cluster.json")));
            String inputLine;
            while ((inputLine = bufferedReader.readLine()) != null) {
                bd.append(inputLine);
            }
            loadData(bd.toString());
        }catch(Exception e)
        {
            Log.d("Fatal Error Created","ClusterData Load Failed");
        }
    }
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
    public ClusterNewsList getNewsList(String _name)
    {
        for (int i=0;i<lst.size();i++)
        {
            if(lst.get(i).name.equals(_name))
            {
                return lst.get(i);
            }
        }
        return null;
    }
}

public class ClusterFragment extends Fragment {

    private CListAdatper mylistadp;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        mylistadp=new CListAdatper(getContext());
        ClusterData.getInstance().createData(getContext());
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_cluster, container, false);
        ListView listlistview=root.findViewById(R.id.cluster_list);
        listlistview.setAdapter(mylistadp);
        mylistadp.setNamelist(ClusterData.getInstance().getNamelist());
        return root;
    }

    public void launchFragment(String _name)
    {
        Intent intent=new Intent(getActivity(),ClusterDetailActivity.class);
        intent.putExtra("NAME",_name);
        startActivity(intent);
    }

    public class CListAdatper extends BaseAdapter
    {
        private ArrayList<String> namelist;
        private Context context;

        CListAdatper(Context _context)
        {
            namelist=new ArrayList<String>();
            context=_context;
        }

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
        public View getView(final int i, View view, ViewGroup viewGroup) {
            view= LayoutInflater.from(context).inflate(R.layout.cluster_list_item,viewGroup,false);
            TextView text=view.findViewById(R.id.cluster_list_content);
            text.setText(namelist.get(i));
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    launchFragment(namelist.get(i));
                }
            });
            return view;
        }
    }
}
