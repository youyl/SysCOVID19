package com.java.youyilin.ui.scholar;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.java.youyilin.R;
import com.java.youyilin.ui.graph.GraphBackend;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import io.reactivex.functions.Consumer;

public class ScholarSubFragment extends Fragment {
    private ScholarAdapter adapter;
    private List<Scholar> scholarList;
    private int mode;

    ScholarSubFragment(int m){
        if (m == 0)
            scholarList = ScholarSubBackend.getInstance().scholarLiveList;
        else
            scholarList = ScholarSubBackend.getInstance().scholarDeadList;
        mode = m;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_sub_scholar, container, false);
        // Inflate the layout for this fragment
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        RecyclerView recyclerView = (RecyclerView) root.findViewById(R.id.recycler_scholar);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new ScholarAdapter();
        recyclerView.setAdapter(adapter);
        return root;
    }

    class ScholarAdapter extends RecyclerView.Adapter{

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_scholar, parent, false);
            return new ScholarViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
            final ScholarViewHolder h = (ScholarViewHolder)holder;
            final Scholar scholar = scholarList.get(position);
            h.scholarButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), ScholarDetail.class);
                    intent.putExtra("id", position);
                    intent.putExtra("mode", mode);
                    startActivity(intent);
                }
            });
            h.scholarName.setText(scholar.name);
            h.hindex.setText(String.valueOf(scholar.hindex));
            h.activity.setText(String.format("%.2f", scholar.activity));
            h.sociability.setText(String.format("%.2f", scholar.sociability));
            h.citations.setText(String.valueOf(scholar.citations));
            h.pubs.setText(String.valueOf(scholar.pubs));
            if (scholar.position != null && scholar.position.length() > 0)
                h.position.setText(scholar.position);
            else
                h.layoutPosition.setVisibility(View.GONE);
            h.affiliation.setText(scholar.affiliation);
            Bitmap bitmap = getImageFromAssetsFile(scholar.id + ".jpg", getContext());
            if (bitmap != null) {
                h.scholarImage.setImageBitmap(bitmap);
                return;
            }
            GraphBackend.getBitmapFromURL(scholar.avatar).subscribe(new Consumer<Bitmap>() {
                @Override
                public void accept(Bitmap bitmap) throws Exception {
                    if (bitmap != null)
                        h.scholarImage.setImageBitmap(bitmap);
                    else
                        h.scholarImage.setVisibility(View.INVISIBLE);
                }
            });
        }

        @Override
        public int getItemCount() {
            return scholarList.size();
        }
    }

    public static Bitmap getImageFromAssetsFile(String fileName, Context context)
    {
        Bitmap image = null;
        AssetManager am = context.getResources().getAssets();
        try {
            InputStream is = am.open(fileName);
            image = BitmapFactory.decodeStream(is);
            is.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return image;
    }

    class ScholarViewHolder extends RecyclerView.ViewHolder {

        private TextView scholarName, hindex, activity, sociability, citations, pubs, position, affiliation;
        private ImageView scholarImage;
        private Button scholarButton;
        private View layoutPosition;

        public ScholarViewHolder(@NonNull View itemView) {
            super(itemView);
            scholarImage = (ImageView) itemView.findViewById(R.id.scholar_image);
            scholarName = (TextView) itemView.findViewById(R.id.scholar_name);
            scholarButton = (Button) itemView.findViewById(R.id.scholar_button);
            hindex = (TextView) itemView.findViewById(R.id.scholar_hindex);
            activity = (TextView) itemView.findViewById(R.id.scholar_activity);
            sociability = (TextView) itemView.findViewById(R.id.scholar_sociability);
            citations = (TextView) itemView.findViewById(R.id.scholar_citations);
            pubs = (TextView) itemView.findViewById(R.id.scholar_pubs);
            position = (TextView) itemView.findViewById(R.id.scholar_position);
            affiliation = (TextView) itemView.findViewById(R.id.scholar_affiliation);
            layoutPosition = itemView.findViewById(R.id.layout_position);
        }
    }
}
