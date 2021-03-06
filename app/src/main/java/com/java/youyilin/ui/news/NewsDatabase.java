package com.java.youyilin.ui.news;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;
import java.util.concurrent.Callable;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class NewsDatabase
{
    private String pathNews;
    private String pathSearch;
    private ArrayList<String>searchHistory;
    private ArrayList<NewsData>cachedData;
    private ArrayList<String>cachedDataDetail;
    private HashSet<String>cachedNews;
    NewsList offlineView;
    HashMap<String,String> contentMap;

    private NewsDatabase()
    {
        contentMap=new HashMap<String, String>();
        offlineView=null;
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

    public void setOfflineView(NewsList offlineView) {
        this.offlineView = offlineView;
    }

    public Single<Boolean> loadData(final Context _context)
    {
        return Single.fromCallable(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
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
                        String urlll=newsObj.getString("url");
                        cachedNews.add(id);
                        contentMap.put(id,newscontent);
                        cachedData.add(new NewsData(title,date,source,id,newsType,urlll));
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
                return true;
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
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
                smallObj.put("url",cachedData.get(i).getUrl());
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
        for (int i=0;i<searchHistory.size();i++)
        {
            if(searchHistory.get(i).equals(str))
            {
                searchHistory.remove(i);
                break;
            }
        }
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
        contentMap.put(_name,_content);
        cachedData.add(0,_data);
    }
    public void refreshOfflineView() {
        if (offlineView != null) offlineView.refresh();
    }

    public String findContent(String _id)
    {
        if(contentMap.containsKey(_id))return contentMap.get(_id);
        else return "";
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
