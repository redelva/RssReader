package com.lgq.rssreader.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.lgq.rssreader.R;
import com.lgq.rssreader.model.Blog;
import com.lgq.rssreader.util.DateUtil;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import java.util.List;

//import com.lgq.rssreader.db.DataAccess;

/**
 * 订阅Blog列表
 */
public class ImageRecyclerViewAdapter extends BaseRecyclerViewAdapter<Blog,ImageRecyclerViewAdapter.BlogTextViewHolder>{
    public ImageRecyclerViewAdapter(Context context, List<Blog> data, ViewHolderFactory<ImageRecyclerViewAdapter.BlogTextViewHolder> factory){
        super(context, data, factory, R.layout.blog_text);
    }

    @Override
    public void bindItemViewHolder(ImageRecyclerViewAdapter.BlogTextViewHolder holder, int position) {

        Blog entity = this.getData().get(position);
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
        if(getData().get(position).getAvatar() != null && !getData().get(position).getAvatar().equals("none")){
            //显示图片的配置
            DisplayImageOptions options = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();

            ImageLoader.getInstance().displayImage(getData().get(position).getAvatar(), holder.mAvatarView, options);
        }else{
            holder.mAvatarView.setVisibility(View.GONE);
        }

        holder.parent.setTag(getData().get(position));


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

    public static class BlogTextViewHolderFactory implements ViewHolderFactory<BlogTextViewHolder>{

        @Override
        public BlogTextViewHolder create(View v) {
            return new BlogTextViewHolder(v, true);
        }
    }
}
