package com.lgq.rssreader;

import android.app.FragmentManager;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.lgq.rssreader.abstraction.FeedlyParser;
import com.lgq.rssreader.abstraction.RssParser;
import com.lgq.rssreader.adapter.ViewPagerAdapter;
import com.lgq.rssreader.fragment.FragmentCallback;
import com.lgq.rssreader.fragment.ListFragment;
import com.lgq.rssreader.fragment.TitleFragment;
import com.lgq.rssreader.model.Channel;
import com.lgq.rssreader.util.PreferencesUtil;

import java.util.List;

public class MainActivity extends AppCompatActivity {// implements FragmentCallback

    private Toolbar mToolbar;
    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;
    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private ActionBarDrawerToggle mActionBarDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        Fragment homeFragment = new ListFragment();
        Fragment bestFragment = new TitleFragment();
        Fragment candidateFragment = new TitleFragment();
        Fragment recommendFragment = new TitleFragment();
        adapter.addFragment(homeFragment, "首页");
        adapter.addFragment(bestFragment, "精华");
        adapter.addFragment(candidateFragment, "候选");
        adapter.addFragment(recommendFragment, "推荐");
        mViewPager.setAdapter(adapter);
        mTabLayout.setupWithViewPager(mViewPager);
    }

    public void callback(String content){

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

    /**
     * 初始化按键控制
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK ) {


            return true;
        }else if(keyCode==KeyEvent.KEYCODE_SEARCH){

            return true;
        }else if(keyCode==KeyEvent.KEYCODE_MENU){


            return true;
        }else {
            return super.onKeyDown(keyCode, event);
        }
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
        }

        return super.onOptionsItemSelected(item);
    }

    class ChannelTask extends AsyncTask<String, Void, List<Channel>> {
        private ListFragment home;

        public ChannelTask(){
            ViewPagerAdapter adapter = (ViewPagerAdapter)mViewPager.getAdapter();
            home = (ListFragment)adapter.getItem(0);
        }

        protected List<Channel> doInBackground(String... urls) {

            String token = urls[0];

            RssParser parser = new FeedlyParser(token);
            try {
                List<Channel> channels = parser.loadData();

                if(channels != null){
                    PreferencesUtil.saveChannels(channels);
                }

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
                if(home != null && channels != null){
                    home.getChannels().clear();
                    home.getChannels().addAll(channels);
                    home.getChannelRecyclerViewAdapter().notifyDataSetChanged();
                }

                if(home != null && home.getSwipeRefreshLayout() != null)
                    home.getSwipeRefreshLayout().setRefreshing(false);
            }else{
                Toast.makeText(MainActivity.this, "同步数据失败", Toast.LENGTH_SHORT).show();
            }
        }
    }
}