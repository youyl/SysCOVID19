package com.example.syscovid19.ui.news;

public class NewsData
{
    private String source;
    private String date;
    private String title;
    private String id;
    private String content;
    private String type;
    NewsData(final String _title, final String _date, final String _source, final String _id,final String _type)
    {
        type=_type;
        source=_source;
        date=_date;
        title=_title;
        id=_id;
    }
    public String getSource()
    {
        return source;
    }
    public String getDate()
    {
        return date;
    }
    public String getTitle()
    {
        return title;
    }
    public String getId()
    {
        return id;
    }
    public String getType()
    {
        return type;
    }
}
