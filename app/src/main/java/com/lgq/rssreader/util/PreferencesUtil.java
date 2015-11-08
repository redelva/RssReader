package com.lgq.rssreader.util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.lgq.rssreader.core.ReaderApp;
import com.lgq.rssreader.model.Channel;
import com.lgq.rssreader.model.Profile;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by redel on 2015-09-28.
 */
public class PreferencesUtil {
    public static void saveChannels(List<Channel> channels){
        Gson gson = new Gson();

        StringBuilder sb = new StringBuilder();

        for(Channel c : channels){
            sb.append(gson.toJson(c) + "____");
        }

        ReaderApp.getContext().getSharedPreferences("RssReader", 0).edit().putString("Channel", sb.toString()).commit();
    }

    public static List<Channel> getChannels(){
        Gson gson = new Gson();
        String json = ReaderApp.getContext().getSharedPreferences("RssReader", 0).getString("Channel", "");
        String[] data = json.split("____");

        List<Channel> channels = new ArrayList<Channel>();

        for(String obj : data){
            Channel c = gson.fromJson(obj, Channel.class);
            if(c != null)
                channels.add(c);
        }

        return channels;
    }

    public static void saveAccessToken(String token){
        ReaderApp.getContext().getSharedPreferences("RssReader", 0).edit().putString("access_token", token).commit();
    }

    public static String getAccessToken(){
        return ReaderApp.getContext().getSharedPreferences("RssReader", 0).getString("access_token","");
    }

    public static void saveRefreshToken(String token){
        ReaderApp.getContext().getSharedPreferences("RssReader", 0).edit().putString("refresh_token", token).commit();
    }

    public static String getRefreshToken(){
        return ReaderApp.getContext().getSharedPreferences("RssReader", 0).getString("refresh_token","");
    }

    public static void saveProfile(Profile profile){
        String content = new Gson().toJson(profile);
        ReaderApp.getContext().getSharedPreferences("RssReader", 0).edit().putString("profile", content).commit();
    }

    public static Profile getProfile(){
        String content = ReaderApp.getContext().getSharedPreferences("RssReader", 0).getString("profile","");
        return new Gson().fromJson(content, Profile.class);
    }
}
