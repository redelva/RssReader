package com.lgq.rssreader;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;

import com.lgq.rssreader.abstraction.FeedlyParser;
import com.lgq.rssreader.abstraction.RssParser;
import com.lgq.rssreader.core.ReaderApp;
import com.lgq.rssreader.model.Profile;
import com.lgq.rssreader.util.PreferencesUtil;

import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.KeyEvent;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class LoginActivity extends FragmentActivity {
    private static final String LOGIN_URL = "http://feedly.com/v3/auth/auth?client_id=feedly&redirect_uri=http%3A%2F%2Ffeedly.com%2Ffeedly.html&scope=https%3A%2F%2Fcloud.feedly.com%2Fsubscriptions&response_type=code&migrate=false&ck=1412952055218&ct=feedly.desktop&windowsLiveOAuthActive=true&facebookOAuthActive=true&twitterOAuthActive=true&cv=24.0.861&mode=login";
    private static final String FEEDLYLOGINURL = "http://cloud.feedly.com/v3/auth/token";

    private ProgressDialog mProgressDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        boolean needLogin = checkTokenOrUpdate();
        if(needLogin)
            initViews();
    }

    private boolean checkTokenOrUpdate(){
        if(PreferencesUtil.getAccessToken().length() > 0){
            Intent i = new Intent(this,MainActivity.class);
            startActivity(i);
            LoginActivity.this.finish();
            return false;
        }

        return true;
    }

    private void initViews(){
        WebView view = (WebView) findViewById(R.id.login_webview);
        initProgressDialog();
        view.getSettings().setJavaScriptEnabled(true);
        view.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);

        view.setWebViewClient(new WebViewClient() {
            public void onPageFinished(WebView view, String url) {
                Log.i("RssReader", "finished " + url);

                if(url.equals(LOGIN_URL)){
                    mProgressDialog.hide();
                }else {
                    if (url.contains("http://feedly.com/feedly.html") && url.contains("code=")) {
                        mProgressDialog.setMessage(getResources().getString(R.string.login_authing));
                        mProgressDialog.show();

                        String params = url.substring(url.indexOf("?") + 1);
                        String code = "";

                        for (String p : params.split("&")) {
                            if (p.contains("code")) {
                                code = p.split("=")[1];
                                break;
                            }
                        }

                        new AuthTask(code).execute();
                    }
                }
            }
        });

        view.loadUrl(LOGIN_URL);
    }

    private void initProgressDialog(){
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.setIcon(R.drawable.progress);
        mProgressDialog.setMessage(getResources().getString(R.string.content_loading) + "...");
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setIndeterminateDrawable(getResources().getDrawable(R.drawable.spinner));
        mProgressDialog.show();
    }

    private class AuthTask extends AsyncTask<String, Void, String>{
        private String code;

        public AuthTask(String code){
            this.code = code;
        }

        @Override
        protected String doInBackground(String... params) {
            try{

                String actionParams = "client_id=feedly&client_secret=0XP4XQ07VVMDWBKUHTJM4WUQ&grant_type=authorization_code&" +
                        "redirect_uri=http%3A%2F%2Fwww.feedly.com%2Ffeedly.html&code=" + code;

                URLConnection client = new URL(FEEDLYLOGINURL).openConnection();

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
                connection.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
                connection.connect();
                DataOutputStream out = new DataOutputStream(connection.getOutputStream());
                out.writeBytes(actionParams);

                out.flush();
                out.close();

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

                    JSONObject result = new JSONObject(message.toString());

                    PreferencesUtil.saveAccessToken(result.getString("access_token"));
                    PreferencesUtil.saveRefreshToken(result.getString("refresh_token"));

                    Profile p = new FeedlyParser(result.getString("access_token")).getProfile();

                    PreferencesUtil.saveProfile(p);

                    return null;
                }else{
                    return null;
                }
            }
            catch (Exception ex){
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            if(PreferencesUtil.getAccessToken().length() > 0){
                mProgressDialog.dismiss();

                Intent intent = new Intent(LoginActivity.this ,MainActivity.class);
                setResult(Activity.RESULT_OK, intent);
                startActivity(intent);
                LoginActivity.this.finish();
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK ) {
            if(PreferencesUtil.getAccessToken().length() == 0)
                return false;
            else
                return super.onKeyDown(keyCode, event);
        }

        return super.onKeyDown(keyCode, event);
    }
}