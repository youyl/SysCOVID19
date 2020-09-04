package com.example.syscovid19.ui.data;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

public class DataSubFragment extends Fragment {
    public static DataSubFragment newInstance(int category) {
        Bundle args = new Bundle();
        DataSubFragment fragment = new DataSubFragment();
        args.putInt("category", category);
        fragment.setArguments(args);
        return fragment;
    }
}
