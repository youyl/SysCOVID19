package com.java.youyilin.ui.news;

public class NewsData
{
    private String source;
    private String date;
    private String title;
    private String id;
    private String type;
    private String urlll;
    NewsData(final String _title, final String _date, final String _source, final String _id,final String _type,final String _urlll)
    {
        urlll=_urlll;
        type=_type;
        source=_source;
        date=_date;
        title=_title;
        id=_id;
    }

    public String getUrl() {
        return urlll;
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
