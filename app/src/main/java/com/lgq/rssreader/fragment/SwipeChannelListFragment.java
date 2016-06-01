//package com.lgq.rssreader.fragment;
//
//import android.content.Intent;
//import android.os.AsyncTask;
//import android.os.Bundle;
//import android.support.annotation.IdRes;
//import android.support.v4.widget.SwipeRefreshLayout;
//import android.support.v7.widget.LinearLayoutManager;
//import android.support.v7.widget.RecyclerView;
//import android.view.Gravity;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Adapter;
//import android.widget.TextView;
//
//import com.lgq.rssreader.BlogListActivity;
//import com.lgq.rssreader.R;
//import com.lgq.rssreader.abstraction.FeedlyParser;
//import com.lgq.rssreader.abstraction.RssParser;
//import com.lgq.rssreader.adapter.BaseRecyclerViewAdapter;
//import com.lgq.rssreader.adapter.ChannelRecyclerViewAdapter;
//import com.lgq.rssreader.adapter.DialogAdapter;
//import com.lgq.rssreader.adapter.OnRecyclerViewItemClickListener;
//import com.lgq.rssreader.adapter.SwipeChannelRecyclerViewAdapter;
//import com.lgq.rssreader.adapter.ViewHolderFactory;
//import com.lgq.rssreader.controls.EndlessRecyclerOnScrollListener;
//import com.lgq.rssreader.core.ReaderApp;
//import com.lgq.rssreader.model.Channel;
//import com.lgq.rssreader.util.PreferencesUtil;
//import java.util.List;
//
//import jp.wasabeef.recyclerview.animators.SlideInUpAnimator;
//
///**
// * Created by redel on 2015-09-03.
// */
//public class SwipeChannelListFragment extends BaseFragment {
//
//    private static String CHANNELS = "channels";
//    //private SwipeChannelRecyclerViewAdapter mAdapter;
//    ChannelRecyclerViewAdapter mAdapter;
//    private List<Channel> mChannels;
//
//    public RecyclerView getRecyclerView(){
//        return mRecyclerView;
//    }
//    public SwipeRefreshLayout getSwipeRefreshLayout(){
//        return mSwipeRefreshLayout;
//    }
//    public List<Channel> getData(){
//        return mChannels;
//    }
//
//    public  RecyclerView.Adapter getChannelRecyclerViewAdapter(){
//        return mAdapter;
//    }
//
//    private RecyclerView mRecyclerView;
//    private SwipeRefreshLayout mSwipeRefreshLayout;
//    private EndlessRecyclerOnScrollListener scrollListener = new EndlessRecyclerOnScrollListener() {
//        @Override
//        public void onBottom() {
//            super.onBottom();
//
//            SwipeChannelListFragment.this.onLoadMore();
//        }
//    };
//
//    private void initView(){
//        mRecyclerView.setLayoutManager(new LinearLayoutManager(ReaderApp.getContext()));
//        mRecyclerView.setAdapter(mAdapter);
//        //mAdapter.setCustomHeaderView(new UltimateRecyclerView.CustomRelativeWrapper(ReaderApp.getContext()));
//        mRecyclerView.setItemAnimator(new SlideInUpAnimator());
//
//        mAdapter.setOnItemClickListener(new OnRecyclerViewItemClickListener<Channel>() {
//            @Override
//            public void onItemClick(View view, Channel data) {
//                SwipeChannelListFragment.this.onItemClick(view, data);
//            }
//
//            @Override
//            public void onItemLongClick(View view, Channel data) {
//                SwipeChannelListFragment.this.onItemLongClick(view, data);
//            }
//        });
//
//        //mSwipeRefreshLayout.setColorSchemeResources(R.color.md_green_600);
//        mSwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
//                android.R.color.holo_green_light, android.R.color.holo_orange_light, android.R.color.holo_red_light);
//        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                mSwipeRefreshLayout.setRefreshing(true);
//
//                SwipeChannelListFragment.this.onRefresh();
//            }
//        });
//
//        mRecyclerView.addOnScrollListener(scrollListener);
//    }
//
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//
//        mRecyclerView.removeOnScrollListener(scrollListener);
//    }
//
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.fragment_channel, container, false);
//        mChannels = PreferencesUtil.getChannels();
//        mRecyclerView = (RecyclerView)view.findViewById(R.id.channelList);
//        mSwipeRefreshLayout = (SwipeRefreshLayout)view.findViewById(R.id.swipeRefreshLayout);
//        //mAdapter = new SwipeChannelRecyclerViewAdapter(mChannels);
//        mAdapter = new ChannelRecyclerViewAdapter(this.getContext(), mChannels, new ChannelRecyclerViewAdapter.ChannelTextViewHolderFactory());
//        mAdapter.setOnScrollToListener(new OnScrollToListener() {
//
//            public void scrollVerticallyToPosition(int position) {
//                RecyclerView.LayoutManager lm = mRecyclerView.getLayoutManager();
//
//                if (lm != null && lm instanceof LinearLayoutManager) {
//                    ((LinearLayoutManager) lm).scrollToPositionWithOffset(position, 0);
//                } else {
//                    lm.scrollToPosition(position);
//                }
//            }
//
//            @Override
//            public void scrollTo(int position) {
//                scrollVerticallyToPosition(position - 1);
//            }
//        });
//        initView();
//        return view;
//    }
//
//    public void onItemClick(View view, Channel data) {
//        Intent intent = new Intent();
//        intent.setClass(ReaderApp.getContext(), BlogListActivity.class);
//        Bundle mBundle = new Bundle();
//        mBundle.putSerializable("channel", data);
//        intent.putExtras(mBundle);
//        startActivity(intent);
//    }
//
//    public void onItemLongClick(View view, Channel data) {
//
//    }
//
//
//    public void onRefresh() {
//        mSwipeRefreshLayout.setRefreshing(true);
//
//        new ChannelTask(mSwipeRefreshLayout).execute(getToken());
//    }
//
//
//    public void onLoadMore() {
//
//    }
//
//    class ChannelTask extends AsyncTask<String, Void, List<Channel>>{
//        private SwipeRefreshLayout mSwipeRefreshLayout;
//
//        public ChannelTask(SwipeRefreshLayout mSwipeRefreshLayout){
//            this.mSwipeRefreshLayout = mSwipeRefreshLayout;
//        }
//
//        protected List<Channel> doInBackground(String... urls) {
//
//            RssParser parser = new FeedlyParser(urls[0]);
//            try {
//                List<Channel> channels = parser.loadData();
//
//                PreferencesUtil.saveChannels(channels);
//
//                return channels;
//            }catch (Exception e){
//                return null;
//            }
//        }
//
//        protected void onPostExecute(List<Channel> channels) {
//            if(channels != null){
//                mChannels.clear();
//                mChannels.addAll(channels);
//                mAdapter.notifyDataSetChanged();
//            }
//
//            mSwipeRefreshLayout.setRefreshing(false);
//        }
//    }
//}