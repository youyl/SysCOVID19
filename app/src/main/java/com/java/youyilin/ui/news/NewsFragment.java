package com.java.youyilin.ui.news;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.java.youyilin.R;
import com.google.android.material.tabs.TabLayout;

public class NewsFragment extends Fragment {

    private TabLayout myTabLayout;
    private ViewPager myViewPager;
    private MyPageAdapter myPageAdapter;

    public void onCreate(Bundle savedInstanceState)
    {
        Log.d("Create","Create of News Fragment Happens!");
        super.onCreate(savedInstanceState);
        myPageAdapter=new MyPageAdapter(getChildFragmentManager());
    }
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        Log.d("CreateView","CreateView of News Fragment Happens!");
        View root = inflater.inflate(R.layout.fragment_news, container, false);
        myViewPager=(ViewPager)root.findViewById(R.id.pager_viewer);
        myViewPager.setOffscreenPageLimit(3);
        myTabLayout=(TabLayout)root.findViewById(R.id.tab_layout);
        myTabLayout.addTab(myTabLayout.newTab());
        myTabLayout.addTab(myTabLayout.newTab());
        myTabLayout.addTab(myTabLayout.newTab());
        myTabLayout.addTab(myTabLayout.newTab());
        myViewPager.setAdapter(myPageAdapter);
        myTabLayout.setupWithViewPager(myViewPager);
        ImageButton search_btn=(ImageButton)root.findViewById(R.id.search_button);
        ImageButton menu_btn=(ImageButton)root.findViewById(R.id.menu_button);
        search_btn.setOnClickListener(new View.OnClickListener()
                                      {
                                          @Override
                                          public void onClick(View view) {
                                              Intent intent=new Intent(getActivity(), SearchActivity.class);
                                              startActivityForResult(intent,0);
                                              getActivity().overridePendingTransition(R.anim.bottom_up_news,R.anim.bottom_stable_news);
                                          }
                                      }
        );
        menu_btn.setOnClickListener(new View.OnClickListener()
                                      {
                                          @Override
                                          public void onClick(View view) {
                                              Intent intent=new Intent(getActivity(), MenuActivity.class);
                                              startActivityForResult(intent,0);
                                              getActivity().overridePendingTransition(R.anim.bottom_up_news,R.anim.bottom_stable_news);
                                          }
                                      }
        );
        return root;
    }


    private class MyPageAdapter extends FragmentStatePagerAdapter
    {
        public MyPageAdapter(@NonNull FragmentManager fm)
        {
            super(fm);
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position)
        {
            return GlobalCategory.getInstance().getItem(position).getTitle();
        }

        @Override
        public Fragment getItem(int position)
        {
            return NewsList.newInstance(GlobalCategory.getInstance().getItem(position).getCode(),"test");
        }

        @Override
        public int getCount()
        {
            return GlobalCategory.getInstance().getSize();
        }
    }
}