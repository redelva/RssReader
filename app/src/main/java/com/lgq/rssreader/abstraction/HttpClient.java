package com.lgq.rssreader.abstraction;

import org.apache.http.conn.ssl.AllowAllHostnameVerifier;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.X509TrustManager;

public class HttpClient {

    public static String get(final String urlString, HashMap<String, String> headers)  {
        HttpURLConnection connection = null;
        URL url = null;
        try {
            url = new URL(urlString);

            //关键代码
            //ignore https certificate validation |忽略 https 证书验证
            if (url.getProtocol().toUpperCase().equals("HTTPS")) {
//                //trustAllHosts();
//                HttpsURLConnection https = (HttpsURLConnection) url.openConnection();
//                https.setHostnameVerifier(new AllowAllHostnameVerifier());

                HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier(){
                    public boolean verify(String hostname, SSLSession session) {
                        return true;
                    }});
                SSLContext context = SSLContext.getInstance("TLS");
                context.init(null, new X509TrustManager[]{new X509TrustManager(){
                    public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {}
                    public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {}
                    public X509Certificate[] getAcceptedIssuers() {
                        return new X509Certificate[0];
                    }}}, new SecureRandom());
                HttpsURLConnection.setDefaultSSLSocketFactory(context.getSocketFactory());

                HttpsURLConnection https = (HttpsURLConnection) url.openConnection();
                connection = https;
            } else {
                connection = (HttpURLConnection) url.openConnection();
            }

            connection.addRequestProperty("User-Agent", "Mozilla/5.0 (Linux; Android 4.4.4; en-us; Nexus 5 Build/JOP40D) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/42.0.2307.2 Mobile Safari/537.36");
            for (String key : headers.keySet()) {
                connection.addRequestProperty(key, headers.get(key));
            }
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
                return String.valueOf(HttpURLConnection.HTTP_UNAUTHORIZED);
            }
            else if(connection.getResponseCode() == HttpURLConnection.HTTP_MOVED_TEMP){
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

    public static boolean delete(String url) {
        try{
            URLConnection client = new URL(url).openConnection();

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

    public static boolean delete(String url, HashMap<String, String> headers) {
        try{
            URLConnection client = new URL(url).openConnection();

            for (String key : headers.keySet()) {
                client.addRequestProperty(key, headers.get(key));
            }

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

    public static String post(String url) {
        try{
            URLConnection client = new URL(url).openConnection();
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
        URLConnection client = null;
        try{

            URLConnection conn = new URL(url).openConnection();
            conn.setDoOutput(true);
            OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());

            //write parameters
            writer.write(content);
            writer.flush();

            // Get the response
            StringBuffer answer = new StringBuffer();
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                answer.append(line);
            }
            writer.close();
            reader.close();

            //Output the response
            System.out.println(answer.toString());

            return answer != null;
        }
        catch (Exception e){
            e.printStackTrace();
            return false;
        }
        finally {
            if(client != null){
                ((HttpURLConnection)client).disconnect();
            }
        }
    }

    public static boolean put(String url, String content) {
        try{
            URLConnection client = new URL(url).openConnection();

            HttpURLConnection connection = (HttpURLConnection)client;

            // 设置是否向connection输出，因为这个是post请求，参数要放在
            // http正文内，因此需要设为true
            connection.setDoOutput(true);
            // Read from the connection. Default is true.
            connection.setDoInput(true);
            // 默认是 GET方式
            connection.setRequestMethod("PUT");

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

    public static String post(String url, HashMap<String, String> headers) {
        try{
            URLConnection client = new URL(url).openConnection();

            for (String key : headers.keySet()) {
                client.addRequestProperty(key, headers.get(key));
            }

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

    public static String post(String url, HashMap<String, String> headers, String content) {
        URLConnection client = null;
        try{

            client = new URL(url).openConnection();

            for (String key : headers.keySet()) {
                client.addRequestProperty(key, headers.get(key));
            }

            client.setDoOutput(true);
            OutputStreamWriter writer = new OutputStreamWriter(client.getOutputStream());

            //write parameters
            writer.write(content);
            writer.flush();

            // Get the response
            StringBuffer answer = new StringBuffer();
            BufferedReader reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                answer.append(line);
            }
            writer.close();
            reader.close();

            //Output the response
            System.out.println(answer.toString());

            return answer.toString();
        }
        catch (Exception e){
            e.printStackTrace();
            return null;
        }
        finally {
            if(client != null){
                ((HttpURLConnection)client).disconnect();
            }
        }
    }

    public static boolean put(String url, HashMap<String, String> headers, String content) {
        try{
            URLConnection client = new URL(url).openConnection();

            for (String key : headers.keySet()) {
                client.addRequestProperty(key, headers.get(key));
            }

            HttpURLConnection connection = (HttpURLConnection)client;

            // 设置是否向connection输出，因为这个是post请求，参数要放在
            // http正文内，因此需要设为true
            connection.setDoOutput(true);
            // Read from the connection. Default is true.
            connection.setDoInput(true);
            // 默认是 GET方式
            connection.setRequestMethod("PUT");

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
