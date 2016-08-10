package com.lgq.rssreader.task;

import android.os.AsyncTask;
import android.widget.Toast;

import com.lgq.rssreader.R;
import com.lgq.rssreader.abstraction.FeedlyParser;
import com.lgq.rssreader.abstraction.RssParser;
import com.lgq.rssreader.adapter.ChannelRecyclerViewAdapter;
import com.lgq.rssreader.core.ReaderApp;
import com.lgq.rssreader.model.Channel;
import com.lgq.rssreader.model.RssAction;
import com.lgq.rssreader.util.PreferencesUtil;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * Created by redel on 2015-10-22.
 */
public class DownloadTask extends AsyncTask<String, Void, List<Channel>> {
    private RssAction mAction;
    private Channel mChannel;
    private WeakReference<ChannelRecyclerViewAdapter> mContent;

    public DownloadTask(Channel mChannel, RssAction mAction, ChannelRecyclerViewAdapter fragment){
        this.mAction = mAction;
        this.mChannel = mChannel;
        this.mContent = new WeakReference<ChannelRecyclerViewAdapter>(fragment);
    }

    protected List<Channel> doInBackground(String... urls) {

        String token = urls[0];

        String userId = urls[1];

        RssParser parser = new FeedlyParser(token);
        try {
            boolean b = parser.markTag(userId, mChannel, mAction);

            List<Channel> channels = null;

            if(b){
                channels = parser.loadData();

                PreferencesUtil.saveChannels(channels);
            }

            return channels;
        }catch (Exception e){
            return null;
        }
    }

    protected void onPostExecute(List<Channel> channels) {
        if(channels != null){
            if(this.mContent.get() != null){
                this.mContent.get().resetData(channels);
            }
            Toast.makeText(ReaderApp.getContext(), R.string.channel_markComplete, Toast.LENGTH_SHORT);
        }else{
            Toast.makeText(ReaderApp.getContext(), R.string.channel_markFailed, Toast.LENGTH_SHORT);
        }
    }
}