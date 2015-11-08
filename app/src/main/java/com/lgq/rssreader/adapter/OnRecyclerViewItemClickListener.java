package com.lgq.rssreader.adapter;

import android.view.View;

/**
 * Created by redel on 2015-09-03.
 */
public interface OnRecyclerViewItemClickListener<T>{
    void onItemClick(View view , T data);
    void onItemLongClick(View view , T data);
}
