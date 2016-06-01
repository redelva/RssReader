package com.lgq.rssreader;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.lgq.rssreader.adapter.ComplexBlogRecyclerViewAdapter;
import com.lgq.rssreader.adapter.OnRecyclerViewItemClickListener;
import com.lgq.rssreader.controls.EndlessRecyclerOnScrollListener;
import com.lgq.rssreader.core.AppSettings;
import com.lgq.rssreader.core.Constant;
import com.lgq.rssreader.enums.FromType;
import com.lgq.rssreader.enums.ItemType;
import com.lgq.rssreader.model.Blog;
import com.lgq.rssreader.model.Channel;
import com.lgq.rssreader.task.BlogTask;
import com.lgq.rssreader.task.PageTask;
import com.lgq.rssreader.util.BroadcastReceiverEx;
import com.lgq.rssreader.util.PreferencesUtil;
import com.lgq.rssreader.util.ThemeUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

public class BlogListActivity extends AppCompatActivity {
    private TextView channelTitle;
    private RecyclerView mRecyclerView;
    public RecyclerView getRecyclerView(){
        return mRecyclerView;
    }
    private ComplexBlogRecyclerViewAdapter mAdapter;
    public ComplexBlogRecyclerViewAdapter getBlogRecyclerViewAdapter() {
        return mAdapter;
    }
    private SwipeRefreshLayout mSwipeRefreshLayout;
    public SwipeRefreshLayout getSwipeRefreshLayout(){
        return mSwipeRefreshLayout;
    }
    private ItemTouchHelper mItemTouchHelper;
    private Channel mChannel;
    private List<Blog> mBlogs = new ArrayList<>();
    private boolean isInited = false;
    private boolean themeChanged = false;
    private int selected_position = -1;
    private static final String LIST_STATE = "listState";
    private ImageView popupButton;
    private BroadcastReceiverEx broadcastReceiverEx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemeUtil.onActivityCreateSetTheme(this);
        setContentView(R.layout.activity_blogs);
    }

    @Override
    protected void onStart() {
        super.onStart();

        initData();
        initView();
        isInited = true;
    }

    private void showPopupMenu(final View view) {
        // Create a PopupMenu, giving it the clicked view for an anchor
        PopupMenu popup = new PopupMenu(this, view);

        // Inflate our menu resource into the PopupMenu's Menu
        popup.getMenuInflater().inflate(R.menu.popup, popup.getMenu());

        // Set a listener so we are notified if a menu item is clicked
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                AppSettings settings = PreferencesUtil.getAppSettings();
                switch (menuItem.getItemId()) {
                    case R.id.menu_simple:
                        mAdapter.setItemType(ItemType.Small);
                        //mAdapter = new BlogRecyclerViewAdapter(BlogListActivity.this, mBlogs, new BlogRecyclerViewAdapter.BlogTextViewHolderFactory());
                        view.setTag(ItemType.Small);
                        mAdapter.notifyDataSetChanged();
                        settings.setItemType(ItemType.Small);
                        PreferencesUtil.saveAppSettings(settings);
                        return true;
                    case R.id.menu_complex:
                        mAdapter.setItemType(ItemType.Big);
                        //mAdapter = new BlogRecyclerViewAdapter(BlogListActivity.this, mBlogs, new BlogRecyclerViewAdapter.BlogTextViewHolderFactory());
                        view.setTag(ItemType.Big);
                        settings.setItemType(ItemType.Big);
                        PreferencesUtil.saveAppSettings(settings);
                        mAdapter.notifyDataSetChanged();
                        return true;
                }
                return false;
            }
        });

        // Finally show the PopupMenu
        popup.show();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putInt(LIST_STATE, ((LinearLayoutManager)mRecyclerView.getLayoutManager()).findFirstCompletelyVisibleItemPosition());
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState){
        super.onRestoreInstanceState(savedInstanceState);
        selected_position = savedInstanceState.getInt(LIST_STATE);
        //mRecyclerView.scrollVerticallyToPosition(selected_position);
    }

    private void initData(){
        Bundle b = getIntent().getExtras();
        mChannel = (Channel)b.getSerializable("channel");
        themeChanged = b.containsKey("themeChanged");
        mBlogs.clear();
        if(mChannel.getIsDirectory())
            mBlogs.addAll(Blog.find(Blog.class, "TAG_ID=?", new String[]{mChannel.getChannelId()}, "", "TIME_STAMP DESC", "30"));
        else
            mBlogs.addAll(Blog.find(Blog.class, "CHANNEL_ID=?", new String[]{mChannel.getChannelId()}, "", "TIME_STAMP DESC", "30"));
    }

    /**
     * 初始化布局
     */
    private void initView() {
        //实例化控件
        channelTitle = (TextView) findViewById(R.id.channel_title);
        mRecyclerView = (RecyclerView)findViewById(R.id.blogList);
        mAdapter = new ComplexBlogRecyclerViewAdapter(this, mBlogs, PreferencesUtil.getAppSettings().getItemType());
        mRecyclerView.setAdapter(mAdapter);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.blogSwipeRefreshLayout);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        popupButton = (ImageView)findViewById(R.id.button_popup);
        popupButton.setTag(ItemType.Small);
        popupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupMenu(v);
            }
        });

        //设置对应属性
        channelTitle.setText(mChannel.getTitle() + " (" + String.valueOf(mChannel.getUnreadCount()) + ")");
        channelTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRecyclerView.smoothScrollToPosition(0);
            }
        });

        mSwipeRefreshLayout.setColorSchemeResources(R.color.md_green_600);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSwipeRefreshLayout.setRefreshing(true);

                new BlogTask(mChannel, mBlogs, mAdapter, mSwipeRefreshLayout).executeOnExecutor(Executors.newCachedThreadPool(), PreferencesUtil.getAccessToken());
            }
        });

        mAdapter.setOnItemClickListener(new OnRecyclerViewItemClickListener<Blog>() {
            @Override
            public void onItemClick(View view, Blog data) {
                Intent intent = new Intent();
                intent.setClass(BlogListActivity.this, ContentActivity.class);
                Bundle mBundle = new Bundle();
                mBundle.putString("blog", data.getBlogId());
                mBundle.putSerializable("channel", mChannel);
                mBundle.putString("from", FromType.Normal.toString());
                intent.putExtras(mBundle);
                startActivityForResult(intent, 100);
                //startActivity(intent);
            }

            @Override
            public void onItemLongClick(View view, Blog data) {

            }
        });

        mRecyclerView.setOnScrollListener(new EndlessRecyclerOnScrollListener() {
            @Override
            public void onBottom() {
                new PageTask(mChannel, mBlogs, mAdapter, mSwipeRefreshLayout).executeOnExecutor(Executors.newCachedThreadPool(), PreferencesUtil.getAccessToken());
            }
        });

