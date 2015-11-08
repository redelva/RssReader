package com.lgq.rssreader;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.lgq.rssreader.controls.GestureListener;
import com.lgq.rssreader.controls.GestureOnTouchListener;
import com.lgq.rssreader.core.ManualResetEvent;
import com.lgq.rssreader.core.ReaderApp;
import com.lgq.rssreader.formatter.BlogFormatter;
import com.lgq.rssreader.formatter.CacheEventArgs;
import com.lgq.rssreader.formatter.ContentFormatter;
import com.lgq.rssreader.formatter.DescriptionFormatter;
import com.lgq.rssreader.model.Blog;
import com.lgq.rssreader.model.Channel;
import com.lgq.rssreader.model.RssAction;
import com.lgq.rssreader.task.ContentTask;
import com.lgq.rssreader.task.MarkTask;
import com.lgq.rssreader.task.SaveMediaTask;
import com.lgq.rssreader.util.HtmlUtil;
import com.lgq.rssreader.util.PreferencesUtil;
import com.linroid.filtermenu.library.FilterMenu;
import com.linroid.filtermenu.library.FilterMenuLayout;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ContentActivity extends AppCompatActivity  implements GestureListener.IGestureListener{
    private static ExecutorService FULL_TASK_EXECUTOR;

    static {
        FULL_TASK_EXECUTOR = Executors.newCachedThreadPool();
    }

    private TextView blogTitle;
    private WebView mWebview;
    private Blog mBlog;
    private Channel mChannel;
    private ManualResetEvent loadEvent;
    private BlogFormatter contentFormatter = new ContentFormatter();
    private BlogFormatter descFormatter = new DescriptionFormatter();

    private BlogFormatter.FlashCompleteHandler flashCompleteHandler = new BlogFormatter.FlashCompleteHandler() {
        @Override
        public void onFlash(final Object sender, final CacheEventArgs cacheArgs) {
            Log.d("RssReader", cacheArgs.Cache.outerHtml());

            new Thread(
                new Runnable(){
                    @Override
                    public void run() {
                        try {
                            loadEvent.waitOne();
                        }catch (Exception e){
                            e.printStackTrace();
                        }

                        Message m = myHandler.obtainMessage();
                        m.what = FLASH;
                        m.obj = cacheArgs;
                        Bundle b = new Bundle();
                        b.putString("title", sender.toString());
                        m.setData(b);
                        myHandler.sendMessage(m);
                    }
                }
            ).start();
        }
    };
    private GestureListener mGestureListener;
    private GestureDetector mGestureDetector;
    private ScaleGestureDetector mScaleGestureDetector;
    private ProgressDialog mProgressDialog;
    private ImageView processImage;
    private TextView processMsg;
    private View processContainer;
    private FloatingActionButton menuRead;
    private FloatingActionButton menuLike;

    private ArrayList<GestureOnTouchListener> onTouchListeners = new ArrayList<GestureOnTouchListener>(10);
    private static final int CONTENT = 1;
    private static final int DESC = 2;
    private static final int FLASH = 3;
    private static final int SHARE = 4;
    private static final int SHAKE = 5;
    private Handler myHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            String Content = "";
            if(msg.obj instanceof String)
                Content = (String)msg.obj;

            CacheEventArgs cacheArgs = null;
            String title = "";
            if(msg.obj instanceof CacheEventArgs){
                cacheArgs = (CacheEventArgs)msg.obj;
                title = msg.getData().getString("title");
            }

            if(mWebview != null){
                switch(msg.what){
                    case CONTENT:
                    case DESC:

                        Log.i("RssReader", "render content in js");

                        //check current blog is still the parsed blog
                        if(cacheArgs != null && cacheArgs.Blog.getBlogId() != mBlog.getBlogId())
                            return;

                        if(Content.length() != 0)
                            mWebview.loadUrl("javascript: LoadContent('" + HtmlUtil.trim(Content) + "','','" + (msg.what == CONTENT ? "content" : "description") + "')");
                        else
                            mWebview.loadUrl("javascript: LoadError('" + ReaderApp.getContext().getResources().getString(R.string.content_errortitle) + "','" +
                                    ReaderApp.getContext().getResources().getString(R.string.content_errorcontent) + "','" +
                                    ReaderApp.getContext().getResources().getString(R.string.content_errorload) + "','" +
                                    (msg.what == CONTENT ? "content" : "description") + "')");

                        break;
                    case FLASH:

                        Log.i("RssReader","Flash callbakc happens");

                        if(cacheArgs != null && cacheArgs.Blog.getBlogId() == mBlog.getBlogId()){
                            if (cacheArgs.Total == -1)
                                mWebview.loadUrl("javascript: replaceFlash('" + String.valueOf(cacheArgs.CompleteIndex) + "','" + cacheArgs.Cache.html().replace("'", "\"") + "','" + (ReaderApp.getContext().getResources().getString(R.string.blog_clicktoview) + " " + title.replace("'", "\"")) + "','True')");
                            else
                                mWebview.loadUrl("javascript: replaceFlash('" + String.valueOf(cacheArgs.CompleteIndex) + "','" + cacheArgs.Cache.html().replace("'", "\"") + "','" + title.replace("'", "\"") + "','True')");
                        }
                        break;
                    case SHARE:


                        break;
                    case SHAKE:


                        break;
                }
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content);
        initData();
        initView();
    }

    private void initData(){
        Bundle b = getIntent().getExtras();
        mBlog = Blog.find(Blog.class, "blog_id=?", new String[]{b.getString("blog")}).get(0);
        mChannel = (Channel)b.getSerializable("channel");
        contentFormatter.setFlashCompleteHandler(flashCompleteHandler);
        descFormatter.setFlashCompleteHandler(flashCompleteHandler);
        loadEvent = new ManualResetEvent(false);
    }

    /**
     * 初始化布局
     */
    private void initView() {
        //实例化控件
        mWebview = (WebView)findViewById(R.id.content);
        mWebview.getSettings().setJavaScriptEnabled(true);
        mWebview.addJavascriptInterface(this, "external");
        mWebview.getSettings().setPluginState(WebSettings.PluginState.ON);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (0 != (getApplicationInfo().flags &= ApplicationInfo.FLAG_DEBUGGABLE)) {
                WebView.setWebContentsDebuggingEnabled(true);
            }
        }
        mWebview.setWebViewClient(new WebViewClient() {
            // Load opened URL in the application instead of standard browser
            // application
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Log.i("RssReader", url);
                return true;
            }

            public void onPageFinished(WebView view, String url) {
                Log.i("RssReader", url + " load complete");
            }
        });

        blogTitle = (TextView) findViewById(R.id.content_title);
        blogTitle.setText(mBlog.getTitle());
        blogTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mWebview.loadUrl("javascript: moveToTop()");
            }
        });

        processImage = (ImageView)findViewById(R.id.process);
        processMsg = (TextView)findViewById(R.id.blog_content_msg);
        processContainer = findViewById(R.id.processContainer);

        initProgressDialog(false);

        FloatingActionMenu layout = (FloatingActionMenu) findViewById(R.id.menu);
        menuLike = (FloatingActionButton) findViewById(R.id.menu_like);
        menuRead = (FloatingActionButton) findViewById(R.id.menu_read);

        layout.bringToFront();

        Log.i("RssReader", "before " + System.currentTimeMillis());
        new ContentTask(descFormatter, mWebview, mProgressDialog).executeOnExecutor(FULL_TASK_EXECUTOR, mBlog);
        new MarkTask(menuLike, menuRead, mBlog, RssAction.AsRead).executeOnExecutor(FULL_TASK_EXECUTOR, PreferencesUtil.getAccessToken(), PreferencesUtil.getProfile().getId());
        //mWebview.loadDataWithBaseURL("/", HtmlUtil.wrapHtml(descFormatter.render(mBlog)), "text/html", "utf-8", null);

        mGestureListener = new GestureListener(this,
                findViewById(R.id.content),
                (int) (getWindowManager().getDefaultDisplay().getWidth() * 0.33),
                (int) (getWindowManager().getDefaultDisplay().getHeight() * 0.33));
        mGestureDetector = new GestureDetector(this, mGestureListener);
        mScaleGestureDetector= new ScaleGestureDetector(this, mGestureListener);

        this.registerGestureOnTouchListener(new GestureOnTouchListener() {
            @Override
            public void onTouch(MotionEvent ev) {

                mGestureDetector.onTouchEvent(ev);
                mScaleGestureDetector.onTouchEvent(ev);

                //if(isScrolling && ev.getAction() == MotionEvent.ACTION_UP){
                if (ev.getAction() == MotionEvent.ACTION_UP) {
                    mGestureListener.onScrollComplete(ev);
                }
            }
        });
    }

    private void initProgressDialog(boolean isShowDialog){
        mProgressDialog = new ProgressDialog(this);
        //mProgressDialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
        //mProgressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.setIcon(R.drawable.progress);
        mProgressDialog.setMessage(getResources().getString(R.string.content_loading) + "...");
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setIndeterminateDrawable(getResources().getDrawable(R.drawable.spinner));
        if(isShowDialog)
            mProgressDialog.show();

        //mProgressDialog.getWindow().getDecorView().setSystemUiVisibility(getWindow().getDecorView().getSystemUiVisibility());
        //Clear the not focusable flag from the window
        //mProgressDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
    }

    protected void showProcess(final String msg){
        processMsg.setText(msg);
        processMsg.setVisibility(View.GONE);
        processImage.setVisibility(View.VISIBLE);
        processContainer.setVisibility(View.VISIBLE);
        processImage.setAnimation(AnimationUtils.loadAnimation(this, R.anim.rotate));
    }

    protected void hideProcess(){
        processImage.clearAnimation();
        processMsg.setText("");
        processImage.setVisibility(View.GONE);
        processMsg.setVisibility(View.GONE);
        processContainer.setVisibility(View.GONE);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        for (GestureOnTouchListener listener : onTouchListeners) {
            listener.onTouch(ev);
        }
        //displayEvent(ev);
        return super.dispatchTouchEvent(ev);
    }

    public void registerGestureOnTouchListener(GestureOnTouchListener listener){
        onTouchListeners.add(listener);
    }

    @Override
    public void onDoubleTap() {
        //loadEvent = null;
        loadEvent = new ManualResetEvent(false);
        new ContentTask(contentFormatter, mWebview, mProgressDialog).execute(mBlog);
    }

    @Override
    public void onLeft() {
        //showProcess("Previous item");

        List<Blog> blogs = Blog.find(Blog.class, "TIME_STAMP >? and CHANNEL_ID=?", new String[]{String.valueOf(mBlog.getTimeStamp()), mChannel.getChannelId()},"","TIME_STAMP ASC", "1");

        if(blogs != null && blogs.size() > 0){
            mBlog = blogs.get(0);
            blogTitle.setText(mBlog.getTitle());
            new ContentTask(descFormatter, mWebview, mProgressDialog).executeOnExecutor(FULL_TASK_EXECUTOR, mBlog);
            new MarkTask(menuLike, menuRead, mBlog, RssAction.AsRead).executeOnExecutor(FULL_TASK_EXECUTOR, PreferencesUtil.getAccessToken(), PreferencesUtil.getProfile().getId());
        }else{
            Toast.makeText(this, "No previous item found", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRight() {
        //showProcess("Next item");

        List<Blog> blogs = Blog.find(Blog.class, "TIME_STAMP <? and CHANNEL_ID=?", new String[]{String.valueOf(mBlog.getTimeStamp()), mChannel.getChannelId()},"","TIME_STAMP DESC", "1");

        if(blogs != null && blogs.size() > 0){
            mBlog = blogs.get(0);
            blogTitle.setText(mBlog.getTitle());
            new ContentTask(descFormatter, mWebview, mProgressDialog).executeOnExecutor(FULL_TASK_EXECUTOR, mBlog);
            new MarkTask(menuLike, menuRead, mBlog, RssAction.AsRead).executeOnExecutor(FULL_TASK_EXECUTOR, PreferencesUtil.getAccessToken(),PreferencesUtil.getProfile().getId());
        }else{
            Toast.makeText(this, "No next item found", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDown() {

    }

    @Override
    public void onUp() {

    }

    @Override
    public void onScale(double scale) {
        mWebview.loadUrl("javascript: fontsize('" + scale * 14 + "')");
    }

    @JavascriptInterface
    public void loadComplete(String args) {
        Log.i("RssReader", "Page Load " + args);
        loadEvent.set();
        //mProgressDialog.dismiss();
        //hideProcess();
    }

    @JavascriptInterface
    public void notifyJava(String args) {
        Log.i("RssReader", args);

        String url = "";
        if(args.startsWith("LinkHandle")){
            url = args.replace("LinkHandle", "");

            if(url.contains("mp4") || url.contains("f4v") || url.contains("flv")){
                Intent intent = new Intent(Intent.ACTION_VIEW);
                String type = "video/mp4";
                Uri name = Uri.parse(HtmlUtil.unescape(url));
                intent.setDataAndType(name, type);

                startActivity(intent);
            }
            else{
                Uri name = Uri.parse(HtmlUtil.unescape(url));
                Intent intent = new Intent(Intent.ACTION_VIEW, name);

                startActivity(intent);
            }
        }

        if(args.startsWith("SaveToMediaLibrary")){
            url = args.replace("SaveToMediaLibrary", "");

            new SaveMediaTask(this).execute(url);
        }

        if(args.equals("reload")){
            new ContentTask(descFormatter, mWebview, mProgressDialog).execute(mBlog);
        }
    }
}