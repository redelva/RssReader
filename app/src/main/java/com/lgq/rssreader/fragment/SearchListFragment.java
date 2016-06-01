package com.lgq.rssreader.fragment;

import android.app.SearchManager;
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
public class SearchListFragment extends BaseListFragment<Blog> {

    private BlogRecyclerViewAdapter mAdapter;
    private List<Blog> mBlogs;

    public static final SearchListFragment newInstance()
    {
        SearchListFragment fragment = new SearchListFragment();
        fragment.setLayout(R.id.channelList);
        return fragment;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        String query = "";
        if (Intent.ACTION_SEARCH.equals(getActivity().getIntent().getAction())) {
            query = getActivity().getIntent().getStringExtra(SearchManager.QUERY);
        }
        mBlogs = Blog.find(Blog.class, "title like ? or description like ? or content like ?", new String[]{"%"+query+"%", "%"+query+"%", "%"+query+"%"}, "", "TIME_STAMP DESC", "30");
        mAdapter = new BlogRecyclerViewAdapter(this.getContext(), mBlogs, new BlogRecyclerViewAdapter.BlogTextViewHolderFactory());
        setLayout(R.id.channelList);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    protected void onNewIntent(Intent intent){
        String query = "";
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            query = intent.getStringExtra(SearchManager.QUERY);
            //use the query to search your data somehow
        }
        mBlogs = Blog.find(Blog.class, "title like '?' or description like '?' or content like '?'", new String[]{"%"+query+"%", "%"+query+"%", "%"+query+"%"}, "", "TIME_STAMP DESC", "30");
        mAdapter = new BlogRecyclerViewAdapter(this.getContext(), mBlogs, new BlogRecyclerViewAdapter.BlogTextViewHolderFactory());
        setLayout(R.id.channelList);
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
        mBundle.putString("from", FromType.All.toString());
        mBundle.putString("query", FromType.Search.toString());
        intent.putExtras(mBundle);
        startActivity(intent);
    }

    @Override
    public void onItemLongClick(View view, Blog data) {

    }

    @Override
    public void onRefresh() {
        this.getSwipeRefreshLayout().setRefreshing(true);

    }

    @Override
    public void onLoadMore() {
        new PageTask(
                Channel.getAllChannel(),
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