package com.lgq.rssreader.util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

/**
 * Created by redel on 2015-10-03.
 */
public class UrlUtil {

    public static String findValueInUrl(String url, String key){
        String[] params = url.split("&");

        String value = "";

        for(String p : params){
            if(p.contains(key)){
                try {
                    value = URLDecoder.decode(p.split("=")[1], "utf-8");
                } catch (UnsupportedEncodingException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }

        return value;
    }

    public static String findValueInConetent(String content, String spliter, String splitValue, String key){
        String[] params = content.split(spliter);

        String value = "";

        for(String p : params){
            if(p.contains(key)){
                try {
                    String[] parts = p.split(splitValue);
                    value = parts[parts.length - 1].replace("'","").replace(" ","");
                    if(value.startsWith("//"))
                        value = "http:" + value;
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }

        return value;
    }
}
