package com.lgq.rssreader;

import android.app.FragmentManager;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.lgq.rssreader.abstraction.FeedlyParser;
import com.lgq.rssreader.abstraction.RssParser;
import com.lgq.rssreader.adapter.ViewPagerAdapter;
import com.lgq.rssreader.core.Constant;
import com.lgq.rssreader.fragment.BlogListFragment;
import com.lgq.rssreader.fragment.ChannelListFragment;
import com.lgq.rssreader.fragment.FavListFragment;
import com.lgq.rssreader.fragment.FragmentCallback;
//import com.lgq.rssreader.fragment.ListFragment;
import com.lgq.rssreader.fragment.GalleryFragment;
import com.lgq.rssreader.fragment.SubscribeListFragment;
import com.lgq.rssreader.fragment.TitleFragment;
import com.lgq.rssreader.fragment.UnreadListFragment;
import com.lgq.rssreader.model.Blog;
import com.lgq.rssreader.model.Channel;
import com.lgq.rssreader.model.Profile;
import com.lgq.rssreader.model.RssAction;
import com.lgq.rssreader.task.ChannelMarkTask;
import com.lgq.rssreader.util.PreferencesUtil;
import com.lgq.rssreader.util.ThemeUtil;
import com.lgq.rssreader.util.VibrateUtil;
import com.orm.StringUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {// implements FragmentCallback

    private Toolbar mToolbar;
    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;
    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private ActionBarDrawerToggle mActionBarDrawerToggle;
    private BottomSheetBehavior behavior;

    public BottomSheetBehavior getBottomSheetBehavior(){
        return behavior;
    }

    private static Boolean isExit = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemeUtil.onActivityCreateSetTheme(this);
        setContentView(R.layout.activity_main);
        initView();
    }

    /**
     * 初始化布局
     */
    private void initView() {
        //实例化控件
        mToolbar = (Toolbar) findViewById(R.id.activity_toolbar);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.dl_drawer);
        mTabLayout = (TabLayout) findViewById(R.id.tl_main_tabs);
        mNavigationView = (NavigationView) findViewById(R.id.nv_main_menu);
        mViewPager = (ViewPager) findViewById(R.id.vp_main_content);
        //设置对应属性
        initToolBar();
        initMainContent();
        initAction();
        updateChannels();
        initBehaviour();
    }

    private void initBehaviour(){
        CoordinatorLayout coordinatorLayout = (CoordinatorLayout) findViewById(R.id.rl);
        View bottomSheet = coordinatorLayout.findViewById(R.id.bottom_sheet);
        behavior = BottomSheetBehavior.from(bottomSheet);
    }

    private void updateChannels(){
        if(PreferencesUtil.getAccessToken().length() > 0){
            new ChannelTask().execute(PreferencesUtil.getAccessToken());
        }
    }

    /**
     * 初始化控件的动作监听
     */
    private void initAction() {
        //设置侧滑菜单点击监听
        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                int id = menuItem.getItemId();
                switch (id) {
                    //返回首页
                    case R.id.nav_home:
                        mViewPager.setCurrentItem(0);
                        break;
                    case R.id.nav_setting:

                        Intent intent = new Intent();
                        intent.setClass(MainActivity.this, SettingsActivity.class);
                        startActivity(intent);

                        break;
                }
                mDrawerLayout.closeDrawers();
                return false;
            }
        });
    }

    /**
     * 初始化TabLayout和ViewPager
     */
    private void initMainContent() {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        Fragment homeFragment = new ChannelListFragment();
        Fragment allFragment = BlogListFragment.newInstance();
        Fragment unreadFragment = UnreadListFragment.newInstance();
        Fragment favFragment = FavListFragment.newInstance();
        Fragment galleryFragment = new GalleryFragment();
        adapter.addFragment(homeFragment, MainActivity.this.getString(R.string.tab_main));
        adapter.addFragment(allFragment, MainActivity.this.getString(R.string.tab_all));
        adapter.addFragment(unreadFragment, MainActivity.this.getString(R.string.tab_unread));
        adapter.addFragment(favFragment, MainActivity.this.getString(R.string.tab_star));
        adapter.addFragment(galleryFragment, MainActivity.this.getString(R.string.tab_gallery));
        mViewPager.setAdapter(adapter);
        mTabLayout.setupWithViewPager(mViewPager);
    }

    /**
     * 初始化ToolBar
     */
    private void initToolBar() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mActionBarDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.drawer_open, R.string.drawer_close);
        mActionBarDrawerToggle.syncState();
        mDrawerLayout.setDrawerListener(mActionBarDrawerToggle);
    }

    private void exitBy2Click()
    {
        Timer tExit = null;
        if(isExit == false ) {
            isExit = true;
            Toast.makeText(this, getResources().getString(R.string.main_exit), Toast.LENGTH_SHORT).show();
            tExit = new Timer();
            tExit.schedule(new TimerTask() {
                @Override
                public void run() {
                    isExit = false;
                }
            }, 2000);
        }else{
            finish();
            //android.os.Process.killProcess(android.os.Process.myPid());
        }
    }

    /**
     * 初始化按键控制
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK ) {
            // 监控返回键
//            new AlertDialog.Builder(MainActivity.this).setTitle("提示")
//                    .setIconAttribute(android.R.attr.alertDialogIcon)
//                    .setMessage("确定要退出吗?")
//                    .setPositiveButton("确认", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            MainActivity.this.finish();
//                        }})
//                    .setNegativeButton("取消", null)
//                    .create().show();
            exitBy2Click();
            return false;
        }else if(keyCode==KeyEvent.KEYCODE_SEARCH){

            return true;
        }else if(keyCode==KeyEvent.KEYCODE_MENU){
            mDrawerLayout.openDrawer(Gravity.LEFT);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        MenuItem menuSearchItem = menu.findItem(R.id.action_search);

        // Get the SearchView and set the searchable configuration
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menuSearchItem.getActionView();

        // Assumes current activity is the searchable activity
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(true);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(resultCode){
            case Constant.ADD_SUBSCRIBE:
                new ChannelTask().execute(PreferencesUtil.getAccessToken());
                break;
            case Constant.ThemeChanged:
                ThemeUtil.changeToTheme(this);
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_settings) {

            Intent intent = new Intent();
            intent.setClass(this, SettingsActivity.class);
            startActivity(intent);

            return true;
        }else if(id == R.id.action_sync) {
            new ChannelTask().execute(PreferencesUtil.getAccessToken());

            return true;
        }else if(id == R.id.action_add) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            // Get the layout inflater
            LayoutInflater inflater = this.getLayoutInflater();

            final View view = inflater.inflate(R.layout.fragment_dialog, null);

            // Inflate and set the layout for the dialog
            // Pass null as the parent view because its going in the dialog layout
            builder.setView(view)
                    .setTitle(R.string.action_add)
                    // Add action buttons
                    .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {

                            CharSequence input = ((TextView)view.findViewById(R.id.input)).getText();

                            if(input != null && input.length() > 0){
                                Intent i = new Intent(MainActivity.this, SubscribeActivity.class);

                                Bundle b = new Bundle();
                                b.putCharSequence(SubscribeListFragment.SUBSCRIBE_QUERY, input);
                                i.putExtras(b);

                                startActivityForResult(i, Constant.ADD_SUBSCRIBE);
                            }

                            dialog.dismiss();
                        }
                    })
                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            builder.create().show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    class ChannelTask extends AsyncTask<String, Void, List<Channel>> {
        private ChannelListFragment home;

        public ChannelTask(){
            ViewPagerAdapter adapter = (ViewPagerAdapter)mViewPager.getAdapter();
            home = (ChannelListFragment)adapter.getItem(0);
        }

        private void markAsRead(List<String> ids){
            String whereClause = "";
            String[] whereArgs = new String[ids.size()];
            for(int i = ids.size() - 1; i>=0;i--){
                whereClause = whereClause + StringUtil.toSQLName("blogID") + "=?";
                if(i != 0){
                    whereClause = whereClause + " OR ";
                }
                whereArgs[i] = ids.get(i);
            }

            List<Blog> exists = Blog.find(Blog.class, whereClause, whereArgs);
            if(exists.size() > 0) {
                for (Blog b : exists) {
                    b.setIsRead(true);
                    Log.i("RssReader", "set " + b.getTitle() + " as read");
                }

                Blog[] tmp = new Blog[exists.size()];
                Blog.saveInTx(exists.toArray(tmp));
            }
        }

        protected List<Channel> doInBackground(String... urls) {

            RssParser parser = new FeedlyParser(urls[0]);
            try {
                List<Channel> channels = parser.loadData();

                if(channels != null){
                    PreferencesUtil.saveChannels(channels);
                }

                Profile p = PreferencesUtil.getProfile();
                long lastSyncTime = PreferencesUtil.getLastSyncTime();

                List<String> ids = parser.sync(p.getId(), null, lastSyncTime);

                if(ids.size() > 100){
                    for(int i=0;i<ids.size();i = i+100){
                        if(i + 100 > ids.size())
                            markAsRead(ids.subList(i, ids.size() - 1));
                        else
                            markAsRead(ids.subList(i, i+100));
                    }
                }else{
                    markAsRead(ids);
                }

                PreferencesUtil.saveLastSyncTime(System.currentTimeMillis());

                return channels;
            }catch (Exception e){
                return null;
            }
        }

        @Override
        protected void onPreExecute() {
            if(home != null && home.getSwipeRefreshLayout() != null)
                home.getSwipeRefreshLayout().setRefreshing(true);
        }

        protected void onPostExecute(List<Channel> channels) {
            if(channels != null){
                if(home != null && channels != null && home.getChannelRecyclerViewAdapter() != null){
                    home.getData().clear();
                    home.getData().addAll(channels);
                    home.getChannelRecyclerViewAdapter().notifyDataSetChanged();
                }

                if(home != null && home.getSwipeRefreshLayout() != null)
                    home.getSwipeRefreshLayout().setRefreshing(false);

                VibrateUtil.vibrate();
            }else{
                Toast.makeText(MainActivity.this, "同步数据失败", Toast.LENGTH_SHORT).show();
            }
        }
    }
}