package com.lgq.rssreader.task;

import android.os.AsyncTask;

import com.lgq.rssreader.abstraction.FeedlyParser;
import com.lgq.rssreader.abstraction.RssParser;
import com.lgq.rssreader.adapter.BlogRecyclerViewAdapter;
import com.lgq.rssreader.model.Blog;
import com.lgq.rssreader.model.Channel;
import com.marshalchen.ultimaterecyclerview.UltimateRecyclerView;
import com.orm.StringUtil;

import java.lang.ref.WeakReference;
import java.util.Date;
import java.util.List;

/**
 * Created by redel on 2015-10-22.
 */
public class PageTask extends AsyncTask<String, Void, List<Blog>> {
    private WeakReference<UltimateRecyclerView> mRecyclerView;
    private WeakReference<BlogRecyclerViewAdapter> mAdapter;
    private Channel mChannel;
    private List<Blog> mBlogs;

    public PageTask(Channel mChannel, List<Blog> mBlogs, UltimateRecyclerView mRecyclerView, BlogRecyclerViewAdapter mAdapter){
        this.mChannel = mChannel;
        this.mBlogs = mBlogs;
        this.mRecyclerView = new WeakReference<UltimateRecyclerView>(mRecyclerView);
        this.mAdapter = new WeakReference<BlogRecyclerViewAdapter>(mAdapter);
    }

    protected List<Blog> doInBackground(String... urls) {

        String token = urls[0];

        Blog last = mBlogs.get(mBlogs.size() - 1);

        List<Blog> nextPage = Blog.find(Blog.class, StringUtil.toSQLName("timeStamp") + " <?", new String[]{String.valueOf(last.getTimeStamp())}, "", StringUtil.toSQLName("timeStamp") + " DESC", "30");

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

                    if (blogs != null) {
                        for (int i = blogs.size() - 1; i >= 0; i--) {
                            Blog b = blogs.get(i);
                            if (Blog.count(Blog.class, "BLOG_ID=?", new String[]{b.getBlogId()}) == 0) {
                                mBlogs.add(b);
                                b.save();
                            }
                        }
                    }
                }

                return blogs;
            } catch (Exception e) {
                return null;
            }
        }
    }

    protected void onPostExecute(List<Blog> blogs) {
        if(mAdapter.get() != null)
            mAdapter.get().notifyDataSetChanged();
        if(mRecyclerView.get() != null)
            mRecyclerView.get().setRefreshing(false);
    }
}