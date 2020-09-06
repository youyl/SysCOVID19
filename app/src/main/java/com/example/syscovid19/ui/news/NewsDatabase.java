package com.example.syscovid19.ui.news;

import java.util.ArrayList;
import java.util.HashSet;

public class NewsDatabase
{
    private ArrayList<String>searchHistory;
    private HashSet<String>visitedNews;
    private ArrayList<NewsData>visitedData;
    private ArrayList<String>cachedDataName;
    private ArrayList<String>cachedDataDetail;
    private HashSet<String>cachedNews;

    private NewsDatabase()
    {
        searchHistory=new ArrayList<String>();
        visitedNews=new HashSet<String>();
        cachedNews=new HashSet<String>();
        visitedData=new ArrayList<NewsData>();
        cachedDataDetail=new ArrayList<String>();
        cachedDataName=new ArrayList<String>();
        //load from DB
    }
    private static NewsDatabase instance=new NewsDatabase();
    public static NewsDatabase getInstance()
    {
        return instance;
    }

    @Override
    protected void finalize() throws Throwable {
        //write back to DB
        super.finalize();
    }

    public boolean is_visited(final String id)
    {
        return visitedNews.contains(id);
    }

    public void addHistory(final String str)
    {
        if(searchHistory.size()==10)searchHistory.remove(9);
        searchHistory.add(0,str);
    }

    public boolean iscached(String _name)
    {
        return cachedNews.contains(_name);
    }

    public void addDetail(String _name,String _content)
    {
        if(iscached(_name))return;
        cachedNews.add(_name);
        cachedDataName.add(0,_name);
        cachedDataDetail.add(0,_content);
    }

    public void addData(final NewsData _data)
    {
        if(is_visited(_data.getId()))return;
        visitedData.add(0,_data);
    }

    public String findContent(String _id)
    {
        for (int i=0;i<cachedDataName.size();i++)
        {
            if(cachedDataName.get(i).equals(_id))
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

    public ArrayList<NewsData> getHistoryData(int startPage)
    {
        int start=(startPage-1)*10;
        int over=startPage*10;
        if(over>=visitedData.size())over=visitedData.size()-1;
        ArrayList<NewsData>lst=new ArrayList<NewsData>();
        for (int i=start;i<over;i++)
        {
            lst.add(visitedData.get(i));
        }
        return lst;
    }
}
