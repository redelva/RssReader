package com.lgq.rssreader.adapter;

import android.content.Context;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.lgq.rssreader.R;
//import com.lgq.rssreader.db.DataAccess;
import com.lgq.rssreader.core.ReaderApp;
import com.lgq.rssreader.model.Channel;
import com.lgq.rssreader.model.Subscription;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * 订阅Channel列表
 */
public abstract class BaseRecyclerViewAdapter<T, K extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<K> implements View.OnClickListener,View.OnLongClickListener{
    //private LayoutInflater mLayoutInflater;
    private OnRecyclerViewItemClickListener<T> mOnItemClickListener = null;
    private List<T> mData;
    private WeakReference<Context> mContext;
    private ViewHolderFactory<K> mFactory;
    private int itemLayoutId;

    public BaseRecyclerViewAdapter(Context context,  List<T> data, ViewHolderFactory<K> factory, @LayoutRes int itemLayout) {
        mData = data;
        mContext = new WeakReference<Context>(context);
        this.mFactory = factory;
        this.itemLayoutId = itemLayout;
    }

    @Override
    public K onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(itemLayoutId, parent, false);
        K holder = mFactory.create(view);
        view.setOnClickListener(this);
        view.setOnLongClickListener(this);

        return holder;
    }

    public void setOnItemClickListener(OnRecyclerViewItemClickListener<T> listener) {
        this.mOnItemClickListener = listener;
    }

    public List<T> getData(){return mData;}

    @Override
    public void onClick(View v) {
        if (mOnItemClickListener != null) {
            //注意这里使用getTag方法获取数据
            mOnItemClickListener.onItemClick(v, (T)v.getTag());
        }
    }

    @Override
    public boolean onLongClick(View v) {
        if (mOnItemClickListener != null) {
            //注意这里使用getTag方法获取数据
            mOnItemClickListener.onItemLongClick(v, (T) v.getTag());
        }

        return true;
    }

    @Override
    public void onBindViewHolder(K holder, int position) {
        bindItemViewHolder(holder, position);
    }

    public abstract void bindItemViewHolder(K holder, int position);

    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.size();
    }


}
