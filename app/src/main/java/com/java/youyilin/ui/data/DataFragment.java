package com.java.youyilin.ui.data;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.java.youyilin.R;

public class DataFragment extends Fragment {

    private final String[] TITLES = new String[] {"国内疫情数据", "全球疫情数据"};
    private DataPagerAdapter pagerAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pagerAdapter = new DataPagerAdapter(getChildFragmentManager());
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_data, container, false);
        // Inflate the layout for this fragment
        ViewPager viewPager = (ViewPager) root.findViewById(R.id.view_pager);

        TabLayout tabLayout = (TabLayout) root.findViewById(R.id.tab_layout);
        for (int i = 0; i < TITLES.length; i++)
            tabLayout.addTab(tabLayout.newTab().setText(TITLES[i]));

        viewPager.setAdapter(pagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
        return root;
    }

    private class DataPagerAdapter extends FragmentStatePagerAdapter {

        public DataPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return new DataSubFragment(DataSubBackend.getInstance(position));
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return TITLES[position];
        }

        @Override
        public int getCount() {
            return TITLES.length;
        }

    }
}