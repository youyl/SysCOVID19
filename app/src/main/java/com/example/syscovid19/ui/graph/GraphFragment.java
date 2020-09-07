package com.example.syscovid19.ui.graph;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.syscovid19.R;

import org.json.JSONObject;

import io.reactivex.functions.Consumer;

public class GraphFragment extends Fragment {

    private TextView name, abstractInfo;
    private ImageView image;
    private RecyclerView recyclerView;
    public final static String[] abstractWebsite = {"enwiki", "baidu", "zhwiki"};

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
                                recyclerView.setVisibility(View.VISIBLE);
                                JSONObject entity = GraphBackend.entity;
                                name.setText(entity.getString("label"));
                                JSONObject abstractObject = entity.getJSONObject("abstractInfo");
                                for (String website: abstractWebsite){
                                    String text = abstractObject.getString(website);
                                    if (text.length() > 0){
                                        abstractInfo.setText(text);
                                        break;
                                    }
                                }
                            }
                            else {
                                recyclerView.setVisibility(View.INVISIBLE);
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
        recyclerView = (RecyclerView) root.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(layoutManager);
        GraphAdapter graphAdapter = new GraphAdapter();
        recyclerView.setAdapter(graphAdapter);
        recyclerView.setVisibility(View.INVISIBLE);
        return root;
    }

    class GraphAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view;
            switch (viewType) {
                default:
                    view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_entity, parent, false);
                    return new EntityViewHolder(view);
            }
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            switch(position){
                default:
                    EntityViewHolder h = (EntityViewHolder)holder;
                    name = h.name;
                    abstractInfo = h.abstractInfo;
                    image = h.image;
            }
        }

        @Override
        public int getItemViewType(int position) {
            return position;
        }

        @Override
        public int getItemCount() {
            return 1;
        }
    }

    class EntityViewHolder extends RecyclerView.ViewHolder {

        private TextView name, abstractInfo;
        private ImageView image;

        public EntityViewHolder(@NonNull View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.entity_name);
            abstractInfo = (TextView) itemView.findViewById(R.id.entity_abstract);
            image = (ImageView) itemView.findViewById(R.id.entity_image);
        }
    }
}