package com.lgq.rssreader.util;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.lgq.rssreader.R;
import com.lgq.rssreader.core.AppSettings;
import com.lgq.rssreader.core.ReaderApp;
import com.lgq.rssreader.model.Channel;
import com.lgq.rssreader.model.Profile;
import com.lgq.rssreader.model.Style;
import com.lgq.rssreader.model.serializer.ReadSettingsDeserializer;
import com.lgq.rssreader.model.serializer.ReadSettingsSerializer;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by redel on 2015-09-28.
 */
public class PreferencesUtil {
    private static HashMap<Style, Integer> style2Theme = new HashMap<Style, Integer>(){{
        put(Style.Black, R.style.BlackTheme);
        put(Style.Dark, R.style.DarkTheme);
        put(Style.Gray, R.style.GrayTheme);
        put(Style.Green, R.style.GreenTheme);
        put(Style.White, R.style.WhiteTheme);
    }};

    private static HashMap<Integer, Style> theme2Style = new HashMap<Integer, Style>(){{
        put(R.style.BlackTheme, Style.Black);
        put(R.style.DarkTheme, Style.Dark);
        put(R.style.GrayTheme, Style.Gray);
        put(R.style.GreenTheme, Style.Green);
        put(R.style.WhiteTheme, Style.White);
    }};

    public static void saveChannels(List<Channel> channels){
        Gson gson = new Gson();

        StringBuilder sb = new StringBuilder();

        for(Channel c : channels){
            sb.append(gson.toJson(c) + "____");
        }

        ReaderApp.getContext().getSharedPreferences("RssReader", 0).edit().putString("Channel", sb.toString()).commit();
    }

    public static Channel findParentChannel(Channel channel){
        if(channel.getTagId() == null)
            return null;

        for(Channel c : getChannels()){
            if(c.getChannelId().equals(channel.getTagId())){
                return c;
            }
        }

        return null;
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

    public static int getTheme(){
        return style2Theme.get(getAppSettings().getStyle());
    }

    public static Style getStyle(Context context){
        return theme2Style.get(context.getTheme());
    }

    public static AppSettings getAppSettings(){
        final GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(AppSettings.class, new ReadSettingsDeserializer());
        final Gson gson = gsonBuilder.create();
        if( ReaderApp.getContext().getSharedPreferences("RssReader", 0).contains("AppSettings")){
            String json = ReaderApp.getContext().getSharedPreferences("RssReader", 0).getString("AppSettings", "");
            Type type = new TypeToken<AppSettings>(){}.getType();
            return gson.fromJson(json, type);
        }
        return new AppSettings();
    }

    public static void saveAppSettings(AppSettings readSettings){
        final GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(AppSettings.class, new ReadSettingsSerializer());
        final Gson gson = gsonBuilder.create();
        Type type = new TypeToken<AppSettings>(){}.getType();
        ReaderApp.getContext().getSharedPreferences("RssReader", 0).edit().putString("AppSettings", gson.toJson(readSettings,type)).commit();
    }

    public static void saveAccessToken(String token){
        ReaderApp.getContext().getSharedPreferences("RssReader", 0).edit().putString("access_token", token).commit();
    }

    public static String getAccessToken(){
        return ReaderApp.getContext().getSharedPreferences("RssReader", 0).getString("access_token","");
    }

    public static void saveLastSyncTime(long lastSyncTime){
        ReaderApp.getContext().getSharedPreferences("RssReader", 0).edit().putLong("lastSyncTime", lastSyncTime).commit();
    }

    public static long getLastSyncTime(){
        return ReaderApp.getContext().getSharedPreferences("RssReader", 0).getLong("lastSyncTime",0);
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