package com.example.syscovid19.ui.news;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;



public class NewsCrawler
{
    private static NewsCrawler instance=new NewsCrawler();
    private NewsCrawler()
    {
        //init database
    }
    public static NewsCrawler getInstance()
    {
        return instance;
    }

    //pagesize=10
    public ArrayList<NewsData> getNews(final String type,final String keyword, int startPage)
    {
        ArrayList<NewsData>lst=new ArrayList<NewsData>();
        if(type.equals(new String("offline")))
        {
            //crawl from database
        }
        else
        {
            if(type.equals(new String("search")))
            {
                try {
                    String adr = new StringBuilder().append("https://covid-dashboard.aminer.cn/api/events/list?")
                            .append("type=").append(type).append("&page=")
                            .append(new Integer(startPage).toString()).append("&size=200").toString();
                    URL url = new URL(adr);
                    HttpURLConnection path=(HttpURLConnection) url.openConnection();
                    path.setRequestMethod("GET");
                    path.setConnectTimeout(5000);
                    path.setReadTimeout(5000);
                    path.connect();

                    InputStream inputStream = path.getInputStream();
                    InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                    StringBuilder jsonStr = new StringBuilder();
                    String inputLine;
                    while ((inputLine = bufferedReader.readLine()) != null)
                    {
                        jsonStr.append(inputLine);
                    }
                    bufferedReader.close();
                    inputStreamReader.close();
                    inputStream.close();
                    inputLine=jsonStr.toString();

                    JSONObject largeObj=new JSONObject(inputLine);
                    JSONArray data=largeObj.getJSONArray("data");
                    for (int i=0;i<data.length();i++)
                    {
                        JSONObject newsObj=data.getJSONObject(i);
                        String newsType=GlobalCategory.decodeUnicode(newsObj.getString("type"));
                        String id=GlobalCategory.decodeUnicode(newsObj.getString("_id"));
                        String title=GlobalCategory.decodeUnicode(newsObj.getString("title"));
                        String source=GlobalCategory.decodeUnicode(newsObj.getString("source"));
                        String date=GlobalCategory.decodeUnicode(newsObj.getString("time"));
                        if(title.contains(keyword)) {
                            lst.add(new NewsData(title, date, source, id, newsType));
                        }
                    }

                }catch (Exception e){
                    Log.d("Searcher Create Error","Error Created");}
            }
            else
            {
                try {
                    String adr = new StringBuilder().append("https://covid-dashboard.aminer.cn/api/events/list?")
                            .append("type=").append(type).append("&page=")
                            .append(new Integer(startPage).toString()).append("&size=10").toString();
                    Log.d("Addr Created:",adr);
                    URL url = new URL(adr);
                    HttpURLConnection path=(HttpURLConnection) url.openConnection();
                    path.setRequestMethod("GET");
                    path.setConnectTimeout(5000);
                    path.setReadTimeout(5000);
                    path.connect();

                    InputStream inputStream = path.getInputStream();
                    InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                    StringBuilder jsonStr = new StringBuilder();
                    String inputLine;
                    while ((inputLine = bufferedReader.readLine()) != null)
                    {
                        jsonStr.append(inputLine);
                    }
                    bufferedReader.close();
                    inputStreamReader.close();
                    inputStream.close();
                    inputLine=jsonStr.toString();

                    JSONObject largeObj=new JSONObject(inputLine);
                    JSONArray data=largeObj.getJSONArray("data");
                    for (int i=0;i<data.length();i++)
                    {
                        JSONObject newsObj=data.getJSONObject(i);
                        String newsType=GlobalCategory.decodeUnicode(newsObj.getString("type"));
                        String id=GlobalCategory.decodeUnicode(newsObj.getString("_id"));
                        String title=GlobalCategory.decodeUnicode(newsObj.getString("title"));
                        String source=GlobalCategory.decodeUnicode(newsObj.getString("source"));
                        String date=GlobalCategory.decodeUnicode(newsObj.getString("time"));
                        lst.add(new NewsData(title,date,source,id,newsType));
                    }

                }catch (Exception e){
                    Log.d("Crawler Create Error","Error Created");}
            }
        }
        return lst;
    }

    public String getNewsDetail(String id)
    {
        String str=new String();
        try {
            String adr = new StringBuilder().append("https://covid-dashboard-api.aminer.cn/event/")
                    .append(id).toString();
            URL url = new URL(adr);
            HttpURLConnection path=(HttpURLConnection) url.openConnection();
            path.setRequestMethod("GET");
            path.setConnectTimeout(5000);
            path.setReadTimeout(5000);
            path.connect();

            InputStream inputStream = path.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            StringBuilder jsonStr = new StringBuilder();
            String inputLine;
            while ((inputLine = bufferedReader.readLine()) != null)
            {
                jsonStr.append(inputLine);
            }
            bufferedReader.close();
            inputStreamReader.close();
            inputStream.close();
            inputLine=jsonStr.toString();

            JSONObject largeObj=new JSONObject(inputLine);
            JSONObject smallObj=largeObj.getJSONObject("data");
            str=GlobalCategory.decodeUnicode(smallObj.getString("content"));
        }catch (Exception e){
            Log.d("Crawler Create Error","Detailed Error Created");}
        return str;
    }
}
