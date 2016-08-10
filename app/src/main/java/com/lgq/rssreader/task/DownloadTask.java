package com.lgq.rssreader.task;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.lgq.rssreader.R;
import com.lgq.rssreader.abstraction.FeedlyParser;
import com.lgq.rssreader.abstraction.RssParser;
import com.lgq.rssreader.adapter.ChannelRecyclerViewAdapter;
import com.lgq.rssreader.core.Constant;
import com.lgq.rssreader.core.ReaderApp;
import com.lgq.rssreader.formatter.ContentFormatter;
import com.lgq.rssreader.formatter.DescriptionFormatter;
import com.lgq.rssreader.formatter.DownloadContentFormatter;
import com.lgq.rssreader.formatter.DownloadDescriptionFormatter;
import com.lgq.rssreader.model.Blog;
import com.lgq.rssreader.model.Channel;
import com.lgq.rssreader.model.RssAction;
import com.lgq.rssreader.util.NotificationUtil;
import com.lgq.rssreader.util.PreferencesUtil;
import com.orm.StringUtil;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * Created by redel on 2015-10-22.
 */
public class DownloadTask extends AsyncTask<String, Integer, List<Blog>> {
    private Channel mChannel;
    private Context mContext;
    public NotificationUtil mNotificationUtil;

    public DownloadTask(Context mContext, Channel mChannel){
        this.mChannel = mChannel;
        this.mContext = mContext;
        mNotificationUtil = new NotificationUtil(mContext);
    }

    protected List<Blog> doInBackground(String... urls) {

        String token = urls[0];

        String userId = urls[1];

        ArrayList<String> existBlogIDs = new ArrayList<>();

        List<Blog> blogs = new ArrayList<>();

        RssParser parser = new FeedlyParser(token);
        try {
            Blog tmp = new Blog();
            tmp.setTimeStamp(0);
            tmp.setPubDate(new Date());

            blogs = parser.getRssBlog(mChannel, tmp, 30);

            if(blogs != null) {
                String whereClause = "";
                String[] whereArgs = new String[blogs.size()];
                for (int i = blogs.size() - 1; i >= 0; i--) {
                    Blog b = blogs.get(i);
                    whereClause = whereClause + StringUtil.toSQLName("blogID") + "=?";
                    if (i != 0) {
                        whereClause = whereClause + " OR ";
                    }
                    whereArgs[i] = b.getBlogId();
                }

                List<Blog> exists = Blog.find(Blog.class, whereClause, whereArgs);

                for (Blog b : exists) {
                    existBlogIDs.add(b.getBlogId());
                }

                if (existBlogIDs.size() > 0) {
                    for (Blog b : blogs) {
                        if (!existBlogIDs.contains(b.getBlogId())) {
                            b.save();
                        }
                    }
                } else {
                    Blog.saveInTx(blogs);
                }
            }

            int count = 0;

            DownloadContentFormatter content = new DownloadContentFormatter();
            DownloadDescriptionFormatter desc = new DownloadDescriptionFormatter();

            for (Blog b : blogs) {
                try{
                    String val = content.render(b);
                    if(val.length() >0 && !val.equals(Constant.EMPTY_HTML)) {
                        b.setContent(val);
                    }

                    count++;
                    publishProgress((int) ((count / (float)60) * 100));

                    val = desc.render(b);
                    count++;
                    if(val.length() >0 && !val.equals(Constant.EMPTY_HTML)) {
                        b.setDescription(val);
                    }
                    publishProgress((int) ((count / (float) 60) * 100));

                    if(!existBlogIDs.contains(b.getBlogId()))
                        b.save();
                }catch (Exception e){
                    Log.e("RssReader", e.getMessage());
                    if(count % 2 == 0)
                        count = count + 2;
                    else
                        count = count + 1;

                    publishProgress((int) ((count / (float) 60) * 100));
                }
            }
        }catch (Exception e){
            Log.e("RssReader", e.getMessage());

            mNotificationUtil.completed();
        }

        return blogs;
    }

    protected void onPreExecute(){
        //Create the notification in the statusbar
        mNotificationUtil.createNotification();
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        Log.i("RssReader", "onProgressUpdate(Progress... progresses) called");
        mNotificationUtil.progressUpdate(values[0]);
    }

    protected void onPostExecute(List<Blog> blogs) {
        Toast.makeText(mContext, R.string.download_complete, Toast.LENGTH_SHORT);

        mNotificationUtil.completed();
    }
}