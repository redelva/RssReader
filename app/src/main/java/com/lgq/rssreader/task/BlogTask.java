package com.lgq.rssreader.task;

import android.os.AsyncTask;
import android.widget.Toast;

import com.lgq.rssreader.abstraction.FeedlyParser;
import com.lgq.rssreader.abstraction.RssParser;
import com.lgq.rssreader.adapter.BlogRecyclerViewAdapter;
import com.lgq.rssreader.core.ReaderApp;
import com.lgq.rssreader.model.Blog;
import com.lgq.rssreader.model.Channel;
import com.marshalchen.ultimaterecyclerview.UltimateRecyclerView;
import com.orm.StringUtil;

import org.eclipse.mat.collect.ArrayUtils;

import java.lang.ref.WeakReference;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * Created by redel on 2015-10-22.
 */
public class BlogTask extends AsyncTask<String, Void, List<Blog>> {
    private WeakReference<UltimateRecyclerView> mRecyclerView;
    private WeakReference<BlogRecyclerViewAdapter> mAdapter;
    private Channel mChannel;
    private List<Blog> mBlogs;

    public BlogTask(Channel mChannel, List<Blog> mBlogs, UltimateRecyclerView mRecyclerView, BlogRecyclerViewAdapter mAdapter){
        this.mChannel = mChannel;
        this.mBlogs = mBlogs;
        this.mRecyclerView = new WeakReference<UltimateRecyclerView>(mRecyclerView);
        this.mAdapter = new WeakReference<BlogRecyclerViewAdapter>(mAdapter);
    }

    protected List<Blog> doInBackground(String... urls) {

        String token = urls[0];

        RssParser parser = new FeedlyParser(token);
        try {
            Blog tmp;
            if(mBlogs.size() > 0){
                tmp = mBlogs.get(0);
            }else{
                tmp = new Blog();
                tmp.setTimeStamp(0);
                tmp.setPubDate(new Date());
            }

            List<Blog> blogs = parser.getRssBlog(mChannel, tmp, 30);

            if(blogs != null){
                String whereClause = "";
                String[] whereArgs = new String[blogs.size()];
                for(int i = blogs.size() - 1; i>=0;i--){
                    Blog b = blogs.get(i);
                    whereClause = whereClause + StringUtil.toSQLName("blogID") + "=?";
                    if(i != 0){
                        whereClause = whereClause + " OR ";
                    }
                    whereArgs[i] = b.getBlogId();
                }

                List<Blog> exists = Blog.find(Blog.class, whereClause, whereArgs);

                ArrayList<String> existBlogIDs = new ArrayList<>();
                for (Blog b : exists) {
                    existBlogIDs.add(b.getBlogId());
                }

                if(existBlogIDs.size() > 0) {
                    for (Blog b : blogs) {

                        if (!existBlogIDs.contains(b.getBlogId())) {
                            mBlogs.add(0, b);

                            b.save();
                        }
                    }
                }else {
                    mBlogs.addAll(blogs);
                    Blog.saveInTx(blogs);
                }

                java.util.Collections.sort(mBlogs, new Comparator() {
                    @Override
                    public int compare(Object o1, Object o2) {
                        return -(int)(((Blog)o1).getTimeStamp() - ((Blog)o2).getTimeStamp());
                    }
                });
            }

            return blogs;
        }catch (Exception e){
            return null;
        }
    }

    protected void onPostExecute(List<Blog> blogs) {
        if(blogs == null){
            Toast.makeText(ReaderApp.getContext(), "获取数据失败", Toast.LENGTH_SHORT).show();
            return;
        }

        if(mAdapter.get() != null && blogs != null) {
            mAdapter.get().notifyDataSetChanged();
        }
        if(mRecyclerView.get() != null)
            mRecyclerView.get().setRefreshing(false);
    }
}