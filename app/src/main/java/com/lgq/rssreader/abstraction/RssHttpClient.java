package com.lgq.rssreader.abstraction;

import com.lgq.rssreader.util.PreferencesUtil;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

public class RssHttpClient{

    private static String mToken;

    public static void setToken(String token){
        mToken = token;
    }

    private static URLConnection setup(String url) throws IOException{
        URLConnection client = new URL(url).openConnection();
        client.addRequestProperty("Host", "cloud.feedly.com");
        client.addRequestProperty("Accept-Charset", "utf8");
        client.addRequestProperty("Referer", "http://cloud.feedly.com/");
        client.addRequestProperty("Authorization", mToken);

        return client;
    }

    private static String refresh(String refreshToken){
        try{
            String url = "http://feedly.com/v3/auth/token?ct=feedly.desktop&cv=29.0.1029&ck=" + System.currentTimeMillis();
            URL refreshUrl = new URL(url);

            HttpURLConnection urlConnection = (HttpURLConnection) refreshUrl.openConnection();
            try {
                urlConnection.setDoOutput(true);
                urlConnection.setChunkedStreamingMode(0);

                urlConnection.setRequestProperty("Host", "cloud.feedly.com");
                urlConnection.setRequestProperty("Accept-Charset", "utf8");
                urlConnection.setRequestProperty("Referer", "http://cloud.feedly.com/");
                urlConnection.setRequestProperty("Content-Type", "application/json");
                // Post 请求不能使用缓存
                urlConnection.setUseCaches(false);
                urlConnection.setInstanceFollowRedirects(true);
                urlConnection.setDoOutput(true);
                urlConnection.setRequestMethod("POST");

                String content = "{\"refresh_token\":\"" + refreshToken + "\",\"client_id\":\"feedly\",\"client_secret\":\"0XP4XQ07VVMDWBKUHTJM4WUQ\",\"grant_type\":\"refresh_token\"}";
                DataOutputStream out = new DataOutputStream(urlConnection.getOutputStream());
                out.writeBytes(content);

                out.flush();
                out.close();

                if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    // 得到响应消息
                    InputStreamReader in = new InputStreamReader(urlConnection.getInputStream());
                    // 为输出创建BufferedReader
                    BufferedReader buffer = new BufferedReader(in);
                    String inputLine = null;
                    StringBuffer message = new StringBuffer();
                    //使用循环来读取获得的数据
                    while (((inputLine = buffer.readLine()) != null)) {
                        message.append(inputLine);
                    }
                    //关闭InputStreamReader
                    in.close();

                    JSONObject response = new JSONObject(message.toString());
                    if (response.has("access_token")) {
                        return "OAuth " + response.getString("access_token");
                    } else {
                        return "";
                    }
                }
                else{
                    return "";
                }
            }
            finally {
                urlConnection.disconnect();
            }
        }
        catch (Exception e){
            return "";
        }
    }

    public static String get(final String url) {
        try{
            URLConnection client = setup(url);
            // 得到HttpURLConnection对象
            HttpURLConnection connection = (HttpURLConnection) client;
            // 设置为GET方式
            connection.setRequestMethod("GET");
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                // 得到响应消息
                InputStreamReader in = new InputStreamReader(connection.getInputStream());
                // 为输出创建BufferedReader
                BufferedReader buffer = new BufferedReader(in);
                String inputLine = null;
                StringBuffer message = new StringBuffer();
                //使用循环来读取获得的数据
                while (((inputLine = buffer.readLine()) != null)) {
                    message.append(inputLine);
                }
                //关闭InputStreamReader
                in.close();
                return message.toString();
            }
            else if(connection.getResponseCode() == HttpURLConnection.HTTP_UNAUTHORIZED){
                connection.disconnect();
                String newToken = refresh(PreferencesUtil.getRefreshToken());
                if(newToken != null && newToken.length() > 0){
                    setToken(newToken);
                    PreferencesUtil.saveAccessToken(newToken);
                    return get(url);
                }else{
                    return null;
                }
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

    public static String cleanget(final String url) {
        try{
            URLConnection client = new URL(url).openConnection();
            // 得到HttpURLConnection对象
            HttpURLConnection connection = (HttpURLConnection) client;
            // 设置为GET方式
            connection.setRequestMethod("GET");
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                // 得到响应消息
                InputStreamReader in = new InputStreamReader(connection.getInputStream());
                // 为输出创建BufferedReader
                BufferedReader buffer = new BufferedReader(in);
                String inputLine = null;
                StringBuffer message = new StringBuffer();
                //使用循环来读取获得的数据
                while (((inputLine = buffer.readLine()) != null)) {
                    message.append(inputLine);
                }
                //关闭InputStreamReader
                in.close();
                return message.toString();
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

    public static boolean delete(String url) {

//        client.delete(url, responseHandler);
//        client.removeHeader("Content-Type");
//        client.("Content-Length");

        try{
            URLConnection client = setup(url);

            client.setRequestProperty("Content-Type", "application/json");
            client.setRequestProperty("Content-Length", "0");

            // 得到HttpURLConnection对象
            HttpURLConnection connection = (HttpURLConnection) client;
            // 设置为GET方式
            connection.setRequestMethod("DELETE");
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                // 得到响应消息
                String message = connection.getResponseMessage();
                return true;
            }
            else{
                return false;
            }
        }
        catch (Exception e){
            return false;
        }
    }

//    public static boolean post(String url) {
//        try{
//            URLConnection client = setup(url);
//            // 得到HttpURLConnection对象
//            HttpURLConnection connection = (HttpURLConnection) client;
//            // 设置为GET方式
//            connection.setRequestMethod("POST");
//            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
//                // 得到响应消息
//                String message = connection.getResponseMessage();
//                return true;
//            }
//            else{
//                return false;
//            }
//        }
//        catch (Exception e){
//            return false;
//        }
//    }

    public static String post(String url) {
        try{
            URLConnection client = setup(url);
            // 得到HttpURLConnection对象
            HttpURLConnection connection = (HttpURLConnection) client;
            // 设置为GET方式
            connection.setRequestMethod("POST");
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                // 得到响应消息
                InputStreamReader in = new InputStreamReader(connection.getInputStream());
                // 为输出创建BufferedReader
                BufferedReader buffer = new BufferedReader(in);
                String inputLine = null;
                StringBuffer message = new StringBuffer();
                //使用循环来读取获得的数据
                while (((inputLine = buffer.readLine()) != null)) {
                    message.append(inputLine);
                }
                //关闭InputStreamReader
                in.close();
                return message.toString();
            }
            else{
                return null;
            }
        }
        catch (Exception e){
            return null;
        }
    }

    public static boolean post(String url, String content) {
        try{
            URLConnection client = setup(url);

            HttpURLConnection connection = (HttpURLConnection)client;

            // 设置是否向connection输出，因为这个是post请求，参数要放在
            // http正文内，因此需要设为true
            connection.setDoOutput(true);
            // Read from the connection. Default is true.
            connection.setDoInput(true);
            // 默认是 GET方式
            connection.setRequestMethod("POST");

            // Post 请求不能使用缓存
            connection.setUseCaches(false);
            connection.setInstanceFollowRedirects(true);
            connection.setRequestProperty("Content-Type","application/json");
            connection.connect();
            DataOutputStream out = new DataOutputStream(connection.getOutputStream());
            out.writeBytes(content);

            out.flush();
            out.close();

            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                // 得到响应消息
                String message = connection.getResponseMessage();
                return true;
            }
            else{
                return false;
            }
        }
        catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }
}
