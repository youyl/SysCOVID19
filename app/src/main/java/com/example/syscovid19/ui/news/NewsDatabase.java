package com.example.syscovid19.ui.news;

import java.util.ArrayList;
import java.util.HashSet;

public class NewsDatabase
{
    private ArrayList<String>searchHistory;
    private HashSet<String>visitedNews;
    private ArrayList<NewsData>visitedData;

    private NewsDatabase()
    {
        searchHistory=new ArrayList<String>();
        visitedNews=new HashSet<String>();
        visitedData=new ArrayList<NewsData>();
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

    public void addData(final NewsData _data)
    {
        visitedData.add(0,_data);
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
