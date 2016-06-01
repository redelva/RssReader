package com.lgq.rssreader.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.IdRes;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.lgq.rssreader.BlogListActivity;
import com.lgq.rssreader.R;

import com.lgq.rssreader.abstraction.FeedlyParser;
import com.lgq.rssreader.abstraction.RssParser;
import com.lgq.rssreader.adapter.BaseRecyclerViewAdapter;
import com.lgq.rssreader.adapter.ChannelRecyclerViewAdapter;
import com.lgq.rssreader.adapter.DialogAdapter;
import com.lgq.rssreader.adapter.OnRecyclerViewItemClickListener;
import com.lgq.rssreader.controls.EndlessRecyclerOnScrollListener;
import com.lgq.rssreader.core.ReaderApp;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.recyclerview.animators.SlideInUpAnimator;

/**
 * Created by redel on 2015-09-03.
 */
public abstract class BaseListFragment<T> extends BaseFragment {
    private RecyclerView mRecyclerView;
    public RecyclerView getRecyclerView(){
        return mRecyclerView;
    }
    private SwipeRefreshLayout mSwipeRefreshLayout;
    public SwipeRefreshLayout getSwipeRefreshLayout(){
        return mSwipeRefreshLayout;
    }
    private BaseRecyclerViewAdapter<T, RecyclerView.ViewHolder> mAdapter;
    private EndlessRecyclerOnScrollListener scrollListener = new EndlessRecyclerOnScrollListener() {
        @Override
        public void onBottom() {
            super.onBottom();

            BaseListFragment.this.onLoadMore();
        }
    };
    public RecyclerView.Adapter<RecyclerView.ViewHolder> getChannelRecyclerViewAdapter(){
        return mAdapter;
    }
    private List<T> mData = new ArrayList<>();
    public List<T> getData(){
        return mData;
    }

    private int layoutId;

    public void setLayout(@IdRes int id){
        layoutId = id;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_channel, container, false);
        mData = loadData();
        mRecyclerView = (RecyclerView)view.findViewById(layoutId);
        mAdapter = getAdapter();
        mSwipeRefreshLayout = (SwipeRefreshLayout)view.findViewById(R.id.swipeRefreshLayout);
        initView();
        return view;
    }

    public abstract List<T> loadData();
    public abstract void onItemClick(View view, T data);
    public abstract void onItemLongClick(View view, T data);
    public abstract void onRefresh();
    public abstract void onLoadMore();
    public abstract BaseRecyclerViewAdapter<T, RecyclerView.ViewHolder> getAdapter();

    private void initView(){
        mRecyclerView.setLayoutManager(new LinearLayoutManager(ReaderApp.getContext()));
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setItemAnimator(new SlideInUpAnimator());

        mAdapter.setOnItemClickListener(new OnRecyclerViewItemClickListener<T>() {
            @Override
            public void onItemClick(View view, T data) {
                BaseListFragment.this.onItemClick(view, data);
            }

            @Override
            public void onItemLongClick(View view, T data) {
                BaseListFragment.this.onItemLongClick(view, data);
            }
        });

        //mSwipeRefreshLayout.setColorSchemeResources(R.color.md_green_600);
        mSwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light, android.R.color.holo_orange_light, android.R.color.holo_red_light);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSwipeRefreshLayout.setRefreshing(true);

                BaseListFragment.this.onRefresh();
            }
        });

        mRecyclerView.addOnScrollListener(scrollListener);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mRecyclerView.removeOnScrollListener(scrollListener);
    }
}