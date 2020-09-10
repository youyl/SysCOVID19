package com.java.youyilin.ui.news;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
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
        Log.d("Get news Created",type);
        if(type.equals(new String("offline")))
        {
            lst=NewsDatabase.getInstance().getVisitedData(startPage);
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
                        String title=newsObj.getString("title");
                        String newsType=newsObj.getString("type");
                        String id=newsObj.getString("_id");
                        String date=newsObj.getString("time");
                        String source=new String();
                        if(!newsType.equals("event")) {
                            source = newsObj.getString("source");
                            if (source.isEmpty()&&newsType.equals("paper")) {
                                JSONObject authorObj = newsObj.getJSONArray("authors").getJSONObject(0);
                                source = authorObj.getString("name");
                            }
                        }
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
                        String title=newsObj.getString("title");
                        String newsType=newsObj.getString("type");
                        String id=newsObj.getString("_id");
                        String date=newsObj.getString("time");
                        String source=new String();
                        if(!newsType.equals("event")) {
                            source = newsObj.getString("source");
                            if (source.isEmpty()&&newsType.equals("paper")) {
                                JSONObject authorObj = newsObj.getJSONArray("authors").getJSONObject(0);
                                source = authorObj.getString("name");
                            }
                        }
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
        String str;
        try {
            String adr = new StringBuilder().append("https://covid-dashboard-api.aminer.cn/event/")
                    .append(id).toString();
            Log.d("Detail Adr Created",adr);
            URL url = new URL(adr);
            HttpURLConnection path=(HttpURLConnection) url.openConnection();
            path.setRequestMethod("GET");
            path.setConnectTimeout(2000);
            path.setReadTimeout(2000);
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
            String urlsss=smallObj.getJSONArray("urls").getString(0);
            Log.d("News Url catch Created",urlsss);
            str=smallObj.getString("content");
        }catch (Exception e){
            Log.d("DetailCrawler Create Error","Something Happend"); return null;}
        return str;
    }

    public String getNewsUrl(String id)
    {
        String str;
        try {
            String adr = new StringBuilder().append("https://covid-dashboard-api.aminer.cn/event/")
                    .append(id).toString();
            Log.d("Url try Adr Created",adr);
            URL url = new URL(adr);
            HttpURLConnection path=(HttpURLConnection) url.openConnection();
            path.setRequestMethod("GET");
            path.setConnectTimeout(2000);
            path.setReadTimeout(2000);
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
            str=smallObj.getJSONArray("urls").getString(0);
            Log.d("News Url catch Created",str);
        }catch (Exception e){
            Log.d("UrlCrawler Create Error","Something Happend"); return null;}
        return str;
    }
}
