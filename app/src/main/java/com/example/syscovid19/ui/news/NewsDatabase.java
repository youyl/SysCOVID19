package com.example.syscovid19.ui.news;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashSet;

public class NewsDatabase
{
    private ArrayList<String>searchHistory;
    private ArrayList<NewsData>cachedData;
    private ArrayList<String>cachedDataDetail;
    private HashSet<String>cachedNews;

    private NewsDatabase()
    {
        searchHistory=new ArrayList<String>();
        cachedNews=new HashSet<String>();
        cachedData=new ArrayList<NewsData>();
        cachedDataDetail=new ArrayList<String>();
        //load from DB: searchhistory cacheddetail cacheddata
        //load from cacheddata: cachednews
    }
    private static NewsDatabase instance=new NewsDatabase();
    public static NewsDatabase getInstance()
    {
        return instance;
    }

    @Override
    protected void finalize() throws Throwable {
        //write back to DB: searchhistory cacheddetail cacheddata
        super.finalize();
    }


    public void addHistory(final String str)
    {
        if(searchHistory.size()==10)searchHistory.remove(9);
        searchHistory.add(0,str);
    }

    public boolean iscached(String _name)
    {
        Log.d("Cache Request Created",String.valueOf(cachedNews.size()));
        return cachedNews.contains(_name);
    }

    public void addDetail(String _name,String _content,NewsData _data)
    {
        if(iscached(_name))return;
        cachedNews.add(_name);
        cachedDataDetail.add(0,_content);
        cachedData.add(0,_data);
    }

    public String findContent(String _id)
    {
        for (int i=0;i<cachedData.size();i++)
        {
            if(cachedData.get(i).getId().equals(_id))
            {
                return cachedDataDetail.get(i);
            }
        }
        return "";
    }

    public ArrayList<String> getHistory()
    {
        return searchHistory;
    }

    public ArrayList<NewsData> getVisitedData(int startPage)
    {
        int start=(startPage-1)*10;
        int over=startPage*10;
        if(over>cachedData.size())over=cachedData.size();
        Log.d("Visited Data Query Created",String.valueOf(startPage));
        ArrayList<NewsData>lst=new ArrayList<NewsData>();
        for (int i=start;i<over;i++)
        {
            lst.add(cachedData.get(i));
        }
        return lst;
    }
}
