package com.lgq.rssreader.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.IdRes;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.lgq.rssreader.BlogListActivity;
import com.lgq.rssreader.R;

import com.lgq.rssreader.abstraction.FeedlyParser;
import com.lgq.rssreader.abstraction.RssParser;
import com.lgq.rssreader.adapter.BaseRecyclerViewAdapter;
import com.lgq.rssreader.adapter.ChannelRecyclerViewAdapter;
import com.lgq.rssreader.adapter.DialogAdapter;
import com.lgq.rssreader.adapter.ImageRecyclerViewAdapter;
import com.lgq.rssreader.adapter.OnRecyclerViewItemClickListener;
import com.lgq.rssreader.controls.EndlessRecyclerOnScrollListener;
import com.lgq.rssreader.core.ReaderApp;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.recyclerview.animators.SlideInUpAnimator;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.example.captain_miao.grantap.CheckPermission;
import com.example.captain_miao.grantap.listeners.PermissionListener;
import com.lgq.rssreader.BlogListActivity;
import com.lgq.rssreader.R;
import com.lgq.rssreader.abstraction.FeedlyParser;
import com.lgq.rssreader.abstraction.RssParser;
import com.lgq.rssreader.adapter.BaseRecyclerViewAdapter;
import com.lgq.rssreader.adapter.ChannelRecyclerViewAdapter;
import com.lgq.rssreader.adapter.OnRecyclerViewItemClickListener;
import com.lgq.rssreader.adapter.OnScrollToListener;
import com.lgq.rssreader.controls.EndlessRecyclerOnScrollListener;
import com.lgq.rssreader.core.Constant;
import com.lgq.rssreader.core.ReaderApp;
import com.lgq.rssreader.model.Blog;
import com.lgq.rssreader.model.Channel;
import com.lgq.rssreader.model.ImageRecord;
import com.lgq.rssreader.model.RssAction;
import com.lgq.rssreader.task.ChannelMarkTask;
import com.lgq.rssreader.task.DownloadTask;
import com.lgq.rssreader.util.PreferencesUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import jp.wasabeef.recyclerview.animators.SlideInUpAnimator;

/**
 * Created by redel on 2015-09-03.
 */
public class GalleryFragment extends BaseFragment {

    /**
     * The fragment's data source of gallery tab
     */
    private List<ImageRecord> records = new ArrayList<>();

    /**
     * The current adapter for list.
     */
    private ImageRecyclerViewAdapter mAdapter;

    private int page;

    private RecyclerView mRecyclerView;

    private ImageView currentImage;

    private SwipeRefreshLayout mSwipeRefreshLayout;

    private EndlessRecyclerOnScrollListener scrollListener = new EndlessRecyclerOnScrollListener() {
        @Override
        public void onBottom() {
            super.onBottom();
            mSwipeRefreshLayout.setRefreshing(true);
            GalleryFragment.this.onLoadMore();
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_gallery, container, false);
        currentImage = (ImageView)rootView.findViewById(R.id.currentImage);
        mRecyclerView = (RecyclerView)rootView.findViewById(R.id.imageList);
        mAdapter = new ImageRecyclerViewAdapter(getContext(), records, new ImageRecyclerViewAdapter.ImageViewHolderFactory());
        mSwipeRefreshLayout = (SwipeRefreshLayout)rootView.findViewById(R.id.swipeRefreshLayout);
        initView();
        page = 1;

        loadData();

        return rootView;
    }

    private void initView(){
        mRecyclerView.setLayoutManager(new LinearLayoutManager(ReaderApp.getContext()));
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setItemAnimator(new SlideInUpAnimator());

        mAdapter.setOnItemClickListener(new OnRecyclerViewItemClickListener<ImageRecord>() {
            @Override
            public void onItemClick(View view, ImageRecord data) {
                File SDFile = android.os.Environment.getExternalStorageDirectory();

                Bitmap bm = BitmapFactory.decodeFile(SDFile.getAbsolutePath() + data.getStoredName());
                currentImage.setImageBitmap(bm);
            }

            @Override
            public void onItemLongClick(View view, ImageRecord data) {

            }
        });

        mSwipeRefreshLayout.setColorSchemeResources(R.color.md_green_600);
        mSwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light, android.R.color.holo_orange_light, android.R.color.holo_red_light);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //mSwipeRefreshLayout.setRefreshing(true);

                //GalleryFragment.this.onRefresh();
            }
        });

        mRecyclerView.addOnScrollListener(scrollListener);
    }

    public void onLoadMore(){
        page++;
        records.addAll(ImageRecord.find(ImageRecord.class, "", new String[]{}, "", "TIME_STAMP DESC", Integer.toString(page * 15)+ ",15"));

        //records.clear();
        //records.addAll(ImageRecord.find(ImageRecord.class, "", new String[]{}, "", "TIME_STAMP DESC", Integer.toString(page * 15)+ ",15"));

        mSwipeRefreshLayout.setRefreshing(false);
    }

    public void loadData(){
        records.addAll(ImageRecord.find(ImageRecord.class, "", new String[]{}, "", "TIME_STAMP DESC", "0,15"));
        if(records.size() > 0){
            File SDFile = android.os.Environment.getExternalStorageDirectory();

            Bitmap bm = BitmapFactory.decodeFile(SDFile.getAbsolutePath() + records.get(0).getStoredName());
            currentImage.setImageBitmap(bm);
        }
    }

//    @Override
//    public boolean onContextItemSelected(android.view.MenuItem item) {
//
//        if (bMenu) {
//            bMenu=false;
//        }
//
//        return super.onContextItemSelected(item);
//    }
//
//    boolean bMenu=true;
//
//    @Override
//    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
//        android.view.MenuInflater inflater = this.getActivity().getMenuInflater();
//        inflater.inflate(R.menu.contextmenu, (Menu) menu);
//        super.onCreateContextMenu(menu, v, menuInfo);
//        bMenu=true;
//    }
}