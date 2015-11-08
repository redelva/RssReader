package com.lgq.rssreader.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.lgq.rssreader.adapter.ChannelRecyclerViewAdapter;
import com.lgq.rssreader.adapter.DialogAdapter;
import com.lgq.rssreader.adapter.OnRecyclerViewItemClickListener;
import com.lgq.rssreader.core.ReaderApp;
import com.lgq.rssreader.model.Channel;
import com.lgq.rssreader.util.PreferencesUtil;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.GridHolder;
import com.orhanobut.dialogplus.ListHolder;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.recyclerview.animators.SlideInUpAnimator;

/**
 * Created by redel on 2015-09-03.
 */
public class ListFragment extends BaseFragment {
    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    public SwipeRefreshLayout getSwipeRefreshLayout(){
        return mSwipeRefreshLayout;
    }
    private ChannelRecyclerViewAdapter mAdapter;
    public ChannelRecyclerViewAdapter getChannelRecyclerViewAdapter(){
        return mAdapter;
    }
    private List<Channel> mChannels = new ArrayList<>();
    public List<Channel> getChannels(){
        return mChannels;
    }
    private View dialogHeader;

//    private final FragmentHandler mHandler = new FragmentHandler(this);
//
//    static class FragmentHandler extends WeakReferenceHandler<ListFragment> {
//
//        public FragmentHandler(ListFragment fragment){
//            super(fragment);
//        }
//
//        public void handleMessage(ListFragment fragment, Message msg) {
//
//            switch (msg.what){
//                case 1:
//                    fragment.getSwipeRefreshLayout().setRefreshing(false);
//                    if(msg.obj != null){
//                        fragment.getChannels().addAll((List<Channel>) msg.obj);
//                        fragment.getChannelRecyclerViewAdapter().notifyDataSetChanged();
//                    }
//                    break;
//            }
//        }
//    };

    private static String CHANNELS = "channels";

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_channel, container, false);
        mChannels = PreferencesUtil.getChannels();
        mRecyclerView = (RecyclerView)view.findViewById(R.id.channelList);
        mAdapter = new ChannelRecyclerViewAdapter(getContext(), mChannels);
        mSwipeRefreshLayout = (SwipeRefreshLayout)view.findViewById(R.id.swipeRefreshLayout);
        initView();
        return view;
    }

    private void initView(){
        mRecyclerView.setLayoutManager(new LinearLayoutManager(ReaderApp.getContext()));
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setItemAnimator(new SlideInUpAnimator());

        mAdapter.setOnItemClickListener(new OnRecyclerViewItemClickListener<Channel>() {
            @Override
            public void onItemClick(View view, Channel data) {
                Intent intent = new Intent();
                intent.setClass(ReaderApp.getContext(), BlogListActivity.class);
                Bundle mBundle = new Bundle();
                mBundle.putSerializable("channel", data);
                intent.putExtras(mBundle);
                startActivity(intent);
            }

            @Override
            public void onItemLongClick(View view, Channel data) {
                dialogHeader = setDialogHeader(view, data);

                final DialogPlus dialog = DialogPlus.newDialog(view.getContext())
                        .setContentHolder(new GridHolder(4))
                        .setHeader(dialogHeader)
                        //.setFooter(R.layout.dialog_footer)
                        .setCancelable(true)
                        .setGravity(Gravity.BOTTOM)
                        .setAdapter(new DialogAdapter(view.getContext()))
                        .setExpanded(false)
                        .create();
                dialog.show();
            }
        });

        //mSwipeRefreshLayout.setColorSchemeResources(R.color.md_green_600);
        mSwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light, android.R.color.holo_orange_light, android.R.color.holo_red_light);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSwipeRefreshLayout.setRefreshing(true);

                new ChannelTask().execute(getToken());
            }
        });
    }

    private View setDialogHeader(View view, Channel mChannel){
        LayoutInflater inflater = LayoutInflater.from(view.getContext());
        View header = inflater.inflate(R.layout.dialog_header, null);
        TextView tv = (TextView)header.findViewById(R.id.dialog_title);
        tv.setText(mChannel.getTitle());

        return header;
    }

    class ChannelTask extends AsyncTask<String, Void, List<Channel>>{
        protected List<Channel> doInBackground(String... urls) {

            String token = urls[0];

            RssParser parser = new FeedlyParser(token);
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