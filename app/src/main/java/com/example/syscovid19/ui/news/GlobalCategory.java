package com.example.syscovid19.ui.news;

import com.example.syscovid19.MainActivity;

import java.util.ArrayList;

public class GlobalCategory {
    private ArrayList<NewsCategory>catList;
    private int catVal;
    private static GlobalCategory instance=new GlobalCategory();
    private GlobalCategory()
    {
        catVal=3;
        this.setCat(3);
    }
    public static GlobalCategory getInstance()
    {
        return instance;
    }
    public void setCat(int msk)
    {
        catVal=msk;
        catList=new ArrayList<NewsCategory>();
        catList.add(new NewsCategory("全部",0,"all"));
        catList.add(new NewsCategory("离线",1,"offline"));
        if(msk%2==1)
        {
            catList.add(new NewsCategory("新闻",2,"news"));
        }
        if(msk>=2)
        {
            catList.add(new NewsCategory("论文", 3, "paper"));
        }
    }
    public int getSize()
    {
        return catList.size();
    }
    public int getCatVal()
    {
        return catVal;
    }
    public NewsCategory getItem(int position)
    {
        return catList.get(position);
    }

    //Unicode String to Chinese Characters
    //from:https://blog.csdn.net/zimo2013/article/details/40780439
    public static String decodeUnicode(String unicodeStr)
    {
        if(unicodeStr == null)
        {
            return null;
        }
        StringBuilder retBuf = new StringBuilder();
        int maxLoop = unicodeStr.length();
        for (int i = 0; i < maxLoop; i++)
        {
            if (unicodeStr.charAt(i) == '\\')
            {
                if ((i < maxLoop - 5) && ((unicodeStr.charAt(i + 1) == 'u') || (unicodeStr.charAt(i + 1) == 'U')))
                {
                    try {
                        retBuf.append((char) Integer.parseInt(unicodeStr.substring(i + 2, i + 6), 16));
                        i += 5;
                    } catch (NumberFormatException localNumberFormatException) {
                        retBuf.append(unicodeStr.charAt(i));
                    }
                }
                else
                {
                    retBuf.append(unicodeStr.charAt(i));
                }
            }
            else
            {
                retBuf.append(unicodeStr.charAt(i));
            }
        }
        return retBuf.toString();
    }
}
