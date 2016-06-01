package com.lgq.rssreader.fragment;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lgq.rssreader.BlogListActivity;
import com.lgq.rssreader.MainActivity;
import com.lgq.rssreader.R;
import com.lgq.rssreader.abstraction.FeedlyParser;
import com.lgq.rssreader.abstraction.RssParser;
import com.lgq.rssreader.adapter.BaseRecyclerViewAdapter;
import com.lgq.rssreader.adapter.ChannelRecyclerViewAdapter;
import com.lgq.rssreader.adapter.DialogAdapter;
import com.lgq.rssreader.adapter.OnScrollToListener;
import com.lgq.rssreader.core.Constant;
import com.lgq.rssreader.core.ReaderApp;
import com.lgq.rssreader.model.Channel;
import com.lgq.rssreader.util.PreferencesUtil;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.GridHolder;
import com.orhanobut.dialogplus.OnItemClickListener;

import java.util.List;

/**
 * Created by redel on 2015-09-03.
 */
public class ChannelListFragment extends BaseListFragment<Channel> {

    private static String CHANNELS = "channels";
    private ChannelRecyclerViewAdapter mAdapter;
    private List<Channel> mChannels;

    public static final ChannelListFragment newInstance()
    {
        ChannelListFragment fragment = new ChannelListFragment();
        fragment.setLayout(R.id.channelList);
        return fragment;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mChannels = PreferencesUtil.getChannels();
        mAdapter = new ChannelRecyclerViewAdapter(this.getContext(), mChannels, new ChannelRecyclerViewAdapter.ChannelTextViewHolderFactory());
        mAdapter.setOnScrollToListener(new OnScrollToListener() {

            public void scrollVerticallyToPosition(int position) {
                RecyclerView.LayoutManager lm = getRecyclerView().getLayoutManager();

                if (lm != null && lm instanceof LinearLayoutManager) {
                    ((LinearLayoutManager) lm).scrollToPositionWithOffset(position, 0);
                } else {
                    lm.scrollToPosition(position);
                }
            }

            @Override
            public void scrollTo(int position) {
                scrollVerticallyToPosition(position - 1);
            }
        });
        setLayout(R.id.channelList);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public List<Channel> loadData() {
        return mChannels;
    }

    @Override
    public void onItemClick(View view, Channel data) {
        Intent intent = new Intent();
        intent.setClass(getActivity(), BlogListActivity.class);
        Bundle mBundle = new Bundle();
        mBundle.putSerializable("channel", data);
        intent.putExtras(mBundle);
        startActivityForResult(intent, Constant.BLOG_LIST);
    }

    @Override
    public void onItemLongClick(View view, Channel data) {
        DialogPlus dialog = DialogPlus.newDialog(getContext())
                .setAdapter(new DialogAdapter(getContext()))
                .setContentHolder(new GridHolder(4))
                .setGravity(Gravity.BOTTOM)
                .setOnItemClickListener(new OnItemClickListener() {
                    @Override
                    public void onItemClick(DialogPlus dialog, Object item, View view, int position) {
                    }
                })
                .setExpanded(true, 450)  // This will enable the expand feature, (similar to android L share dialog)
                .create();
        dialog.show();
    }

    @Override
    public void onRefresh() {
        this.getSwipeRefreshLayout().setRefreshing(true);

        new ChannelTask(this.getSwipeRefreshLayout()).execute(getToken());
    }

    @Override
    public void onLoadMore() {

    }

    @Override
    public BaseRecyclerViewAdapter getAdapter() {
        return mAdapter;
    }

    class ChannelTask extends AsyncTask<String, Void, List<Channel>>{
        private SwipeRefreshLayout mSwipeRefreshLayout;

        public ChannelTask(SwipeRefreshLayout mSwipeRefreshLayout){
            this.mSwipeRefreshLayout = mSwipeRefreshLayout;
        }

        protected List<Channel> doInBackground(String... urls) {

            RssParser parser = new FeedlyParser(urls[0]);
            try {
                List<Channel> channels = parser.loadData();

                PreferencesUtil.saveChannels(channels);

                return channels;
            }catch (Exception e){
                return null;
            }
        }

        protected void onPostExecute(List<Channel> channels) {
            if(channels != null){
                mChannels.clear();
                mChannels.addAll(channels);
                mAdapter.notifyDataSetChanged();
            }

            mSwipeRefreshLayout.setRefreshing(false);
        }
    }
}