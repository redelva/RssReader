package com.lgq.rssreader.abstraction;

import com.lgq.rssreader.util.PreferencesUtil;

import org.json.JSONObject;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;

public class RssHttpClient{

    private static String token;

    public static void setToken(String token){
        RssHttpClient.token = token;
    }

    private static String refresh(String refreshToken){
        try{
            String url = "http://feedly.com/v3/auth/token?ct=feedly.desktop&cv=29.0.1029&ck=" + System.currentTimeMillis();

            HashMap<String, String> headers = new HashMap<String, String>();
            headers.put("Host", "cloud.feedly.com");
            headers.put("Accept-Charset", "utf8");
            headers.put("Referer", "http://cloud.feedly.com/");
            headers.put("Content-Type", "application/json");

            String content = "{\"refresh_token\":\"" + refreshToken + "\",\"client_id\":\"feedly\",\"client_secret\":\"0XP4XQ07VVMDWBKUHTJM4WUQ\",\"grant_type\":\"refresh_token\"}";

            String message = HttpClient.post(url, headers, content);
            JSONObject response = new JSONObject(message.toString());
            if (response.has("access_token")) {
                return response.getString("access_token");
            } else {
                return "";
            }
        }
        catch (Exception e){
            return "";
        }
    }

    public static String tudou(final String url)  {
        try{
            URLConnection client = new URL(url).openConnection();
            client.addRequestProperty("User-Agent", "Mozilla/5.0 (Linux; Android 4.4.4; en-us; Nexus 5 Build/JOP40D) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/42.0.2307.2 Mobile Safari/537.36");
            //client.addRequestProperty("host", "www.tudou.com");
            // 得到HttpURLConnection对象
            HttpURLConnection connection = (HttpURLConnection) client;
            // 设置为GET方式
            connection.setRequestMethod("GET");
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                return connection.getURL().toString();
            }
            else{
                return null;
            }
        }
        catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public static String youku(final String url) {


        try{
            URLConnection client = new URL(url).openConnection();
            client.addRequestProperty("User-Agent", "Mozilla/5.0 (Linux; Android 4.4.4; en-us; Nexus 5 Build/JOP40D) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/42.0.2307.2 Mobile Safari/537.36");
            client.addRequestProperty("host", "k.youku.com");
            // 得到HttpURLConnection对象
            HttpURLConnection connection = (HttpURLConnection) client;
            // 设置为GET方式
            connection.setRequestMethod("GET");
            if (connection.getResponseCode() == HttpURLConnection.HTTP_MOVED_TEMP) {
                return connection.getHeaderField("location");
            }
            else{
                return null;
            }
        }
        catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public static String get(final String url) {
        try {
            HashMap<String, String> headers = new HashMap<String, String>();
            headers.put("Host", "cloud.feedly.com");
            headers.put("Accept-Charset", "utf8");
            headers.put("Referer", "http://cloud.feedly.com/");
            headers.put("Authorization", "OAuth " + token);
            headers.put("$Authorization.feedly", "true");
            headers.put("Content-Type", "application/json");

            String message = HttpClient.get(url, headers);

            if(message.equals(String.valueOf(HttpURLConnection.HTTP_UNAUTHORIZED))){
                String newToken = refresh(PreferencesUtil.getRefreshToken());
                PreferencesUtil.saveAccessToken(newToken);
                setToken(newToken);
                return get(url);
            }

            if (message != null) {
                return message;
            } else {
                return "";
            }
        }
        catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public static boolean delete(String url) {

        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Host", "cloud.feedly.com");
        headers.put("Accept-Charset", "utf8");
        headers.put("Referer", "http://cloud.feedly.com/");
        headers.put("Authorization", token);
        headers.put("$Authorization.feedly", "true");
        headers.put("Content-Type", "application/json");
        headers.put("Content-Length", "0");

        return HttpClient.delete(url, headers);
    }

    public static String post(String url) {
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Host", "cloud.feedly.com");
        headers.put("Accept-Charset", "utf8");
        headers.put("Referer", "http://cloud.feedly.com/");
        headers.put("Authorization", token);
        headers.put("$Authorization.feedly", "true");
        headers.put("Content-Type", "application/json");

        return HttpClient.post(url, headers);
    }

    public static boolean post(String url, String content) {
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Host", "cloud.feedly.com");
        headers.put("Accept-Charset", "utf8");
        headers.put("Referer", "http://cloud.feedly.com/");
        headers.put("Authorization", token);
        headers.put("$Authorization.feedly", "true");
        headers.put("Content-Type", "application/json");

        return HttpClient.post(url, headers, content) != null;
    }

    public static boolean put(String url, String content) {
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Host", "cloud.feedly.com");
        headers.put("Accept-Charset", "utf8");
        headers.put("Referer", "http://cloud.feedly.com/");
        headers.put("Authorization", token);
        headers.put("$Authorization.feedly", "true");
        headers.put("Content-Type", "application/json");

        return HttpClient.put(url, headers, content);
    }
}
