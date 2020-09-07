package com.example.syscovid19.ui.graph;

import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.syscovid19.R;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

import io.reactivex.functions.Consumer;

public class GraphFragment extends Fragment {

    private TextView entityName, abstractInfo;
    private ImageView entityImage;
    private LinearLayout layoutEntity;
    private LinearLayout layoutProperty;
    public final static String[] abstractWebsite = {"enwiki", "baidu", "zhwiki"};
    private ArrayList<String> propertyName = new ArrayList<>();
    private ArrayList<String> propertyDetail = new ArrayList<>();
    private PropertyAdapter propertyAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_graph, container, false);
        final AutoCompleteTextView searchView=(AutoCompleteTextView)root.findViewById(R.id.search_view);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.graph_entity));
        searchView.setAdapter(arrayAdapter);
        searchView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    GraphBackend.getResult(v.getText().toString()).subscribe(new Consumer<Boolean>() {
                        @Override
                        public void accept(Boolean aBoolean) throws Exception {
                            if (aBoolean){
                                layoutEntity.setVisibility(View.VISIBLE);
                                JSONObject entity = GraphBackend.entity;
                                entityName.setText(entity.getString("label"));
                                JSONObject abstractObject = entity.getJSONObject("abstractInfo");
                                abstractInfo.setText("");
                                for (String website: abstractWebsite){
                                    String text = abstractObject.getString(website);
                                    if (text.length() > 0){
                                        abstractInfo.setText(text);
                                        break;
                                    }
                                }
                                JSONObject detailObject = abstractObject.getJSONObject("COVID");
                                JSONObject propertyObject = detailObject.getJSONObject("properties");
                                propertyName = new ArrayList<>();
                                propertyDetail = new ArrayList<>();
                                Iterator<String> iter = propertyObject.keys();
                                while (iter.hasNext()) {
                                    String n = iter.next();
                                    String d = propertyObject.getString(n);
                                    if (d != null && d.length() > 0){
                                        propertyName.add(n);
                                        propertyDetail.add(d);
                                    }
                                }
                                if (propertyName.size() == 0)
                                    layoutProperty.setVisibility(View.GONE);
                                else
                                    layoutProperty.setVisibility(View.VISIBLE);
                                propertyAdapter.notifyDataSetChanged();
                            }
                            else {
                                layoutEntity.setVisibility(View.GONE);
                                Toast.makeText(getContext(), "获取疫情图谱失败", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    searchView.dismissDropDown();
                    return true;
                }
                return false;
            }
        });

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        RecyclerView recyclerProperty = (RecyclerView) root.findViewById(R.id.recycler_property);
        recyclerProperty.setLayoutManager(layoutManager);
        propertyAdapter = new PropertyAdapter();
        recyclerProperty.setAdapter(propertyAdapter);
        recyclerProperty.addItemDecoration(new PropertyDecoration());

        layoutEntity = (LinearLayout)root.findViewById(R.id.layout_entity);
        layoutEntity.setVisibility(View.GONE);
        layoutProperty = (LinearLayout)root.findViewById(R.id.layout_property);

        entityName = (TextView)root.findViewById(R.id.entity_name);
        abstractInfo = (TextView)root.findViewById(R.id.entity_abstract);
        entityImage = (ImageView)root.findViewById(R.id.entity_image);
        return root;
    }

    class PropertyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            Log.v("test", "GraphFragment onCreateViewHolder. ");
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_property, parent, false);
            return new PropertyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            Log.v("test", "GraphFragment onBindViewHolder. ");
            PropertyViewHolder h = (PropertyViewHolder)holder;
            h.name.setText(propertyName.get(position));
            h.detail.setText(propertyDetail.get(position));
        }

        @Override
        public int getItemViewType(int position) {
            return 0;
        }

        @Override
        public int getItemCount() {
            return propertyName.size();
        }

    }

    class PropertyViewHolder extends RecyclerView.ViewHolder {

        private TextView name, detail;

        public PropertyViewHolder(@NonNull View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.property_name);
            detail = (TextView) itemView.findViewById(R.id.property_detail);
        }
    }

    class PropertyDecoration extends RecyclerView.ItemDecoration {
        //设置ItemView的内嵌偏移长度（inset）
        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            super.getItemOffsets(outRect, view, parent, state);
            outRect.set(0, 8, 0, 8);
        }
    }
}