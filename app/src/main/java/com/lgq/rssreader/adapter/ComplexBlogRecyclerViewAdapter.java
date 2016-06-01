package com.lgq.rssreader.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.lgq.rssreader.R;
import com.lgq.rssreader.core.ReaderApp;
import com.lgq.rssreader.enums.ItemType;
import com.lgq.rssreader.model.Blog;
import com.lgq.rssreader.util.DateUtil;
import com.lgq.rssreader.util.HtmlUtil;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

//import com.lgq.rssreader.db.DataAccess;

/**
 * 订阅Blog列表
 */
public class ComplexBlogRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>  implements View.OnClickListener,View.OnLongClickListener{
    private ItemType itemType;
    private final LayoutInflater mLayoutInflater;
    private List<Blog> mBlogs;
    OnRecyclerViewItemClickListener<Blog> mOnItemClickListener;
    private Context mContext;

    public ItemType getItemType(){return itemType;}
    public void setItemType(ItemType itemType){this.itemType = itemType;}

    public ComplexBlogRecyclerViewAdapter(Context context, List<Blog> data, ItemType itemType){
        this.itemType = itemType;
        mLayoutInflater = LayoutInflater.from(context);
        mBlogs = data;
        mContext = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        RecyclerView.ViewHolder holder;
        if (viewType == ItemType.Small.ordinal()) {
            view = mLayoutInflater.inflate(R.layout.blog_text, parent, false);
            holder = new BlogTextViewHolder(view, true);
        } else {
            view = mLayoutInflater.inflate(R.layout.blog_bigtext, parent, false);
            holder = new BlogBigTextViewHolder(view, true);
        }

        view.setOnClickListener(this);
        view.setOnLongClickListener(this);

        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        if(viewHolder instanceof BlogTextViewHolder){
            BlogTextViewHolder holder = (BlogTextViewHolder)viewHolder;
            Blog entity = mBlogs.get(position);
            holder.mTitleView.setText(entity.getTitle());
            holder.mTitleView.setTag(entity);

            holder.mSubtitleView.setText(String.valueOf(entity.getSubsTitle()));
            holder.mSubtitleView.setTag(entity);

            holder.mDateView.setText(DateUtil.getInterval(entity.getPubDate(), ""));
            holder.mDateView.setTag(entity);

            TypedArray text = mContext.getTheme().obtainStyledAttributes(new int[] {
                    R.attr.textColor
            });
            int unreadColor = text.getColor(0, Color.RED);
            text.recycle();

            TypedArray readText = mContext.getTheme().obtainStyledAttributes(new int[] {
                    R.attr.readTextColor
            });
            int readColor = readText.getColor(0, Color.RED);
            readText.recycle();

            if(entity.getIsRead()){
                holder.mTitleView.setTextColor(readColor);
                holder.mSubtitleView.setTextColor(readColor);
            }
            else{
                holder.mTitleView.setTextColor(unreadColor);
                holder.mSubtitleView.setTextColor(unreadColor);
            }

            //new ImageTask(holder.mAvatarView).execute(mBlogs.get(position).getAvatar());
            if(mBlogs.get(position).getAvatar() != null && !mBlogs.get(position).getAvatar().equals("none")){
                //显示图片的配置
                DisplayImageOptions options = new DisplayImageOptions.Builder()
                        .cacheInMemory(true)
                        .cacheOnDisk(true)
                        .bitmapConfig(Bitmap.Config.RGB_565)
                        .build();

                ImageLoader.getInstance().displayImage(mBlogs.get(position).getAvatar(), holder.mAvatarView, options);

            }else{
                holder.mAvatarView.setVisibility(View.GONE);
            }

            holder.parent.setTag(mBlogs.get(position));
        }

        if(viewHolder instanceof  BlogBigTextViewHolder){
            BlogBigTextViewHolder holder = (BlogBigTextViewHolder)viewHolder;
            Blog entity = mBlogs.get(position);
            holder.mTitleView.setText(entity.getTitle());
            holder.mTitleView.setTag(entity);

            if(itemType == ItemType.Big) {
                holder.mSummaryView.setVisibility(View.VISIBLE);
                String summary = HtmlUtil.trim(HtmlUtil.filterHtml(entity.getDescription())).replace(" ","");
                if (summary.length() > 100) {
                    holder.mSummaryView.setText(summary.substring(0, 100));
                } else {
                    holder.mSummaryView.setText(summary);
                }
                holder.mSummaryView.setTag(entity);
            }else{
                holder.mSummaryView.setVisibility(View.GONE);
            }

            holder.mSubtitleView.setText(String.valueOf(entity.getSubsTitle()));
            holder.mSubtitleView.setTag(entity);

            holder.mDateView.setText(DateUtil.getInterval(entity.getPubDate(), ""));
            holder.mDateView.setTag(entity);

            if(entity.getIsRead()){
                //holder.blog_read.setVisibility(View.VISIBLE);
                //holder.blog_read.setImageResource(R.drawable.keepread);
                holder.mTitleView.setTextColor(Color.GRAY);
                if(itemType == ItemType.Big)
                    holder.mSummaryView.setTextColor(Color.GRAY);
                holder.mSubtitleView.setTextColor(Color.GRAY);
            }
            else{
                holder.mTitleView.setTextColor(Color.BLACK);
                if(itemType == ItemType.Big)
                    holder.mSummaryView.setTextColor(Color.GRAY);
                //holder.blog_read.setVisibility(View.GONE);
            }

            //new ImageTask(holder.mAvatarView).execute(mBlogs.get(position).getAvatar());
            if(mBlogs.get(position).getAvatar() != null && !mBlogs.get(position).getAvatar().equals("none")){
                //显示图片的配置
                DisplayImageOptions options = new DisplayImageOptions.Builder()
                        .cacheInMemory(true)
                        .cacheOnDisk(true)
                        .bitmapConfig(Bitmap.Config.RGB_565)
                        .build();

                ImageLoader.getInstance().displayImage(mBlogs.get(position).getAvatar(), holder.mAvatarView, options);

            }else{
                holder.mAvatarView.setVisibility(View.GONE);
            }

            holder.parent.setTag(mBlogs.get(position));
        }
    }

