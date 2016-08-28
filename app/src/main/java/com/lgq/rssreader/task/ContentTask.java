package com.lgq.rssreader.task;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.webkit.WebView;

import com.lgq.rssreader.ContentActivity;
import com.lgq.rssreader.GalleryActivity;
import com.lgq.rssreader.formatter.BlogFormatter;
import com.lgq.rssreader.formatter.ContentFormatter;
import com.lgq.rssreader.formatter.DescriptionFormatter;
import com.lgq.rssreader.model.Blog;
import com.lgq.rssreader.util.CollectionUtil;
import com.lgq.rssreader.util.HtmlUtil;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by redel on 2015-10-17.
 */
public class ContentTask extends AsyncTask<Blog, Void, String> {

    private WeakReference<BlogFormatter> formatter;
    private WeakReference<WebView> mWebview;
    private WeakReference<ProgressDialog> mProgressDialog;
    Blog blog;

    public ContentTask(BlogFormatter formatter, WebView view, ProgressDialog dialog){
        this.formatter = new WeakReference<>(formatter);
        this.mWebview = new WeakReference<>(view);
        this.mProgressDialog = new WeakReference<>(dialog);
    }

    @Override
    protected void onPreExecute() {
        if(mWebview.get() != null && mProgressDialog.get() != null && mWebview.get().getContext() != null && ContentFormatter.class.isInstance(formatter.get())){
            ContentActivity contentActivity = (ContentActivity)mWebview.get().getContext();
            contentActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(mProgressDialog.get() != null)
                        mProgressDialog.get().show();
                }
            });
        }

//        if (ContextCompat.checkSelfPermission(mWebview.get().getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
//            //申请WRITE_EXTERNAL_STORAGE权限
//            ActivityCompat.requestPermissions((ContentActivity)mWebview.get().getContext(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 200);
//        }
    }

    @Override
    protected String doInBackground(Blog... params) {
        blog = params[0];

        if(blog != null){
            if(formatter.get() != null) {

                String content = formatter.get().render(blog);

                if(ContentFormatter.class.isInstance(formatter.get())) {
                    blog.setContent(HtmlUtil.unescape(content));
                    blog.save();
                }

                if(DescriptionFormatter.class.isInstance(formatter.get())) {
                    blog.setDescription(HtmlUtil.unescape(content));
                    blog.save();
                }

                return content;
            }
            return null;
        }else{
            return null;
        }
    }

    private void gotoGallery(String url, List<String> videos, String content){

        Intent intent = new Intent(mWebview.get().getContext(), GalleryActivity.class);
        List<String> images = HtmlUtil.getImageList(content);

        intent.putExtra("images", CollectionUtil.List2Array(images));
        intent.putExtra("videos", CollectionUtil.List2Array(videos));
        intent.putExtra("image_index", images.indexOf(url));
        mWebview.get().getContext().startActivity(intent);
    }

    @Override
    protected void onPostExecute(String readable) {
        if(mWebview.get() != null) {
            Log.i("RssReader", "content task start web view load");

            mWebview.get().loadDataWithBaseURL("/", HtmlUtil.wrapHtml(readable), "text/html", "utf-8", null);

            ContentActivity content = (ContentActivity)mWebview.get().getContext();

            if(content.getCurrentBlog().getBlogId().equals(blog.getBlogId()) && ContentFormatter.class.isInstance(formatter.get())){
                List<String> images = HtmlUtil.getImageList(readable);
                if (images.size() > 10) {
                    Intent intent = new Intent(content, GalleryActivity.class);
                    intent.putExtra("images", CollectionUtil.List2Array(images));
                    //intent.putExtra("videos", );
                    intent.putExtra("image_index", 0);
                    mWebview.get().getContext().startActivity(intent);
                }
            }
        }

        if(mProgressDialog.get() != null && ContentFormatter.class.isInstance(formatter.get())) {

                mProgressDialog.get().hide();

        }
    }
}
