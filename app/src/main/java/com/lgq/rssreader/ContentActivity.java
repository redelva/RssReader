package com.lgq.rssreader;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.lgq.rssreader.core.AppSettings;
import com.lgq.rssreader.core.ReaderApp;
import com.lgq.rssreader.enums.FromType;
import com.lgq.rssreader.fragment.ContentFragment;

import com.lgq.rssreader.model.Blog;
import com.lgq.rssreader.model.Channel;
import com.lgq.rssreader.model.RssAction;
import com.lgq.rssreader.model.Style;
import com.lgq.rssreader.task.BlogMarkTask;
import com.lgq.rssreader.util.ChannelUtil;
import com.lgq.rssreader.util.CollectionUtil;
import com.lgq.rssreader.util.HtmlUtil;
import com.lgq.rssreader.util.PreferencesUtil;
import com.lgq.rssreader.util.ThemeUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ContentActivity extends AppCompatActivity{
    private FloatingActionMenu menu;
    private FloatingActionButton menuRead;
    private FloatingActionButton menuSettings;
    private FloatingActionButton menuLike;
    private FloatingActionButton menuShare;
    private FloatingActionButton menuReload;
    private FloatingActionButton menuExpand;
    private FromType mFromType;
    private Channel mChannel;
    private List<Blog> mBlogs;
    private List<Blog> mReadBlogs;
    private ViewPager contents;
    private BottomSheetBehavior behavior;
    List<ContentFragment> mFragments;
    ContentAdapter adapter;
    RelativeLayout rl;
    private static ExecutorService FULL_TASK_EXECUTOR;

    public static HashMap<Integer, Style> colorMap = new HashMap<Integer, Style>(){{
        put(R.id.setting_color_black, Style.Black);
        put(R.id.setting_color_white, Style.White);
        put(R.id.setting_color_gray, Style.Gray);
        put(R.id.setting_color_green, Style.Green);
        put(R.id.setting_color_dark, Style.Dark);
    }};

    static {
        FULL_TASK_EXECUTOR = Executors.newCachedThreadPool();
    }

    public Blog getCurrentBlog(){
        return mBlogs.get(contents.getCurrentItem());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ThemeUtil.onActivityCreateSetTheme(this);

        setContentView(R.layout.activity_content);
        initData();
        initView();
        initFAB();
        initReadSetting();
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK ) {
            if(behavior.getState() == BottomSheetBehavior.STATE_EXPANDED){
                behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                return true;
            }else{
//                Intent i = new Intent();
//                i.putExtra("count", mReadBlogs.size());
//                this.setResult(Activity.RESULT_OK, i);

                Blog[] blogs = new Blog[mReadBlogs.size()];

                Blog.saveInTx(mReadBlogs.toArray(blogs));

                ChannelUtil.updateCount(mChannel, mReadBlogs.size());
            }
        }

        return super.onKeyDown(keyCode, event);
    }

    private void initData(){
        mChannel = (Channel)getIntent().getExtras().getSerializable("channel");
        mFromType = FromType.valueOf(getIntent().getExtras().getString("from"));
        Blog mBlog = Blog.find(Blog.class, "blog_id=?", new String[]{getIntent().getExtras().getString("blog")}).get(0);
        mBlogs = new ArrayList<>();
        mReadBlogs = new ArrayList<>();

        List<Blog> right = null;

        if(mFromType == FromType.Normal)
            right = Blog.find(Blog.class, "TIME_STAMP <? and (CHANNEL_ID=? or TAG_ID=?)", new String[]{String.valueOf(mBlog.getTimeStamp()), mChannel.getChannelId(), mChannel.getChannelId()}, "", "TIME_STAMP DESC", "30");
        if(mFromType == FromType.All)
            right = Blog.find(Blog.class, "TIME_STAMP <?", new String[]{String.valueOf(mBlog.getTimeStamp())}, "", "TIME_STAMP DESC", "30");
        if(mFromType == FromType.Starred)
            right = Blog.find(Blog.class, "ACTION_TIME <? and Is_Starred = 1", new String[]{String.valueOf(mBlog.getActionTime())}, "", "ACTION_TIME DESC", "30");
        if(mFromType == FromType.Unread)
            right = Blog.find(Blog.class, "TIME_STAMP <? and Is_Read = 0", new String[]{String.valueOf(mBlog.getTimeStamp())}, "", "TIME_STAMP DESC", "30");
        if(mFromType == FromType.Search) {
            String query = getIntent().getExtras().getString("query");
            right = Blog.find(Blog.class, "TIME_STAMP <? and (title like '?' and description like '?' and content like '?')", new String[]{String.valueOf(mBlog.getTimeStamp()), "%"+query+"%", "%"+query+"%", "%"+query+"%"}, "", "TIME_STAMP DESC", "30");
        }

        List<Blog> left = null;

        if(mFromType == FromType.Normal)
            left = Blog.find(Blog.class, "TIME_STAMP >? and (CHANNEL_ID=? or TAG_ID=?)", new String[]{String.valueOf(mBlog.getTimeStamp()), mChannel.getChannelId(), mChannel.getChannelId()},"","TIME_STAMP ASC", null);
        if(mFromType == FromType.All)
            left = Blog.find(Blog.class, "TIME_STAMP >?", new String[]{String.valueOf(mBlog.getTimeStamp())},"","TIME_STAMP ASC",  null);
        if(mFromType == FromType.Unread)
            left = Blog.find(Blog.class, "TIME_STAMP >? and Is_Read = 0", new String[]{String.valueOf(mBlog.getTimeStamp())},"","TIME_STAMP ASC",  null);
        if(mFromType == FromType.Starred)
            left = Blog.find(Blog.class, "ACTION_TIME >? and Is_Starred = 1", new String[]{String.valueOf(mBlog.getActionTime())},"","ACTION_TIME ASC", null);
        if(mFromType == FromType.Search) {
            String query = getIntent().getExtras().getString("query");
            left = Blog.find(Blog.class, "TIME_STAMP >? and (title like '?' and description like '?' and content like '?')", new String[]{String.valueOf(mBlog.getTimeStamp()), "%"+query+"%", "%"+query+"%", "%"+query+"%"}, "", "TIME_STAMP ASC",  null);
        }

        if(left != null) {
            cPos = left.size();
            for(int i=left.size()-1; i>=0; i--){
                mBlogs.add(left.get(i));
            }
        }
        mBlogs.add(mBlog);

        setStatus(mBlog, RssAction.AsRead);

        if(right != null) {
            mBlogs.addAll(right);
        }

        gotoGallery(mBlog);
    }

    private void setTheme(WebView mWebView, TextView mTitleView, ProgressDialog mProgressDialog, Style theme){
        mWebView.loadUrl("javascript: backgroundColor('" + theme.getWebViewBackgroundColor() + "')");
        mWebView.loadUrl("javascript: fontColor('" + theme.getWebViewFontColor() + "')");
        mTitleView.setBackgroundColor(Color.parseColor(theme.getTitleBackgroundColor()));
        mTitleView.setTextColor(Color.parseColor(theme.getTitleFontColor()));
        mProgressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.parseColor(theme.getTitleFontColor())));
    }

    private void initReadSetting(){
        final View read_settings = findViewById(R.id.reading_settings);

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = contents.getCurrentItem();
                if(adapter.getFragment(pos) != null) {
                    WebView mWebView = adapter.getFragment(pos).getWebView();
                    WebView mNextWebView = null;
                    WebView mPreWebView = null;

                    TextView mTitleView = adapter.getFragment(pos).getTitleView();
                    TextView mNextTitleView = null;
                    TextView mPreTitleView = null;

                    ProgressDialog mProgressDialog = adapter.getFragment(pos).getProgressDialog();
                    ProgressDialog mNextProgressDialog = null;
                    ProgressDialog mPreProgressDialog = null;

                    if(adapter.getCount() != pos + 1){
                        mNextWebView = adapter.getFragment(pos + 1).getWebView();
                        mNextTitleView = adapter.getFragment(pos + 1).getTitleView();
                        mNextProgressDialog = adapter.getFragment(pos + 1).getProgressDialog();
                    }

                    if(pos != 0){
                        mPreWebView = adapter.getFragment(pos - 1).getWebView();
                        mPreTitleView = adapter.getFragment(pos - 1).getTitleView();
                        mPreProgressDialog = adapter.getFragment(pos - 1).getProgressDialog();
                    }

                    AppSettings settings = PreferencesUtil.getAppSettings();

                    switch (v.getId()) {
                        case R.id.setting_smallfont:
                            settings.setFontSize(settings.getFontSize() - 1);

                            mWebView.loadUrl("javascript: fontsize(" + settings.getFontSize() + ")");
                            if(mPreWebView != null)
                                mPreWebView.loadUrl("javascript: fontsize(" + settings.getFontSize() + ")");
                            if(mNextWebView != null)
                                mNextWebView.loadUrl("javascript: fontsize(" + settings.getFontSize() + ")");
                            break;
                        case R.id.setting_bigfont:
                            settings.setFontSize(settings.getFontSize() + 1);

                            mWebView.loadUrl("javascript: fontsize(" + settings.getFontSize() + ")");
                            if(mPreWebView != null)
                                mPreWebView.loadUrl("javascript: fontsize(" + settings.getFontSize() + ")");
                            if(mNextWebView != null)
                                mNextWebView.loadUrl("javascript: fontsize(" + settings.getFontSize() + ")");
                            break;
                        case R.id.setting_color_black:
                        case R.id.setting_color_white:
                        case R.id.setting_color_gray:
                        case R.id.setting_color_green:
                        case R.id.setting_color_dark:
                            settings.setStyle(colorMap.get(v.getId()));

                            setTheme(mWebView, mTitleView, mProgressDialog, settings.getStyle());
                            if(mPreWebView != null) {
                                setTheme(mPreWebView, mPreTitleView, mPreProgressDialog, settings.getStyle());
                            }
                            if(mNextWebView != null) {
                                setTheme(mNextWebView, mNextTitleView, mNextProgressDialog, settings.getStyle());
                            }
                            setStatusBarColor(Color.parseColor(settings.getStyle().getTitleBackgroundColor()));

                            PreferencesUtil.saveAppSettings(settings);

                            ThemeUtil.changeToTheme(ContentActivity.this);

                            break;
                    }

                    PreferencesUtil.saveAppSettings(settings);
                }
            }
        };

        //read_settings.setOnClickListener(listener);

        for (int index = 0; index < ((ViewGroup)read_settings).getChildCount(); index++) {
            View view = ((ViewGroup)read_settings).getChildAt(index);
            if(view instanceof ViewGroup){
                for (int j = 0; j < ((ViewGroup)view).getChildCount(); j++) {
                    View v = ((ViewGroup) view).getChildAt(j);
                    v.setOnClickListener(listener);
                }
            }
        }
    }

    public FloatingActionMenu getMenu(){return menu;}

    public void setStatusBarColor(int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            // clear FLAG_TRANSLUCENT_STATUS flag:
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            // finally change the color
            window.setStatusBarColor(color);
        }
    }

    private void initFAB(){
        menu = (FloatingActionMenu) findViewById(R.id.menu);
        menuLike = (FloatingActionButton) findViewById(R.id.menu_like);
        menuRead = (FloatingActionButton) findViewById(R.id.menu_read);
        menuExpand = (FloatingActionButton) findViewById(R.id.menu_expand);
        menuReload = (FloatingActionButton) findViewById(R.id.menu_reload);
        menuShare = (FloatingActionButton) findViewById(R.id.menu_share);
        menuSettings = (FloatingActionButton) findViewById(R.id.menu_settings);

        Blog mBlog =  mBlogs.get(contents.getCurrentItem());

        if (mBlog.getIsStarred()) {
            menuLike.setLabelText(ReaderApp.getContext().getString(R.string.blog_star));
            menuLike.setImageResource(R.mipmap.ic_action_like);
        } else {
            menuLike.setLabelText(ReaderApp.getContext().getString(R.string.blog_unstar));
            menuLike.setImageResource(R.mipmap.ic_action_unlike);
        }

        CoordinatorLayout coordinatorLayout = (CoordinatorLayout) findViewById(R.id.rl);
        View bottomSheet = coordinatorLayout.findViewById(R.id.bottom_sheet);
        behavior = BottomSheetBehavior.from(bottomSheet);
        behavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                //这里是bottomSheet 状态的改变，根据slideOffset可以做一些动画
                Log.i("cjj", "newState--->" + newState);

                if (behavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
                    menu.showMenu(true);
                }
                if(behavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                    menu.hideMenu(true);
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                //这里是拖拽中的回调，根据slideOffset可以做一些动画
                Log.i("cjj", "slideOffset=====》" + slideOffset);
//                ViewCompat.setScaleY(bottomSheet,slideOffset);
            }
        });

        if (!mBlog.getIsRead()) {
            menuRead.setLabelText(ReaderApp.getContext().getString(R.string.blog_read));
            menuRead.setImageResource(R.mipmap.ic_action_read);
        } else {
            menuRead.setLabelText(ReaderApp.getContext().getString(R.string.blog_unread));
            menuRead.setImageResource(R.mipmap.ic_action_unread);
        }

        //rl = (RelativeLayout) findViewById(R.id.rl);

        //layout.bringToFront();

        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menu.close(true);

                Blog mBlog =  mBlogs.get(contents.getCurrentItem());
                RssAction action;

                switch (v.getId()) {
                    case R.id.menu_share:
                        Intent intent = new Intent(Intent.ACTION_SEND);
                        intent.setType("text/plain");
                        intent.putExtra(Intent.EXTRA_SUBJECT, mBlog.getTitle());
                        intent.putExtra(Intent.EXTRA_TEXT, HtmlUtil.trim(HtmlUtil.filterHtml(mBlog.getDescription())).replace(" ",""));
                        startActivity(Intent.createChooser(intent, ReaderApp.getContext().getString(R.string.action_share_to)));
                        break;
                    case R.id.menu_expand:
                        expand();
                        break;
                    case R.id.menu_reload:
                        int pos = contents.getCurrentItem();
                        if(adapter.getFragment(pos) != null)
                            adapter.getFragment(pos).render();
                        break;
                    case R.id.menu_like:
                        action = mBlog.getIsStarred() ? RssAction.AsUnstar : RssAction.AsStar;

                        new BlogMarkTask(mBlog, action).executeOnExecutor(FULL_TASK_EXECUTOR, PreferencesUtil.getAccessToken(), PreferencesUtil.getProfile().getId());

                        if (action.equals(RssAction.AsStar)) {
                            menuLike.setLabelText(ReaderApp.getContext().getString(R.string.blog_star));
                            menuLike.setImageResource(R.mipmap.ic_action_like);
                        } else {
                            menuLike.setLabelText(ReaderApp.getContext().getString(R.string.blog_unstar));
                            menuLike.setImageResource(R.mipmap.ic_action_unlike);
                        }

                        break;
                    case R.id.menu_read:
                        action = mBlog.getIsRead() ? RssAction.AsUnread : RssAction.AsRead;

                        new BlogMarkTask(mBlog, action).executeOnExecutor(FULL_TASK_EXECUTOR, PreferencesUtil.getAccessToken(), PreferencesUtil.getProfile().getId());

                        if (action.equals(RssAction.AsRead)) {
                            menuRead.setLabelText(ReaderApp.getContext().getString(R.string.blog_read));
                            menuRead.setImageResource(R.mipmap.ic_action_read);
                        } else {
                            menuRead.setLabelText(ReaderApp.getContext().getString(R.string.blog_unread));
                            menuRead.setImageResource(R.mipmap.ic_action_unread);
                        }

                        mBlog.save();

                        break;
                    case R.id.menu_settings:
                        if (behavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
                            behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                        } else {
                            behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                        }

                        menu.hideMenu(true);

                        //showBSDialog();

                        break;
                }
            }
        };

        menuShare.setOnClickListener(clickListener);
        menuSettings.setOnClickListener(clickListener);
        menuExpand.setOnClickListener(clickListener);
        menuRead.setOnClickListener(clickListener);
        menuReload.setOnClickListener(clickListener);
        menuLike.setOnClickListener(clickListener);
    }

    public void expand(){
        // The UI options currently enabled are represented by a bitfield.
        // getSystemUiVisibility() gives us that bitfield. _STICKY
        int uiOptions = getWindow().getDecorView().getSystemUiVisibility();
        int newUiOptions = uiOptions;
        boolean isImmersiveModeEnabled = ((uiOptions | View.SYSTEM_UI_FLAG_IMMERSIVE) == uiOptions);
        if (isImmersiveModeEnabled) {
            Log.i("RssReader", "Turning immersive mode mode off. ");
        } else {
            Log.i("RssReader", "Turning immersive mode mode on.");
        }

        // Immersive mode: Backward compatible to KitKat (API 19).
        // Note that this flag doesn't do anything by itself, it only augments the behavior
        // of HIDE_NAVIGATION and FLAG_FULLSCREEN.  For the purposes of this sample
        // all three flags are being toggled together.
        // This sample uses the "sticky" form of immersive mode, which will let the user swipe
        // the bars back in again, but will automatically make them disappear a few seconds later.
        newUiOptions ^= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        newUiOptions ^= View.SYSTEM_UI_FLAG_FULLSCREEN;
        newUiOptions ^= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        getWindow().getDecorView().setSystemUiVisibility(newUiOptions);
    }

    private void showBSDialog() {
        final BottomSheetDialog dialog = new BottomSheetDialog(this);
        dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().getDecorView().setSystemUiVisibility(ContentActivity.this.getWindow().getDecorView().getSystemUiVisibility());
        //Clear the not focusable flag from the window
        dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
        View contentView = LayoutInflater.from(this).inflate(R.layout.reading_settings, null);
        dialog.setContentView(contentView);

        View parent = (View) contentView.getParent();
        BottomSheetBehavior behavior = BottomSheetBehavior.from(parent);
        contentView.measure(0, 0);
        behavior.setPeekHeight(contentView.getMeasuredHeight());
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) parent.getLayoutParams();
        params.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
        parent.setLayoutParams(params);

        dialog.show();
    }

    /**
     * 初始化参数
     */
    private void initView() {
        contents = (ViewPager) this.findViewById(R.id.contents);
        adapter = new ContentAdapter(getSupportFragmentManager());
        contents.setAdapter(adapter);
        contents.setCurrentItem(cPos, false);
        contents.setEnabled(false);
        initListeners();
        setStatusBarColor(Color.parseColor(PreferencesUtil.getAppSettings().getStyle().getTitleBackgroundColor()));
    }

    private void setStatus(Blog mBlog, RssAction mAction){
        if(mAction == RssAction.AsRead){
            mBlog.setIsRead(true);
        }

        if(mAction == RssAction.AsStar){
            mBlog.setIsStarred(true);
            mBlog.setActionTime(System.currentTimeMillis());
        }

        if(mAction == RssAction.AsUnread){
            mBlog.setIsRead(false);
        }

        if(mAction == RssAction.AsUnstar){
            mBlog.setIsStarred(false);
        }

        mReadBlogs.add(mBlog);

        new BlogMarkTask(mBlog, RssAction.AsRead).executeOnExecutor(FULL_TASK_EXECUTOR, PreferencesUtil.getAccessToken(), PreferencesUtil.getProfile().getId());
    }

    private void gotoGallery(Blog mBlog){
        String str = mBlog.getDescription();

//        if (mBlog.getContent() != null && mBlog.getContent().length() > 0) {
//            str = mBlog.getContent();
//        }

        List<String> images = HtmlUtil.getImageList(str);
        if (images.size() > 10) {
            Intent intent = new Intent(this, GalleryActivity.class);
            intent.putExtra("images", CollectionUtil.List2Array(images));
            //intent.putExtra("videos", );
            intent.putExtra("image_index", 0);
            startActivity(intent);
        }
    }

    int cPos = 0;

    /**
     * 初始化事件
     */
    private void initListeners() {

        // ViewPager滑动事件
        contents.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int pos) {
                Blog mBlog = mBlogs.get(pos);
                if(!mBlog.getIsRead()) {
                    setStatus(mBlog, RssAction.AsRead);
                }

                gotoGallery(mBlog);

                if (pos == mBlogs.size() - 1){
                    List<Blog> right = null;

                    if(mFromType == FromType.Normal)
                        right = Blog.find(Blog.class, "TIME_STAMP <? and (CHANNEL_ID=? or TAG_ID=?)", new String[]{String.valueOf(mBlog.getTimeStamp()), mChannel.getChannelId(), mChannel.getChannelId()}, "", "TIME_STAMP DESC", "30");
                    if(mFromType == FromType.All)
                        right = Blog.find(Blog.class, "TIME_STAMP <?", new String[]{String.valueOf(mBlog.getTimeStamp())}, "", "TIME_STAMP DESC", "30");
                    if(mFromType == FromType.Starred)
                        right = Blog.find(Blog.class, "TIME_STAMP <? and Is_Starred = 1", new String[]{String.valueOf(mBlog.getTimeStamp())}, "", "TIME_STAMP DESC", "30");
                    if(mFromType == FromType.Unread)
                        right = Blog.find(Blog.class, "TIME_STAMP <? and Is_Read = 0", new String[]{String.valueOf(mBlog.getTimeStamp())}, "", "TIME_STAMP DESC", "30");
                    if(mFromType == FromType.Search) {
                        String query = getIntent().getExtras().getString("query");
                        right = Blog.find(Blog.class, "TIME_STAMP <? and (title like '?' and description like '?' and content like '?')", new String[]{String.valueOf(mBlog.getTimeStamp()), "%"+query+"%", "%"+query+"%", "%"+query+"%"}, "", "TIME_STAMP ASC", "30");
                    }

                    mBlogs.addAll(right);

                    adapter.notifyDataSetChanged();
                }

                if(pos > 0){
                    if(adapter.getFragment(pos - 1) != null)
                        adapter.getFragment(pos - 1).stopVideo();
                }
                if(adapter.getFragment(pos) != null)
                    adapter.getFragment(pos).stopVideo();
                if(adapter.getFragment(pos + 1) != null)
                    adapter.getFragment(pos + 1).stopVideo();
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageScrollStateChanged(int pos) {
            }
        });
    }

    /**
    * ViewPager的适配器
    */
    class ContentAdapter extends FragmentStatePagerAdapter {

        WeakHashMap<Integer, Fragment> mPageReferenceMap = new WeakHashMap<Integer, Fragment>();

        public ContentAdapter(FragmentManager fm){//}, List<ContentFragment> fragments) {
            super(fm);
        }

        @Override
        public int getCount() {
            return mBlogs.size();
        }

        @Override
        public Fragment getItem(int position) {
            ContentFragment fragment = new ContentFragment();
            Bundle mBundle = new Bundle();
            mBundle.putString("blog",mBlogs.get(position).getBlogId());
            mBundle.putSerializable("channel", mChannel);
            mBundle.putString("from", FromType.Normal.toString());
            fragment.setArguments(mBundle);
            //fragment.render(mBlogs.get(position));

            mPageReferenceMap.put(position, fragment);

            return fragment;
        }

        public void destroyItem (ViewGroup container, int position, Object object) {
            super.destroyItem(container, position, object);
            mPageReferenceMap.remove(position);
        }

        public ContentFragment getFragment(int key) {
            return (ContentFragment)mPageReferenceMap.get(key);
        }
    }
}