package com.lgq.rssreader.abstraction;

import android.os.Looper;

import com.lgq.rssreader.model.Blog;
import com.lgq.rssreader.model.Channel;
import com.lgq.rssreader.model.RssAction;
import com.lgq.rssreader.model.SyncState;

import java.util.List;

/**
 * Created by redel on 2015-09-12.
 */
public interface RssCallback<T> {
    void onSuccess(T data, boolean result, String msg);
    void onSuccess(T data, boolean result, String msg, boolean more);
    void onSuccess(T data);
    void onFailure(String error);
}

