package com.lgq.rssreader.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

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
public class ChannelRecyclerViewAdapter extends RecyclerView.Adapter<ChannelRecyclerViewAdapter.ChannelTextViewHolder> implements View.OnClickListener,View.OnLongClickListener{
    //private LayoutInflater mLayoutInflater;
    private OnRecyclerViewItemClickListener<Channel> mOnItemClickListener = null;
    private List<Channel> mChannels;
    private WeakReference<Context> mContext;

    public ChannelRecyclerViewAdapter(Context context,  List<Channel> datas) {
        mChannels = datas;
        mContext = new WeakReference<Context>(context);
        //if(mContext.get() != null)
            //mLayoutInflater = LayoutInflater.from(mContext.get());
    }

    @Override
    public ChannelTextViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.channel_text, parent, false);
        ChannelTextViewHolder holder = new ChannelTextViewHolder(view);
        view.setOnClickListener(this);
        view.setOnLongClickListener(this);

        return holder;
    }

    public void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    @Override
    public void onClick(View v) {
        if (mOnItemClickListener != null) {
            //注意这里使用getTag方法获取数据
            mOnItemClickListener.onItemClick(v,(Channel)v.getTag());
        }
    }

    @Override
    public boolean onLongClick(View v) {
        if (mOnItemClickListener != null) {
            //注意这里使用getTag方法获取数据
            mOnItemClickListener.onItemLongClick(v,(Channel)v.getTag());
        }

        return true;
    }

    @Override
    public void onBindViewHolder(ChannelTextViewHolder holder, int position) {
        holder.mTextView.setText(mChannels.get(position).getTitle());
        holder.mTextView.setTag(mChannels.get(position));

        holder.mCountView.setText(String.valueOf(mChannels.get(position).getUnreadCount()));
        holder.mCountView.setTag(mChannels.get(position));

        holder.mIconView.setImageResource(mChannels.get(position).getIsDirectory() ? R.mipmap.folder : R.mipmap.rss);
        holder.mIconView.setTag(mChannels.get(position));

        holder.parent.setTag(mChannels.get(position));
    }

    @Override
    public int getItemCount() {
        return mChannels == null ? 0 : mChannels.size();
    }

    public static class ChannelTextViewHolder extends RecyclerView.ViewHolder {
        TextView mTextView;
        TextView mCountView;
        ImageView mIconView;
        View parent;

        ChannelTextViewHolder(View view) {
            super(view);
            parent = view;
            mTextView = (TextView)view.findViewById(R.id.channel_title);
            mCountView = (TextView)view.findViewById(R.id.channel_count);
            mIconView = (ImageView)view.findViewById(R.id.channel_icon);
        }
    }
}
