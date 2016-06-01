package com.lgq.rssreader.adapter;

import android.view.View;

/**
 * Created by redel on 2015-11-11.
 */
public interface ViewHolderFactory<T> {
    T create(View v);
}
