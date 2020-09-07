package com.java.youyilin.ui.news;

public class NewsCategory
{
    private String title;
    private int idx;
    private String code;
    NewsCategory(final String _title, final int _idx,final String _code)
    {
        title=new String(_title);
        idx=_idx;
        code=new String(_code);
    }

    public String getTitle()
    {
        return title;
    }

    public int getIdx()
    {
        return idx;
    }

    public String getCode()
    {
        return code;
    }
}
