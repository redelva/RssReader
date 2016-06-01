package com.lgq.rssreader.task;

import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.lgq.rssreader.abstraction.FeedlyParser;
import com.lgq.rssreader.abstraction.RssParser;
import com.lgq.rssreader.adapter.ResultRecyclerViewAdapter;
import com.lgq.rssreader.core.ReaderApp;
import com.lgq.rssreader.model.Blog;
import com.lgq.rssreader.model.Channel;
import com.lgq.rssreader.model.Result;
import com.orm.StringUtil;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * Created by redel on 2015-10-22.
 */
public class ResultTask extends AsyncTask<String, Void, List<Result>> {
    private WeakReference<SwipeRefreshLayout> mSwipeRefreshLayout;
    private WeakReference<ResultRecyclerViewAdapter> mAdapter;
    private String query;

    public ResultTask(String query, SwipeRefreshLayout mSwipeRefreshLayout, ResultRecyclerViewAdapter mAdapter){
        this.query = query;
        this.mSwipeRefreshLayout = new WeakReference<>(mSwipeRefreshLayout);
        this.mAdapter = new WeakReference<>(mAdapter);
    }

    protected List<Result> doInBackground(String... urls) {
        String token = urls[0];
        RssParser parser = new FeedlyParser(token);
        try {
            List<Result> results = parser.searchRss(query, 0);

            return results;
        }catch (Exception e){
            return null;
        }
    }

    protected void onPostExecute(List<Result> results) {
        if(mSwipeRefreshLayout.get() != null)
            mSwipeRefreshLayout.get().setRefreshing(false);

        if (results == null){
            Toast.makeText(ReaderApp.getContext(), "获取数据失败", Toast.LENGTH_SHORT).show();
            return;
        }

        if(mAdapter.get() != null && results != null) {
            mAdapter.get().getData().clear();
            mAdapter.get().getData().addAll(results);
            mAdapter.get().notifyDataSetChanged();
        }
    }
}