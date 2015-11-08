package com.lgq.rssreader.abstraction;

import android.os.Looper;

/**
 * Created by redel on 2015-09-12.
 */
public class RssHandler<T> implements RssCallback<T> {

    public void onSuccess(T data, boolean result, String msg){

    }

    public void onSuccess(T data, boolean result, String msg, boolean more){

    }

    public void onSuccess(T data){

    }

    public void onFailure(String error){

    }
}
