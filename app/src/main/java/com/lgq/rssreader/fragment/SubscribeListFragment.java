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
import com.lgq.rssreader.adapter.ResultRecyclerViewAdapter;
import com.lgq.rssreader.enums.FromType;
import com.lgq.rssreader.model.Blog;
import com.lgq.rssreader.model.Channel;
import com.lgq.rssreader.model.Result;
import com.lgq.rssreader.task.AddChannelTask;
import com.lgq.rssreader.task.PageTask;
import com.lgq.rssreader.task.ResultTask;
import com.lgq.rssreader.util.PreferencesUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

/**
 * Created by redel on 2015-09-03.
 */
public class SubscribeListFragment extends BaseListFragment<Result> {
    public static final String SUBSCRIBE_QUERY = "subscribe_query";

    private ResultRecyclerViewAdapter mAdapter;
    private List<Result> mResults;


    public static final SubscribeListFragment newInstance()
    {
        SubscribeListFragment fragment = new SubscribeListFragment();
        fragment.setLayout(R.id.channelList);
        return fragment;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        String query = getActivity().getIntent().getCharSequenceExtra(SUBSCRIBE_QUERY).toString();
        mResults = new ArrayList<>();
        mAdapter = new ResultRecyclerViewAdapter(this.getContext(), mResults, new ResultRecyclerViewAdapter.ResultTextViewHolderFactory());
        setLayout(R.id.channelList);
        loadData(query);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public List<Result> loadData() {
        return mResults;
    }

    public void loadData(String query){
        new ResultTask(query, getSwipeRefreshLayout(), mAdapter).executeOnExecutor(Executors.newCachedThreadPool(), PreferencesUtil.getAccessToken());
    }

    @Override
    public void onItemClick(View view, Result data) {
        new AddChannelTask(data, getActivity()).executeOnExecutor(Executors.newCachedThreadPool(), PreferencesUtil.getAccessToken());
    }

    @Override
    public void onItemLongClick(View view, Result data) {

    }

    @Override
    public void onRefresh() {
        this.getSwipeRefreshLayout().setRefreshing(true);
        String query = getActivity().getIntent().getCharSequenceExtra(SUBSCRIBE_QUERY).toString();
        new ResultTask(query, getSwipeRefreshLayout(), mAdapter).executeOnExecutor(Executors.newCachedThreadPool(), PreferencesUtil.getAccessToken());
    }

    @Override
    public void onLoadMore() {

    }

    @Override
    public BaseRecyclerViewAdapter getAdapter() {
        return mAdapter;
    }

}