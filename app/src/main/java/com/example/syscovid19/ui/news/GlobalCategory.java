package com.example.syscovid19.ui.news;

import com.example.syscovid19.MainActivity;

import java.util.ArrayList;

public class GlobalCategory {
    private ArrayList<NewsCategory>catList;
    private int catVal;
    private static GlobalCategory instance=new GlobalCategory();
    private GlobalCategory()
    {
        catVal=3;
        this.setCat(3);
    }
    public static GlobalCategory getInstance()
    {
        return instance;
    }
    public void setCat(int msk)
    {
        catVal=msk;
        catList=new ArrayList<NewsCategory>();
        catList.add(new NewsCategory("全部",0,"all"));
        if(msk%2==1)
        {
            catList.add(new NewsCategory("新闻",1,"news"));
        }
        if(msk>=2)
        {
            catList.add(new NewsCategory("论文", 2, "paper"));
        }
    }
    public int getSize()
    {
        return catList.size();
    }
    public int getCatVal()
    {
        return catVal;
    }
    public NewsCategory getItem(int position)
    {
        return catList.get(position);
    }
}
