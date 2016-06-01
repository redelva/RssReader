package com.lgq.rssreader.task;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.Toast;

import com.lgq.rssreader.R;
import com.lgq.rssreader.abstraction.FeedlyParser;
import com.lgq.rssreader.abstraction.RssParser;
import com.lgq.rssreader.core.Constant;
import com.lgq.rssreader.core.ReaderApp;
import com.lgq.rssreader.model.Channel;
import com.lgq.rssreader.model.Result;
import com.lgq.rssreader.model.RssAction;
import com.lgq.rssreader.util.PreferencesUtil;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * Created by redel on 2015-10-22.
 */
public class AddChannelTask extends AsyncTask<String, Void, Boolean> {
    private Result result;
    private WeakReference<Activity> mContent;

    public AddChannelTask(Result result, Activity activity){
        this.result = result;
        this.mContent = new WeakReference<>(activity);
    }

    protected Boolean doInBackground(String... urls) {
        String token = urls[0];

        RssParser parser = new FeedlyParser(token);
        try {
            boolean b = parser.addRss(result.getFeedId(), result.getTitle());

            return b;
        }catch (Exception e){
            return null;
        }
    }

    protected void onPostExecute(Boolean result) {
        if(result){
            Toast.makeText(ReaderApp.getContext(), R.string.add_rss_complete, Toast.LENGTH_SHORT).show();

            if(mContent.get() != null){
                //数据是使用Intent返回
                Intent intent = new Intent();
                //把返回数据存入Intent
                intent.putExtra("result", result);
                //设置返回数据
                mContent.get().setResult(Constant.ADD_SUBSCRIBE, intent);
                //关闭Activity
                mContent.get().finish();
            }
        }
    }
}