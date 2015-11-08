package com.lgq.rssreader.task;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.lgq.rssreader.R;
import com.lgq.rssreader.abstraction.FeedlyParser;
import com.lgq.rssreader.abstraction.RssParser;
import com.lgq.rssreader.adapter.BlogRecyclerViewAdapter;
import com.lgq.rssreader.core.ReaderApp;
import com.lgq.rssreader.model.Blog;
import com.lgq.rssreader.model.Channel;
import com.lgq.rssreader.model.RssAction;
import com.marshalchen.ultimaterecyclerview.UltimateRecyclerView;
import com.orm.StringUtil;

import org.eclipse.mat.collect.ArrayUtils;

import java.lang.ref.WeakReference;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by redel on 2015-10-22.
 */
public class MarkTask extends AsyncTask<String, Void, Blog> {
    private RssAction mAction;
    private Blog mBlog;
    private WeakReference<FloatingActionButton> like;
    private WeakReference<FloatingActionButton> read;

    public MarkTask(FloatingActionButton like, FloatingActionButton read, Blog mBlog, RssAction mAction){
        this.read = new WeakReference<>(read);
        this.like = new WeakReference<>(like);
        this.mAction = mAction;
        this.mBlog = mBlog;
    }

    protected Blog doInBackground(String... urls) {

        String token = urls[0];

        String userId = urls[1];

        RssParser parser = new FeedlyParser(token);
        try {
            boolean b = parser.markTag(userId, mBlog, mAction);

            if(b){
                if(mAction == RssAction.AsRead){
                    mBlog.setIsRead(true);
                }

                if(mAction == RssAction.AsStar){
                    mBlog.setIsStarred(true);
                }

                if(mAction == RssAction.AsUnread){
                    mBlog.setIsRead(false);
                }

                if(mAction == RssAction.AsUnstar){
                    mBlog.setIsStarred(false);
                }

                mBlog.save();
            }

            return mBlog;
        }catch (Exception e){
            return mBlog;
        }
    }

    protected void onPostExecute(Blog b) {
        if(like.get() != null) {
            Log.i("RssReader", "mark " + mBlog.getTitle() + " as " + mAction.toString() + " complete");
            //    Toast.makeText(mContext.get(), "mark as " + mAction.toString() + " complete", Toast.LENGTH_SHORT).show();
            if (b.getIsRead()) {
                read.get().setLabelText("Read");
                read.get().setImageResource(R.mipmap.ic_action_read);
            } else {
                read.get().setLabelText("Unread");
                read.get().setImageResource(R.mipmap.ic_action_unread);
            }

            if (b.getIsStarred()) {
                like.get().setLabelText("Like");
                like.get().setImageResource(R.mipmap.ic_action_like);
            } else {
                like.get().setLabelText("Unlike");
                like.get().setImageResource(R.mipmap.ic_action_unlike);
            }
        }
    }
}