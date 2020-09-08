package com.java.youyilin.ui.graph;

import android.graphics.Rect;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.java.youyilin.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

import io.reactivex.functions.Consumer;

public class GraphFragment extends Fragment {

    private LinearLayout layoutGraph;

    private TextView entityName, abstractInfo;
    private ImageView entityImage, hotImage1, hotImage2;

    public final static String[] abstractWebsite = {"enwiki", "baidu", "zhwiki"};
    private ArrayList<Property> propertyList = new ArrayList<>();
    private GraphAdapter graphAdapter;

    private boolean extended = true;
    private ArrayList<Relation> relationList = new ArrayList<>();

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
                                refreshGraph();
                            }
                            else {
                                layoutGraph.setVisibility(View.GONE);
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
        RecyclerView recyclerGraph = (RecyclerView) root.findViewById(R.id.recycler_graph);
        recyclerGraph.setLayoutManager(layoutManager);
        graphAdapter = new GraphAdapter();
        recyclerGraph.setAdapter(graphAdapter);
        recyclerGraph.setNestedScrollingEnabled(false);

        layoutGraph = (LinearLayout)root.findViewById(R.id.layout_graph);
        layoutGraph.setVisibility(View.GONE);

        entityName = (TextView)root.findViewById(R.id.entity_name);
        abstractInfo = (TextView)root.findViewById(R.id.entity_abstract);
        entityImage = (ImageView)root.findViewById(R.id.entity_image);
        hotImage1 = (ImageView)root.findViewById(R.id.hot_image_1);
        hotImage2 = (ImageView)root.findViewById(R.id.hot_image_2);
        return root;
    }

    private void refreshGraph() throws JSONException {
        layoutGraph.setVisibility(View.VISIBLE);

        JSONObject entity = GraphBackend.entity;
        entityName.setText(entity.getString("label"));
        double hot = entity.getDouble("hot");
        if (hot < 0.33)
            hotImage1.setVisibility(View.GONE);
        else
            hotImage1.setVisibility(View.VISIBLE);
        if (hot < 0.66)
            hotImage2.setVisibility(View.GONE);
        else
            hotImage2.setVisibility(View.VISIBLE);
        String imageUrl = entity.getString("img");
        if (imageUrl != null){
            GraphBackend.getBitmapFromURL(imageUrl).subscribe(new Consumer<Boolean>() {
                @Override
                public void accept(Boolean aBoolean) throws Exception {
                    if (aBoolean){
                        entityImage.setVisibility(View.VISIBLE);
                        entityImage.setImageBitmap(GraphBackend.image);
                    }
                    else
                        entityImage.setVisibility(View.GONE);
                }
            });
        }else
            entityImage.setVisibility(View.GONE);
        JSONObject abstractObject = entity.getJSONObject("abstractInfo");
        boolean flag = false;
        for (String website: abstractWebsite){
            String text = abstractObject.getString(website);
            if (text.length() > 0){
                abstractInfo.setText(text);
                flag = true;
                break;
            }
        }
        if (flag)
            abstractInfo.setVisibility(View.VISIBLE);
        else
            abstractInfo.setVisibility(View.GONE);
        JSONObject detailObject = abstractObject.getJSONObject("COVID");
        JSONObject propertyObject = detailObject.getJSONObject("properties");
        propertyList = new ArrayList<>();
        Iterator<String> iter = propertyObject.keys();
        while (iter.hasNext()) {
            String n = iter.next();
            String d = propertyObject.getString(n);
            if (d != null && d.length() > 0)
                propertyList.add(new Property(n, d));
        }

        JSONArray relationArray = detailObject.getJSONArray("relations");
        relationList = new ArrayList<>();
        if (relationArray.length() > 0){
            for (int i = 0; i < relationArray.length(); i ++){
                JSONObject object = (JSONObject) relationArray.get(i);
                relationList.add(new Relation(object.getString("relation"), object.getBoolean("forward"), object.getString("label"), object.getString("url")));
            }
        }
        extended = true;
        graphAdapter.notifyDataSetChanged();
    }

    class GraphAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view;
            switch(viewType){
                case 0:
                    view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_text, parent, false);
                    return new TextViewHolder(view);
                case 1:
                    view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_relation, parent, false);
                    return new RelationViewHolder(view);
                case 2:
                    view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_property, parent, false);
                    return new PropertyViewHolder(view);
                default:
                    view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_button, parent, false);
                    return new ButtonViewHolder(view);
            }

        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            if (position == getRelationTextCount() + getRelationCount() +  getButtonCount()){
                TextViewHolder h = (TextViewHolder)holder;
                h.text.setText("属性");
                return;
            }
            if (position == 0){
                TextViewHolder h = (TextViewHolder)holder;
                h.text.setText("关系");
                return;
            }
            if (position >= getRelationTextCount() + getRelationCount() +  getButtonCount() +  getPropertyTextCount()
                    && position < getRelationTextCount() + getRelationCount() +  getButtonCount() +  getPropertyTextCount() + propertyList.size()){
                PropertyViewHolder h = (PropertyViewHolder)holder;
                Property p = propertyList.get(position - (getRelationTextCount() + getRelationCount() +  getButtonCount() +  getPropertyTextCount()));
                h.name.setText(p.name);
                h.detail.setText(p.detail);
                return;
            }
            if (position >= getRelationTextCount() && position < getRelationTextCount() + getRelationCount()){
                RelationViewHolder h = (RelationViewHolder)holder;
                final Relation r = relationList.get(position - getRelationTextCount());
                h.relation.setText(r.relation);
                h.label.setText(r.label);
                if (r.forward)
                    h.forward.setImageResource(R.drawable.ic_baseline_keyboard_arrow_right_24);
                else
                    h.forward.setImageResource(R.drawable.ic_baseline_keyboard_arrow_left_24);
                h.search.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        GraphBackend.getResult(r.label).subscribe(new Consumer<Boolean>() {
                            @Override
                            public void accept(Boolean aBoolean) throws Exception {
                                if (aBoolean){
                                    refreshGraph();
                                }
                                else {
                                    layoutGraph.setVisibility(View.GONE);
                                    Toast.makeText(getContext(), "获取疫情图谱失败", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });
                return;
            }
            ButtonViewHolder h = (ButtonViewHolder)holder;
            if (extended)
                h.button.setText("展开");
            else
                h.button.setText("收起");
            h.button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    extended = !extended;
                    notifyDataSetChanged();
                }
            });
        }

        @Override
        public int getItemViewType(int position) {
             if (position == 0 || position == getRelationTextCount() + getRelationCount() +  getButtonCount() )
                 return 0;
             if (position >= getRelationTextCount() + getRelationCount() +  getButtonCount() +  getPropertyTextCount()
             && position < getRelationTextCount() + getRelationCount() +  getButtonCount() +  getPropertyTextCount() + propertyList.size())
                 return 2;
             if (position >= getRelationTextCount() && position < getRelationTextCount() + getRelationCount())
                 return 1;
             return 3;
        }

        @Override
        public int getItemCount() {
            return  getPropertyTextCount() + getRelationTextCount() + propertyList.size() + getRelationCount() + getButtonCount();
        }

    }

    private int getRelationCount(){
        if (relationList.size() > 6 && extended){
            return 6;
        }
        return relationList.size();
    }

    private int getButtonCount(){
        if (relationList.size() > 6)
            return 1;
        return 0;
    }

    private int getRelationTextCount(){
        if (relationList.size() > 0)
            return 1;
        return 0;
    }

    private int getPropertyTextCount(){
        if (propertyList.size() > 0)
            return 1;
        return 0;
    }

    class PropertyViewHolder extends RecyclerView.ViewHolder {

        private TextView name, detail;

        public PropertyViewHolder(@NonNull View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.property_name);
            detail = (TextView) itemView.findViewById(R.id.property_detail);
        }
    }
    class RelationViewHolder extends RecyclerView.ViewHolder {

        private TextView relation, label;
        private ImageView forward, search;

        public RelationViewHolder(@NonNull View itemView) {
            super(itemView);
            relation = (TextView) itemView.findViewById(R.id.relation_relation);
            label = (TextView) itemView.findViewById(R.id.relation_label);
            forward = (ImageView) itemView.findViewById(R.id.relation_image_forward);
            search = (ImageView) itemView.findViewById(R.id.relation_image_search);
        }
    }

    class ButtonViewHolder extends RecyclerView.ViewHolder {

        private Button button;

        public ButtonViewHolder(@NonNull View itemView) {
            super(itemView);
            button = (Button) itemView.findViewById(R.id.recycler_button);
        }
    }

    class TextViewHolder extends RecyclerView.ViewHolder {

        private TextView text;

        public TextViewHolder(@NonNull View itemView) {
            super(itemView);
            text = (TextView) itemView.findViewById(R.id.recycler_text);
        }
    }

    class EntityDecoration extends RecyclerView.ItemDecoration {
        //设置ItemView的内嵌偏移长度（inset）
        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            super.getItemOffsets(outRect, view, parent, state);
            outRect.set(0, 8, 0, 8);
        }
    }
}

class Property{
    public String name, detail;
    public Property(String n, String d){
        name = n;
        detail = d;
    }
}

class Relation{
    public String relation;
    public boolean forward;
    public String label;
    public String url;
    public Relation(String r, boolean f, String l, String u){
        relation = r;
        forward = f;
        label = l;
        url = u;
    }
}