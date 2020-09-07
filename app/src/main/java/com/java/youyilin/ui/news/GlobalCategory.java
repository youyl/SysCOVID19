package com.java.youyilin.ui.news;

import java.util.ArrayList;

public class GlobalCategory {
    private final static NewsCategory globalall=new NewsCategory("全部",0,"all");
    private final static NewsCategory globaloffline=new NewsCategory("离线",1,"offline");
    private final static NewsCategory globalnews=new NewsCategory("新闻",2,"news");
    private final static NewsCategory globalpaper=new NewsCategory("论文", 3, "paper");
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
        catList.add(globalall);
        catList.add(globaloffline);
        if(msk%2==1)
        {
            catList.add(globalnews);
        }
        if(msk>=2)
        {
            catList.add(globalpaper);
        }
    }
    public ArrayList<NewsCategory> getCatFrommsk(int msk)
    {
        ArrayList<NewsCategory>lst=new ArrayList<NewsCategory>();
        if(msk%2==1)
        {
            lst.add(globalnews);
        }
        if(msk>=2)
        {
            lst.add(globalpaper);
        }
        return lst;
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
