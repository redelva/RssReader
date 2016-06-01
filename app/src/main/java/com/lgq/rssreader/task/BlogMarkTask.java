package com.lgq.rssreader.task;

import android.os.AsyncTask;
import android.util.Log;

import com.github.clans.fab.FloatingActionButton;
import com.lgq.rssreader.R;
import com.lgq.rssreader.abstraction.FeedlyParser;
import com.lgq.rssreader.abstraction.RssParser;
import com.lgq.rssreader.model.Blog;
import com.lgq.rssreader.model.RssAction;

import java.lang.ref.WeakReference;

/**
 * Created by redel on 2015-10-22.
 */
public class BlogMarkTask extends AsyncTask<String, Void, Blog> {
    private RssAction mAction;
    private Blog mBlog;

    public BlogMarkTask(Blog mBlog, RssAction mAction){
        this.mAction = mAction;
        this.mBlog = mBlog;
    }

    protected Blog doInBackground(String... urls) {

        String token = urls[0];

        String userId = urls[1];

        RssParser parser = new FeedlyParser(token);
        try {
            boolean b = parser.markTag(userId, mBlog, mAction);

            Log.i("RssReader", "mark " + mBlog.getTitle() + " as " + mAction.toString() + " result is " + b);



            return mBlog;
        }catch (Exception e){
            return mBlog;
        }
    }

    protected void onPostExecute(Blog b) {
//        if(like.get() != null) {
//            Log.i("RssReader", "mark " + mBlog.getTitle() + " as " + mAction.toString() + " complete");
//            //    Toast.makeText(mContext.get(), "mark as " + mAction.toString() + " complete", Toast.LENGTH_SHORT).show();
//            if (b.getIsRead()) {
//                read.get().setLabelText("Read");
//                read.get().setImageResource(R.mipmap.ic_action_read);
//            } else {
//                read.get().setLabelText("Unread");
//                read.get().setImageResource(R.mipmap.ic_action_unread);
//            }
//
//            if (b.getIsStarred()) {
//                like.get().setLabelText("Like");
//                like.get().setImageResource(R.mipmap.ic_action_like);
//            } else {
//                like.get().setLabelText("Unlike");
//                like.get().setImageResource(R.mipmap.ic_action_unlike);
//            }
//        }
    }
}