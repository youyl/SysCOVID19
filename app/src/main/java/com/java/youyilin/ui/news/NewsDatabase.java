package com.java.youyilin.ui.news;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;

public class NewsDatabase
{
    private String pathNews;
    private String pathSearch;
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
    }
    private static NewsDatabase instance=new NewsDatabase();
    public static NewsDatabase getInstance()
    {
        return instance;
    }

    public void loadData(Context _context)
    {
        pathNews=new StringBuilder().append(_context.getFilesDir().getPath()).append("/newsDataBase.json").toString();
        pathSearch=new StringBuilder().append(_context.getFilesDir().getPath()).append("/searchDataBase.json").toString();
        Log.d("DB Load Created",pathNews);
        try
        {
            //getnewsjson
            Scanner fin=new Scanner(new FileInputStream(pathNews));
            StringBuilder content=new StringBuilder();
            while (fin.hasNextLine())
            {
                content.append(fin.nextLine());
            }
            fin.close();
            JSONArray dataObj=new JSONObject(content.toString()).getJSONArray("data");
            //json2array
            for (int i=0;i<dataObj.length();i++)
            {
                JSONObject newsObj=dataObj.getJSONObject(i);
                String title=newsObj.getString("title");
                String newsType=newsObj.getString("type");
                String id=newsObj.getString("id");
                String source=newsObj.getString("source");
                String date=newsObj.getString("date");
                String newscontent=newsObj.getString("content");
                cachedNews.add(id);
                cachedData.add(new NewsData(title,date,source,id,newsType));
                cachedDataDetail.add(newscontent);
            }
        }
        catch (Exception e)
        {
            Log.d("NewsDB Load Error Created","Created!");
        }
        try
        {
            //getsearchjson
            Scanner fin=new Scanner(new FileInputStream(pathSearch));
            StringBuilder content=new StringBuilder();
            while (fin.hasNextLine())
            {
                content.append(fin.nextLine());
            }
            fin.close();
            JSONArray dataObj=new JSONObject(content.toString()).getJSONArray("data");
            //json2array
            for (int i=0;i<dataObj.length();i++)
            {
                JSONObject newsObj=dataObj.getJSONObject(i);
                String title=newsObj.getString("content");
                searchHistory.add(title);
            }
        }
        catch (Exception e)
        {
            Log.d("SearchDB Load Error Created","Created!");
        }
    }

    public void writeDB() {
        Log.d("DB Write Created",pathNews);
        try
        {
            JSONObject obj=new JSONObject();
            JSONArray dataarray=new JSONArray();
            for (int i=0;i<cachedData.size();i++)
            {
                JSONObject smallObj=new JSONObject();
                smallObj.put("title",cachedData.get(i).getTitle());
                smallObj.put("id",cachedData.get(i).getId());
                smallObj.put("source",cachedData.get(i).getSource());
                smallObj.put("type",cachedData.get(i).getType());
                smallObj.put("date",cachedData.get(i).getDate());
                smallObj.put("content",cachedDataDetail.get(i));
                dataarray.put(i,smallObj);
            }
            obj.put("data",dataarray);
            //write
            OutputStreamWriter fout=new OutputStreamWriter(new FileOutputStream(pathNews));
            fout.write(obj.toString());
            fout.close();
        }
        catch (Exception e)
        {
            Log.d("NewsDB Write Error Created","Created!");
        }
        try
        {
            JSONObject obj=new JSONObject();
            JSONArray dataarray=new JSONArray();
            for (int i=0;i<searchHistory.size();i++)
            {
                JSONObject smallObj=new JSONObject();
                smallObj.put("content",searchHistory.get(i));
                dataarray.put(i,smallObj);
            }
            obj.put("data",dataarray);
            //write
            OutputStreamWriter fout=new OutputStreamWriter(new FileOutputStream(pathSearch));
            fout.write(obj.toString());
            fout.close();
        }
        catch (Exception e)
        {
            Log.d("SearchDB Write Error Created","Created!");
        }
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
