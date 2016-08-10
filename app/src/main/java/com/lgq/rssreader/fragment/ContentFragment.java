package com.lgq.rssreader.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.lgq.rssreader.ContentActivity;
import com.lgq.rssreader.GalleryActivity;
import com.lgq.rssreader.R;
import com.lgq.rssreader.core.ManualResetEvent;
import com.lgq.rssreader.core.ReaderApp;
import com.lgq.rssreader.enums.FromType;
import com.lgq.rssreader.formatter.BlogFormatter;
import com.lgq.rssreader.formatter.CacheEventArgs;
import com.lgq.rssreader.formatter.ContentFormatter;
import com.lgq.rssreader.formatter.DescriptionFormatter;
import com.lgq.rssreader.model.Blog;
import com.lgq.rssreader.model.Channel;
import com.lgq.rssreader.task.ContentTask;
import com.lgq.rssreader.util.CollectionUtil;
import com.lgq.rssreader.util.HtmlUtil;
import com.lgq.rssreader.util.PreferencesUtil;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by redel on 2015-09-03.
 */
public class ContentFragment extends BaseFragment {
    private TextView blogTitle;
    private WebView mWebview;
    private Blog mBlog;
    private Channel mChannel;
    private FromType mFromType;
    private ManualResetEvent loadEvent;
    private BlogFormatter contentFormatter = new ContentFormatter();
    private BlogFormatter descFormatter = new DescriptionFormatter();
    private GestureDetector gs = null;
    ProgressDialog mProgressDialog;

    private static ExecutorService FULL_TASK_EXECUTOR;
    private static final int CONTENT = 1;
    private static final int DESC = 2;
    private static final int FLASH = 3;
    private static final int SHARE = 4;
    private static final int SHAKE = 5;

    static {
        FULL_TASK_EXECUTOR = Executors.newCachedThreadPool();
    }

    public TextView getTitleView(){
        return blogTitle;
    }

    public WebView getWebView(){
        return mWebview;
    }

    public ProgressDialog getProgressDialog(){return mProgressDialog;}

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

