package com.lgq.rssreader.task;

import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;

import com.lgq.rssreader.abstraction.FeedlyParser;
import com.lgq.rssreader.abstraction.RssParser;
import com.lgq.rssreader.adapter.BlogRecyclerViewAdapter;
import com.lgq.rssreader.model.Blog;
import com.lgq.rssreader.model.Channel;
import com.orm.StringUtil;

import java.lang.ref.WeakReference;
import java.util.Date;
import java.util.List;

/**
 * Created by redel on 2015-10-22.
 */
public class PageTask extends AsyncTask<String, Void, List<Blog>> {

    private WeakReference< RecyclerView.Adapter> mAdapter;
    private WeakReference<SwipeRefreshLayout> mSwipeRefreshLayout;
    private Channel mChannel;
    private List<Blog> mBlogs;

    public PageTask(Channel mChannel, List<Blog> mBlogs, RecyclerView.Adapter mAdapter, SwipeRefreshLayout mSwipeRefreshLayout){
        this.mChannel = mChannel;
        this.mBlogs = mBlogs;
        this.mAdapter = new WeakReference<>(mAdapter);
        this.mSwipeRefreshLayout = new WeakReference<>(mSwipeRefreshLayout);
    }

    protected List<Blog> doInBackground(String... urls) {

        String token = urls[0];
        List<Blog> nextPage = null;
        Blog last = null;

        if(mBlogs != null && mBlogs.size() > 0){
            last = mBlogs.get(mBlogs.size() - 1);

            nextPage = Blog.find(Blog.class, StringUtil.toSQLName("timeStamp") + " <? and " + StringUtil.toSQLName("channelId") + "=?", new String[]{String.valueOf(last.getTimeStamp()),String.valueOf(last.getChannelId())}, "", StringUtil.toSQLName("timeStamp") + " DESC", "30");
        }else{
            last = new Blog();
            last.setTimeStamp(0);
            last.setPubDate(new Date());
        }

        if (nextPage != null && nextPage.size() > 0) {
            for (Blog b : nextPage) {
                mBlogs.add(b);
            }
            return nextPage;
        } else {
            RssParser parser = new FeedlyParser(token);
            try {
                last.setTimeStamp(-last.getTimeStamp());

                List<Blog> blogs = parser.getRssBlog(mChannel, last, 30);

                if (blogs != null) {
                    for (int i = blogs.size() - 1; i >= 0; i--) {
                        Blog b = blogs.get(i);
                        if (Blog.count(Blog.class, "BLOG_ID=?", new String[]{b.getBlogId()}) == 0) {
                            mBlogs.add(b);
                            b.save();
                        }
                    }
                }

                return blogs;
            } catch (Exception e) {
                return null;
            }
        }
    }

    protected void onPreExecute() {
        if(mSwipeRefreshLayout.get() != null)
            mSwipeRefreshLayout.get().setRefreshing(true);
    }

    protected void onPostExecute(List<Blog> blogs) {
        if(mAdapter.get() != null)
            mAdapter.get().notifyDataSetChanged();

        if(mSwipeRefreshLayout.get() != null)
            mSwipeRefreshLayout.get().setRefreshing(false);
    }
}