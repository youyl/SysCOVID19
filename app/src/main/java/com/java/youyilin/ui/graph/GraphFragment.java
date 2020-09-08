package com.java.youyilin.ui.graph;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
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
    private GraphAdapter graphAdapter;

    private ArrayList<Entity> entityList = new ArrayList<>();

    public final static String[] abstractWebsite = {"enwiki", "baidu", "zhwiki"};
    private ArrayList<ArrayList<Property>> propertyList = new ArrayList<>();

    private ArrayList<Boolean> extendedList = new ArrayList<>();
    private ArrayList<ArrayList<Relation>> relationList = new ArrayList<>();

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
                    GraphBackend.getResult(v.getText().toString()).subscribe(new Consumer<JSONArray>() {
                        @Override
                        public void accept(JSONArray array) throws Exception {
                            if (array != null){
                                refreshGraph(array);
                            }
                            else {
                                layoutGraph.setVisibility(View.GONE);
                                Toast.makeText(getContext(), "获取疫情图谱失败", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    searchView.dismissDropDown();
                    InputMethodManager manager = ((InputMethodManager)getContext().getSystemService(Context.INPUT_METHOD_SERVICE));
                    if (manager != null)
                        manager.hideSoftInputFromWindow(searchView.getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
                    searchView.clearFocus();
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

        return root;
    }

    private void refreshGraph(JSONArray entityArray) throws JSONException {
        layoutGraph.setVisibility(View.VISIBLE);

        entityList.clear();
        propertyList.clear();
        relationList.clear();
        extendedList.clear();
        for (int i = 0; i < entityArray.length(); i ++){
            JSONObject entity = entityArray.getJSONObject(i);
            Entity aEntity = new Entity();
            aEntity.label = entity.getString("label");
            aEntity.hot = entity.getDouble("hot");
            aEntity.image = entity.getString("img");

            JSONObject abstractObject = entity.getJSONObject("abstractInfo");
            for (String website: abstractWebsite){
                String text = abstractObject.getString(website);
                if (text.length() > 0){
                    aEntity.abstractInfo = text;
                    break;
                }
            }
            aEntity.extended = (i == 0);
            entityList.add(aEntity);

            JSONObject detailObject = abstractObject.getJSONObject("COVID");
            JSONObject propertyObject = detailObject.getJSONObject("properties");
            ArrayList<Property> aPropertyList = new ArrayList<>();
            Iterator<String> iter = propertyObject.keys();
            while (iter.hasNext()) {
                String n = iter.next();
                String d = propertyObject.getString(n);
                if (d != null && d.length() > 0)
                    aPropertyList.add(new Property(n, d));
            }
            propertyList.add(aPropertyList);

            JSONArray relationArray = detailObject.getJSONArray("relations");
            ArrayList<Relation> aRelationList = new ArrayList<>();
            if (relationArray.length() > 0){
                for (int j = 0; j < relationArray.length(); j ++){
                    JSONObject object = (JSONObject) relationArray.get(j);
                    aRelationList.add(new Relation(object.getString("relation"), object.getBoolean("forward"), object.getString("label"), object.getString("url")));
                }
            }
            relationList.add(aRelationList);
            extendedList.add(true);
        }
        graphAdapter.notifyDataSetChanged();
    }

    class GraphAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_sub_graph, parent, false);
            return new RecyclerViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
            final RecyclerViewHolder h = (RecyclerViewHolder)holder;
            final Entity entity = entityList.get(position);
            if (entity.extended){
                h.recyclerView.setVisibility(View.VISIBLE);
                GraphSubAdapter graphSubAdapter = new GraphSubAdapter(position);
                h.recyclerView.setAdapter(graphSubAdapter);

                String imageUrl = entity.image;
                h.entityImage.setVisibility(View.GONE);
                GraphBackend.getBitmapFromURL(imageUrl).subscribe(new Consumer<Bitmap>() {
                    @Override
                    public void accept(Bitmap bitmap) throws Exception {
                        if (bitmap != null){
                            h.entityImage.setVisibility(View.VISIBLE);
                            h.entityImage.setImageBitmap(bitmap);
                        }
                        else
                            h.entityImage.setVisibility(View.GONE);
                    }
                });

                String a = entity.abstractInfo;
                if (a != null && a.length() > 0) {
                    h.abstractInfo.setVisibility(View.VISIBLE);
                    h.abstractInfo.setText(a);
                }
                else
                    h.abstractInfo.setVisibility(View.GONE);
            }else{
                h.recyclerView.setVisibility(View.GONE);
                h.entityImage.setVisibility(View.GONE);
                h.abstractInfo.setVisibility(View.GONE);
            }

            h.entityLabel.setText(entity.label);
            h.entityLabel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    entity.extended = !entity.extended;
                    notifyItemChanged(position);
                }
            });

            double hot = entity.hot;
            if (hot < 0.33)
                h.hotImage1.setVisibility(View.GONE);
            else
                h.hotImage1.setVisibility(View.VISIBLE);
            if (hot < 0.66)
                h.hotImage2.setVisibility(View.GONE);
            else
                h.hotImage2.setVisibility(View.VISIBLE);
        }

        @Override
        public int getItemCount() {
            return entityList.size();
        }
    }

    class GraphSubAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private int i;

        GraphSubAdapter(int ii){
            i = ii;
        }

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
            int relationTextCount = getRelationTextCount(i);
            int relationCount = getRelationCount(i);
            int buttonCount = getButtonCount(i);
            int propertyTextCount = getPropertyTextCount(i);
            if (position ==  relationTextCount+ relationCount +  buttonCount){
                TextViewHolder h = (TextViewHolder)holder;
                h.text.setText("属性");
                return;
            }
            if (position == 0){
                TextViewHolder h = (TextViewHolder)holder;
                h.text.setText("关系");
                return;
            }
            if (position >= relationTextCount + relationCount +  buttonCount +  propertyTextCount
                    && position < relationTextCount + relationCount +  buttonCount +  propertyTextCount + propertyList.get(i).size()){
                PropertyViewHolder h = (PropertyViewHolder)holder;
                Property p = propertyList.get(i).get(position - (relationTextCount + relationCount +  buttonCount +  propertyTextCount));
                h.name.setText(p.name);
                h.detail.setText(p.detail);
                return;
            }
            if (position >= relationTextCount && position < relationTextCount + relationCount){
                RelationViewHolder h = (RelationViewHolder)holder;
                final Relation r = relationList.get(i).get(position - relationTextCount);
                h.relation.setText(r.relation);
                h.label.setText(r.label);
                if (r.forward)
                    h.forward.setImageResource(R.drawable.ic_baseline_keyboard_arrow_right_24);
                else
                    h.forward.setImageResource(R.drawable.ic_baseline_keyboard_arrow_left_24);
                h.search.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        GraphBackend.getResult(r.label).subscribe(new Consumer<JSONArray>() {
                            @Override
                            public void accept(JSONArray array) throws Exception {
                                if (array != null){
                                    refreshGraph(array);
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
            if (extendedList.get(i))
                h.button.setText("展开");
            else
                h.button.setText("收起");
            h.button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    extendedList.set(i, !extendedList.get(i));
                    notifyDataSetChanged();
                }
            });
        }

        @Override
        public int getItemViewType(int position) {
            int relationTextCount = getRelationTextCount(i);
            int relationCount = getRelationCount(i);
            int buttonCount = getButtonCount(i);
            int propertyTextCount = getPropertyTextCount(i);
             if (position == 0 || position ==  relationTextCount+ relationCount +  buttonCount)
                 return 0;
             if (position >= relationTextCount + relationCount +  buttonCount +  propertyTextCount
                     && position < relationTextCount + relationCount +  buttonCount +  propertyTextCount + propertyList.get(i).size())
                 return 2;
             if (position >= relationTextCount && position < relationTextCount + relationCount)
                 return 1;
             return 3;
        }

        @Override
        public int getItemCount() {
            return  getPropertyTextCount(i) + getRelationTextCount(i) + propertyList.get(i).size() + getRelationCount(i) + getButtonCount(i);
        }

    }

    private int getRelationCount(int j){
        if (relationList.get(j).size() > 6 && extendedList.get(j)){
            return 6;
        }
        return relationList.get(j).size();
    }

    private int getButtonCount(int j){
        if (relationList.get(j).size() > 6)
            return 1;
        return 0;
    }

    private int getRelationTextCount(int j){
        if (relationList.get(j).size() > 0)
            return 1;
        return 0;
    }

    private int getPropertyTextCount(int j){
        if (propertyList.get(j).size() > 0)
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

    class RecyclerViewHolder extends RecyclerView.ViewHolder {

        private TextView entityLabel, abstractInfo;
        private ImageView entityImage, hotImage1, hotImage2;
        private RecyclerView recyclerView;

        public RecyclerViewHolder(@NonNull View root) {
            super(root);
            entityLabel = (TextView)root.findViewById(R.id.entity_label);
            abstractInfo = (TextView)root.findViewById(R.id.entity_abstract);
            entityImage = (ImageView)root.findViewById(R.id.entity_image);
            hotImage1 = (ImageView)root.findViewById(R.id.hot_image_1);
            hotImage2 = (ImageView)root.findViewById(R.id.hot_image_2);
            recyclerView = (RecyclerView) root.findViewById(R.id.recycler_sub_graph);
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setNestedScrollingEnabled(false);
        }
    }
}

class Entity{
    public String label, abstractInfo, image;
    public double hot;
    public boolean extended;
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