    @Override
    public int getItemViewType(int position) {
        return itemType.ordinal();
    }

    @Override
    public int getItemCount() {
        return mBlogs == null ? 0 : mBlogs.size();
    }

    public void setOnItemClickListener(OnRecyclerViewItemClickListener<Blog> listener) {
        this.mOnItemClickListener = listener;
    }

    @Override
    public void onClick(View v) {
        if (mOnItemClickListener != null) {
            //注意这里使用getTag方法获取数据
            mOnItemClickListener.onItemClick(v, (Blog)v.getTag());
        }
    }

    @Override
    public boolean onLongClick(View v) {
        if (mOnItemClickListener != null) {
            //注意这里使用getTag方法获取数据
            mOnItemClickListener.onItemLongClick(v, (Blog) v.getTag());
        }

        return true;
    }

    public static class BlogTextViewHolder extends RecyclerView.ViewHolder {//RecyclerView.ViewHolder {
        TextView mTitleView;
        TextView mSubtitleView;
        ImageView mReadView;
        ImageView mStarView;
        ImageView mAvatarView;
        TextView mDateView;
        View parent;

        BlogTextViewHolder(View view, boolean isItem) {
            super(view);
            if(isItem){
                parent = view;
                mTitleView = (TextView)view.findViewById(R.id.blog_title);
                mSubtitleView = (TextView)view.findViewById(R.id.blog_subtitle);
                mReadView = (ImageView)view.findViewById(R.id.blog_read);
                mStarView = (ImageView)view.findViewById(R.id.blog_star);
                mAvatarView = (ImageView)view.findViewById(R.id.blog_avatar);
                mDateView = (TextView)view.findViewById(R.id.blog_date);
            }
        }
    }

    public static class BlogBigTextViewHolder extends RecyclerView.ViewHolder {
        TextView mTitleView;
        TextView mSummaryView;
        TextView mSubtitleView;
        ImageView mReadView;
        ImageView mStarView;
        ImageView mAvatarView;
        TextView mDateView;
        View parent;

        BlogBigTextViewHolder(View view, boolean isItem) {
            super(view);
            if(isItem){
                parent = view;
                mTitleView = (TextView)view.findViewById(R.id.blog_title);
                mSubtitleView = (TextView)view.findViewById(R.id.blog_subtitle);
                mReadView = (ImageView)view.findViewById(R.id.blog_read);
                mStarView = (ImageView)view.findViewById(R.id.blog_star);
                mAvatarView = (ImageView)view.findViewById(R.id.blog_avatar);
                mDateView = (TextView)view.findViewById(R.id.blog_date);
                mSummaryView = (TextView)view.findViewById(R.id.blog_summary);
            }
        }
    }

    public static class BlogTextViewHolderFactory implements ViewHolderFactory<RecyclerView.ViewHolder>{

        private ItemType itemType;

        public BlogTextViewHolderFactory(ItemType itemType){
            this.itemType = itemType;
        }

        @Override
        public RecyclerView.ViewHolder create(View v) {
            if(itemType == ItemType.Small)
                return new BlogTextViewHolder(v, true);

            if(itemType == ItemType.Big)
                return new BlogBigTextViewHolder(v, true);

            return null;
        }
    }
}
