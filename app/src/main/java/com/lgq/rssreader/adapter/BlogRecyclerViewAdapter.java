package com.lgq.rssreader.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.lgq.rssreader.R;
import com.lgq.rssreader.model.Blog;
import com.lgq.rssreader.model.Channel;
import com.lgq.rssreader.task.ImageTask;
import com.lgq.rssreader.util.DateUtil;
import com.marshalchen.ultimaterecyclerview.RecyclerItemClickListener;
import com.marshalchen.ultimaterecyclerview.UltimateRecyclerviewViewHolder;
import com.marshalchen.ultimaterecyclerview.UltimateViewAdapter;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.lang.ref.WeakReference;
import java.security.SecureRandom;
import java.util.List;

//import com.lgq.rssreader.db.DataAccess;

/**
 * 订阅Blog列表
 */
public class BlogRecyclerViewAdapter extends UltimateViewAdapter<BlogRecyclerViewAdapter.BlogTextViewHolder> implements View.OnClickListener{
    //private LayoutInflater mLayoutInflater;
    private WeakReference<Context> mContext;
    private List<Blog> mBlogs;

    public BlogRecyclerViewAdapter(Context context, List<Blog> datas) {
        mBlogs = datas;
        mContext = new WeakReference<Context>(context);
        if(mContext.get() != null){
            //mLayoutInflater = LayoutInflater.from(mContext.get());
        }
    }

    @Override
    public BlogTextViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.blog_text, parent, false);
        BlogTextViewHolder holder = new BlogTextViewHolder(view, true);
        view.setOnClickListener(this);
        return holder;
    }

    @Override
    public BlogTextViewHolder onCreateViewHolder(ViewGroup parent) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.blog_text, parent, false);
        BlogTextViewHolder vh = new BlogTextViewHolder(v, true);
        return vh;
    }

    @Override
    public RecyclerView.ViewHolder onCreateHeaderViewHolder(ViewGroup viewGroup) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.stick_header_item, viewGroup, false);
        return new RecyclerView.ViewHolder(view){} ;
    }

    public Blog getItem(int position) {
        if (customHeaderView != null)
            position--;
        if (position < mBlogs.size())
            return mBlogs.get(position);
        else
            return null;
    }

    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder viewHolder, int position) {

        TextView textView = (TextView) viewHolder.itemView.findViewById(R.id.stick_text);
        textView.setText(String.valueOf(getItem(position).getTitle().charAt(0)));
//        viewHolder.itemView.setBackgroundColor(Color.parseColor("#AA70DB93"));
        viewHolder.itemView.setBackgroundColor(Color.parseColor("#AAffffff"));
        ImageView imageView = (ImageView) viewHolder.itemView.findViewById(R.id.stick_img);

        imageView.setImageResource(R.drawable.test_back3);

    }

    @Override
    public long generateHeaderId(int position) {
        // URLogs.d("position--" + position + "   " + getItem(position));
        if (mBlogs.get(position).getTitle().length() > 0)
            return mBlogs.get(position).getTitle().charAt(0);
        else
            return -1;
    }

    @Override
    public BlogTextViewHolder getViewHolder(View view) {
        return new BlogTextViewHolder(view, false);
    }

//    public void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
//        this.mOnItemClickListener = listener;
//    }

    @Override
    public int getAdapterItemCount() {
        return mBlogs.size();
    }

    @Override
    public void onBindViewHolder(BlogTextViewHolder holder, int position) {
        Blog entity = mBlogs.get(position);
        holder.mTitleView.setText(entity.getTitle());
        holder.mTitleView.setTag(entity);

        holder.mSubtitleView.setText(String.valueOf(entity.getSubsTitle()));
        holder.mSubtitleView.setTag(entity);

        holder.mDateView.setText(DateUtil.getInterval(entity.getPubDate(), ""));
        holder.mDateView.setTag(entity);

        if(entity.getIsRead()){
            //holder.blog_read.setVisibility(View.VISIBLE);
            //holder.blog_read.setImageResource(R.drawable.keepread);
            holder.mTitleView.setTextColor(Color.GRAY);
            holder.mSubtitleView.setTextColor(Color.GRAY);
        }
        else{
            holder.mTitleView.setTextColor(Color.BLACK);
            //holder.blog_read.setVisibility(View.GONE);
        }

//        if(entity.getIsStarred()){
//            holder.blog_star.setVisibility(View.VISIBLE);
//            holder.blog_star.setImageResource(R.drawable.star);
//        }
//        else{
//            holder.blog_star.setVisibility(View.GONE);
//        }

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

    @Override
    public int getItemCount() {
        return mBlogs == null ? 0 : mBlogs.size();
    }

    @Override
    public void onClick(View v) {

    }

    public static class BlogTextViewHolder extends UltimateRecyclerviewViewHolder {//RecyclerView.ViewHolder {
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
}
