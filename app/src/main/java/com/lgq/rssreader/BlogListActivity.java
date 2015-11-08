package com.lgq.rssreader;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.lgq.rssreader.abstraction.FeedlyParser;
import com.lgq.rssreader.abstraction.RssParser;
import com.lgq.rssreader.adapter.BlogRecyclerViewAdapter;
import com.lgq.rssreader.model.Blog;
import com.lgq.rssreader.model.Channel;
import com.lgq.rssreader.task.BlogTask;
import com.lgq.rssreader.task.PageTask;
import com.lgq.rssreader.util.PreferencesUtil;
import com.marshalchen.ultimaterecyclerview.RecyclerItemClickListener;
import com.marshalchen.ultimaterecyclerview.UltimateRecyclerView;
import com.marshalchen.ultimaterecyclerview.itemTouchHelper.SimpleItemTouchHelperCallback;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;

public class BlogListActivity extends AppCompatActivity {
    private TextView channelTitle;
    private UltimateRecyclerView mRecyclerView;
    public UltimateRecyclerView getRecyclerView(){
        return mRecyclerView;
    }
    private BlogRecyclerViewAdapter mAdapter;
    public BlogRecyclerViewAdapter getBlogRecyclerViewAdapter() {
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
    private int selected_position = -1;
    private static final String LIST_STATE = "listState";
    private Parcelable mListState = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blogs);
        initData();
        initView();
        isInited = true;
    }

    @Override
    protected void onResume(){
        super.onResume();

        initData();
        if(mBlogs.size() == 0){
            mRecyclerView.showEmptyView();
        }else{
            mAdapter.notifyDataSetChanged();
        }

        if (mListState != null)
            mRecyclerView.onRestoreInstanceState(mListState);
        mListState = null;
    }

//    @Override
//    public void onSaveInstanceState(Bundle savedInstanceState) {
//        super.onSaveInstanceState(savedInstanceState);
//        savedInstanceState.putInt("index", selected_position);
//    }
//
//    @Override
//    public void onRestoreInstanceState(Bundle savedInstanceState){
//        super.onRestoreInstanceState(savedInstanceState);
//        selected_position = savedInstanceState.getInt("index");
//        mRecyclerView.scrollVerticallyToPosition(selected_position);
//    }

    @Override
    protected void onRestoreInstanceState(Bundle state) {
        super.onRestoreInstanceState(state);
        mListState = state.getParcelable(LIST_STATE);
    }

    @Override
    protected void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);
        mListState = mRecyclerView.onSaveInstanceState();
        state.putParcelable(LIST_STATE, mListState);
    }

    private void initData(){
        Bundle b = getIntent().getExtras();
        mChannel = (Channel)b.getSerializable("channel");
        mBlogs.clear();
        mBlogs.addAll(Blog.find(Blog.class, "CHANNEL_ID=?", new String[]{mChannel.getChannelId()}, "", "TIME_STAMP DESC", "30"));
    }

    /**
     * 初始化布局
     */
    private void initView() {
        //实例化控件
        channelTitle = (TextView) findViewById(R.id.channel_title);
        mRecyclerView = (UltimateRecyclerView)findViewById(R.id.blogList);
        mAdapter = new BlogRecyclerViewAdapter(this, mBlogs);
        mRecyclerView.setAdapter(mAdapter);
        //mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.blogSwipeRefreshLayout);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        //mRecyclerView.setDefaultSwipeToRefreshColorScheme(getResources().getColor(android.R.color.darker_gray));
        mRecyclerView.setEmptyView(getResources().getIdentifier("empty_view", "layout", getPackageName()));
//        if(mBlogs.size() == 0){
//            mRecyclerView.showEmptyView();
//        }
        //设置对应属性
        channelTitle.setText(mChannel.getTitle() + " (" + String.valueOf(mChannel.getUnreadCount()) + ")");
        channelTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRecyclerView.scrollVerticallyToPosition(0);
            }
        });

//        mSwipeRefreshLayout.setColorSchemeResources(R.color.md_green_600);
//        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                mSwipeRefreshLayout.setRefreshing(true);
//
//                if (handler != null) {
//                    // 构建消息对象
//                    Message childMsg = handler.obtainMessage();
//                    childMsg.what = 1;
//                    handler.sendMessage(childMsg);
//                    Log.i("RssReader", "发送给子线程的消息 - " + (String) childMsg.obj);
//                }
//            }
//        });

//        mAdapter.setOnItemClickListener(new OnRecyclerViewItemClickListener<Blog>() {
//            @Override
//            public void onItemClick(View view, Blog data) {
//                Intent intent = new Intent();
//                intent.setClass(BlogListActivity.this, ContentActivity.class);
//                Bundle mBundle = new Bundle();
//                mBundle.putSerializable("blog", data);
//                intent.putExtras(mBundle);
//                startActivity(intent);
//            }
//        });

        mRecyclerView.enableLoadmore();

        mRecyclerView.setOnLoadMoreListener(new UltimateRecyclerView.OnLoadMoreListener() {
            @Override
            public void loadMore(int itemsCount, final int maxLastVisiblePosition) {
                Log.d("RssReader", "Start load more");

                new PageTask(mChannel, mBlogs, mRecyclerView, mAdapter).executeOnExecutor(Executors.newCachedThreadPool(), PreferencesUtil.getAccessToken());
            }
        });

        mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this, new RecyclerItemClickListener.OnItemClickListener(){
            @Override
            public void onItemClick(View view, int i) {
                selected_position = i;
            Intent intent = new Intent();
            intent.setClass(BlogListActivity.this, ContentActivity.class);
            Bundle mBundle = new Bundle();
            mBundle.putString("blog", mAdapter.getItem(i).getBlogId());
            mBundle.putSerializable("channel", mChannel);
            intent.putExtras(mBundle);
            startActivity(intent);
            }
        }));

//        mRecyclerView.setParallaxHeader(getLayoutInflater().inflate(R.layout.parallax_recyclerview_header, mRecyclerView.mRecyclerView, false));

//        mRecyclerView.setOnParallaxScroll(new UltimateRecyclerView.OnParallaxScroll() {
//            @Override
//            public void onParallaxScroll(float percentage, float offset, View parallax) {
//                Drawable c = toolbar.getBackground();
//                c.setAlpha(Math.round(127 + percentage * 128));
//                toolbar.setBackgroundDrawable(c);
//            }
//        });

        mRecyclerView.setDefaultOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new BlogTask(mChannel, mBlogs, mRecyclerView, mAdapter).executeOnExecutor(Executors.newCachedThreadPool(), PreferencesUtil.getAccessToken());
            }
        });


//        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(mAdapter);
//        mItemTouchHelper = new ItemTouchHelper(callback);
//        mItemTouchHelper.attachToRecyclerView(mRecyclerView.mRecyclerView);
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