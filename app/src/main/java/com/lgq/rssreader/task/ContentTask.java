package com.lgq.rssreader.task;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;
import android.webkit.WebView;

import com.lgq.rssreader.formatter.BlogFormatter;
import com.lgq.rssreader.formatter.ContentFormatter;
import com.lgq.rssreader.formatter.DescriptionFormatter;
import com.lgq.rssreader.model.Blog;
import com.lgq.rssreader.util.HtmlUtil;

import java.lang.ref.WeakReference;

/**
 * Created by redel on 2015-10-17.
 */
public class ContentTask extends AsyncTask<Blog, Void, String> {

    private WeakReference<BlogFormatter> formatter;
    private WeakReference<WebView> mWebview;
    private WeakReference<ProgressDialog> mProgressDialog;

    public ContentTask(BlogFormatter formatter, WebView view, ProgressDialog dialog){
        this.formatter = new WeakReference<>(formatter);
        this.mWebview = new WeakReference<>(view);
        this.mProgressDialog = new WeakReference<>(dialog);
    }

    @Override
    protected void onPreExecute() {
        if(ContentFormatter.class.isInstance(formatter.get()))
            mProgressDialog.get().show();
    }

    @Override
    protected String doInBackground(Blog... params) {
        Blog blog = params[0];

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

    @Override
    protected void onPostExecute(String readable) {
        if(mWebview.get() != null) {
            Log.i("RssReader", "content task start web view load");

            mWebview.get().loadDataWithBaseURL("/", HtmlUtil.wrapHtml(readable), "text/html", "utf-8", null);
        }

        if(mProgressDialog.get() != null) {
            mProgressDialog.get().dismiss();
        }
    }
}