//        mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this, new RecyclerItemClickListener.OnItemClickListener(){
//            @Override
//            public void onItemClick(View view, int i) {
//                selected_position = i;
//            Intent intent = new Intent();
//            intent.setClass(BlogListActivity.this, ContentActivity.class);
//            Bundle mBundle = new Bundle();
//            mBundle.putString("blog", mAdapter.getItem(i).getBlogId());
//            mBundle.putSerializable("channel", mChannel);
//            mBundle.putString("from", FromType.Normal.toString());
//            intent.putExtras(mBundle);
//            startActivityForResult(intent, 100);
//            }
//        }));

//        mRecyclerView.setParallaxHeader(getLayoutInflater().inflate(R.layout.parallax_recyclerview_header, mRecyclerView.mRecyclerView, false));

//        mRecyclerView.setOnParallaxScroll(new UltimateRecyclerView.OnParallaxScroll() {
//            @Override
//            public void onParallaxScroll(float percentage, float offset, View parallax) {
//                Drawable c = toolbar.getBackground();
//                c.setAlpha(Math.round(127 + percentage * 128));
//                toolbar.setBackgroundDrawable(c);
//            }
//        });

    }

    /**
     * 为了得到传回的数据，必须在前面的Activity中（指MainActivity类）重写onActivityResult方法
     *
     * requestCode 请求码，即调用startActivityForResult()传递过去的值
     * resultCode 结果码，结果码用于标识返回数据来自哪个新Activity
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == Constant.ThemeChanged){
            ThemeUtil.changeToTheme(this);
        }
    }

    /**
     * 初始化按键控制
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK ) {
            if(themeChanged){
                setResult(Constant.ThemeChanged);
                finish();
                return false;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}