                    if(Content.length() != 0){
                        mWebview.loadUrl("javascript: LoadContent('" + HtmlUtil.trim(Content) + "','','" + (msg.what == CONTENT ? "content" : "description") + "')");
                    }
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
                            mWebview.loadUrl("javascript: replaceFlash('" + String.valueOf(cacheArgs.CompleteIndex) + "','" + cacheArgs.Cache.html().replace("'", "\"").replace("&amp;", "&") + "','" + (ReaderApp.getContext().getResources().getString(R.string.blog_clicktoview) + " " + title.replace("'", "\"")) + "','True')");
                        else
                            mWebview.loadUrl("javascript: replaceFlash('" + String.valueOf(cacheArgs.CompleteIndex) + "','" + cacheArgs.Cache.html().replace("'", "\"").replace("&amp;", "&") + "','" + title.replace("'", "\"") + "','True')");
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

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_content, container, false);
        initData();
        initView(view);
        render();
        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        mWebview.loadUrl("javascript:stopVideo()");
    }

    private void initData(){
        Bundle b = getArguments();
        mBlog = Blog.find(Blog.class, "blog_id=?", new String[]{b.getString("blog")}).get(0);
        mChannel = (Channel)b.getSerializable("channel");
        mFromType = FromType.valueOf(b.getString("from"));

        Log.d("RssReader", "rending " + mBlog.getTitle());
    }

    public void render(){
        blogTitle.setText(mBlog.getTitle());
        blogTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mWebview.loadUrl("javascript: moveToTop()");
            }
        });

        Log.i("RssReader", "before " + System.currentTimeMillis());

        new ContentTask(descFormatter, mWebview, mProgressDialog).executeOnExecutor(FULL_TASK_EXECUTOR, mBlog);
    }

    /**
     * 初始化布局
     */
    private void initView(View v) {
        //实例化控件
        mWebview = (WebView)v.findViewById(R.id.content);
        mWebview.getSettings().setJavaScriptEnabled(true);
        mWebview.addJavascriptInterface(this, "external");
        mWebview.getSettings().setPluginState(WebSettings.PluginState.ON);
        mWebview.setWebChromeClient(new WebChromeClient() );
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (0 != (getActivity().getApplicationInfo().flags &= ApplicationInfo.FLAG_DEBUGGABLE)) {
                WebView.setWebContentsDebuggingEnabled(true);
            }
        }
        mWebview.setWebViewClient(new WebViewClient() {
            // Load opened URL in the application instead of standard browser
            // application
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Log.i("RssReader", url);
                view.loadUrl(url);
                return true;
            }

            public void onPageFinished(WebView view, String url) {
                Log.i("RssReader", url + " load complete");
            }
        });
        blogTitle = (TextView)v.findViewById(R.id.content_title);
        blogTitle.setBackgroundColor(Color.parseColor(PreferencesUtil.getAppSettings().getStyle().getTitleBackgroundColor()));
        blogTitle.setTextColor(Color.parseColor(PreferencesUtil.getAppSettings().getStyle().getTitleFontColor()));
        contentFormatter.setFlashCompleteHandler(flashCompleteHandler);
        descFormatter.setFlashCompleteHandler(flashCompleteHandler);
        loadEvent = new ManualResetEvent(false);
        initProgressDialog(false);
    }

    private void initProgressDialog(boolean isShowDialog){
        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
        mProgressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.setIcon(R.drawable.progress);
        mProgressDialog.setMessage(getResources().getString(R.string.content_loading) + "...");
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setIndeterminateDrawable(getResources().getDrawable(R.drawable.spinner));
        mProgressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.parseColor(PreferencesUtil.getAppSettings().getStyle().getTitleFontColor())));
        if(isShowDialog)
            mProgressDialog.show();

        //mProgressDialog.getWindow().getDecorView().setSystemUiVisibility(getActivity().getWindow().getDecorView().getSystemUiVisibility());
        //Clear the not focusable flag from the window
        //mProgressDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //mWebview.loadDataWithBaseURL("/", "", "text/html", "utf-8", null);
        mWebview.loadUrl("javascript:stopVideo()");
    }

    @JavascriptInterface
    public void loadComplete(String args) {
        Log.i("RssReader", "Page Load " + args);
        mProgressDialog.dismiss();
        ContentFragment.this.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mWebview.setVisibility(View.VISIBLE);
            }
        });
        loadEvent.set();
    }

    public void stopVideo() {
        if(mWebview != null)
        mWebview.loadUrl("javascript:stopVideo()");
    }

    private void gotoGallery(String url, List<String> videos, String content){

        Intent intent = new Intent(this.getContext(), GalleryActivity.class);
        List<String> images = HtmlUtil.getImageList(content);

        intent.putExtra("images", CollectionUtil.List2Array(images));
        intent.putExtra("videos", CollectionUtil.List2Array(videos));
        intent.putExtra("image_index", images.indexOf(url));
        startActivity(intent);
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
                Uri name = null;
                if(url.startsWith("http://"))
                    name = Uri.parse(url.replace("&amp;","&"));
                else
                    name = Uri.parse("http://" + url.replace("&amp;","&"));
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
            url = args.replace("SaveToMediaLibrary", "").split("____")[0];

            List<String> videos = CollectionUtil.Array2List(args.replace("SaveToMediaLibrary", "").split("____"));
            videos.remove(0);
            String str = mBlog.getDescription();
            if(mBlog.getContent() != null && mBlog.getContent().length() > 0){
                str = mBlog.getContent();
            }

            gotoGallery(url, videos, str);
        }

        if(args.equals("reload")){
            new ContentTask(descFormatter, mWebview, mProgressDialog).executeOnExecutor(FULL_TASK_EXECUTOR,mBlog);
        }

        if(args.equals("ondblclick")){
            loadEvent = new ManualResetEvent(false);
            new ContentTask(contentFormatter, mWebview, mProgressDialog).executeOnExecutor(FULL_TASK_EXECUTOR,mBlog);
        }

        if(args.startsWith("scroll")){
            String direction = args.replace("scroll ", "");
            final ContentActivity content = (ContentActivity)getActivity();
//            switch (direction){
//                case "up":
//                    content.runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            if(content.getMenu().isMenuHidden()){
//                                content.getMenu().showMenu(true);
//                            }
//                        }
//                    });
//                break;
//                case "down":
//                    content.runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            if(!content.getMenu().isMenuHidden()){
//                                content.getMenu().hideMenu(true);
//                            }
//                        }
//                    });
//                    break;
//            }
        }
    }
}