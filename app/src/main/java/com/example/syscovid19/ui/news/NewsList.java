package com.example.syscovid19.ui.news;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.syscovid19.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link NewsList#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NewsList extends Fragment {

    // TODO: Rename and change types of parameters
    private String myTitle;
    private String myCode;

    public NewsList() {}

    // TODO: Rename and change types and number of parameters
    public static NewsList newInstance(String param1, String param2)
    {
        NewsList fragment = new NewsList();
        Bundle args = new Bundle();
        args.putString("Title", param1);
        args.putString("Code", param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Log.d("List Create","A List Created !");
        if (getArguments() != null) {
            myTitle = getArguments().getString("Title");
            myCode = getArguments().getString("Code");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d("List CreateView","A List CreateViewed");
        // Inflate the layout for this fragment
        View root=inflater.inflate(R.layout.fragment_news_list, container, false);
        TextView txt=root.findViewById(R.id.test_text);
        txt.setText(new String(myTitle));
        return root;
    }
}