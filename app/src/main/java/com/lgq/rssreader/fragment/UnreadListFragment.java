package com.lgq.rssreader.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lgq.rssreader.ContentActivity;
import com.lgq.rssreader.R;
import com.lgq.rssreader.adapter.BaseRecyclerViewAdapter;
import com.lgq.rssreader.adapter.BlogRecyclerViewAdapter;
import com.lgq.rssreader.enums.FromType;
import com.lgq.rssreader.enums.ItemType;
import com.lgq.rssreader.model.Blog;
import com.lgq.rssreader.model.Channel;
import com.lgq.rssreader.task.PageTask;
import com.lgq.rssreader.util.PreferencesUtil;

import java.util.List;
import java.util.concurrent.Executors;

/**
 * Created by redel on 2015-09-03.
 */
public class UnreadListFragment extends BaseListFragment<Blog> {

    private BlogRecyclerViewAdapter mAdapter;
    private List<Blog> mBlogs;

    public static final UnreadListFragment newInstance() {
        UnreadListFragment fragment = new UnreadListFragment();
        fragment.setLayout(R.id.channelList);
        return fragment;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBlogs = Blog.find(Blog.class, "IS_Read=0", null, "", "TIME_STAMP DESC", "30");
        mAdapter = new BlogRecyclerViewAdapter(this.getContext(), mBlogs, new BlogRecyclerViewAdapter.BlogTextViewHolderFactory());
        setLayout(R.id.channelList);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public List<Blog> loadData() {
        return mBlogs;
    }

    @Override
    public void onItemClick(View view, Blog data) {
        Intent intent = new Intent();
        intent.setClass(this.getContext(), ContentActivity.class);
        Bundle mBundle = new Bundle();
        mBundle.putString("blog", data.getBlogId());
        mBundle.putString("from", FromType.Unread.toString());
        intent.putExtras(mBundle);
        startActivity(intent);
    }

    @Override
    public void onItemLongClick(View view, Blog data) {

    }

    @Override
    public void onRefresh() {
        new PageTask(
                Channel.getUnreadChannel(),
                mBlogs,
                mAdapter,
                this.getSwipeRefreshLayout()).
        executeOnExecutor(Executors.newCachedThreadPool(), PreferencesUtil.getAccessToken());
    }

    @Override
    public void onLoadMore() {
        new PageTask(
                Channel.getUnreadChannel(),
                mBlogs,
                mAdapter,
                this.getSwipeRefreshLayout()).
                executeOnExecutor(Executors.newCachedThreadPool(), PreferencesUtil.getAccessToken());
    }

    @Override
    public BaseRecyclerViewAdapter getAdapter() {
        return mAdapter;
    }

}