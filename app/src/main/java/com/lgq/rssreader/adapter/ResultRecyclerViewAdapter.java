package com.lgq.rssreader.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.lgq.rssreader.R;
import com.lgq.rssreader.core.ReaderApp;
import com.lgq.rssreader.model.Blog;
import com.lgq.rssreader.model.Result;
import com.lgq.rssreader.util.DateUtil;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

//import com.lgq.rssreader.db.DataAccess;

/**
 * 订阅Blog列表
 */
public class ResultRecyclerViewAdapter extends BaseRecyclerViewAdapter<Result, ResultRecyclerViewAdapter.ResultTextViewHolder>{
    public ResultRecyclerViewAdapter(Context context, List<Result> data, ViewHolderFactory<ResultTextViewHolder> factory){
        super(context, data, factory, R.layout.result_text);
    }

    @Override
    public void bindItemViewHolder(ResultTextViewHolder holder, int position) {

        Result entity = this.getData().get(position);

        holder.result_desc.setText(entity.getDescription());
        holder.result_title.setText(entity.getTitle());
        holder.result_info.setText(entity.getSubscriptCount() + ReaderApp.getContext().getString(R.string.subscribe_count));

        if(entity.getAvatar() != null && !entity.getAvatar().equals("none")){
            //显示图片的配置
            DisplayImageOptions options = new DisplayImageOptions.Builder()
                    .cacheInMemory(true)
                    .cacheOnDisk(true)
                    .bitmapConfig(Bitmap.Config.RGB_565)
                    .build();

            ImageLoader.getInstance().displayImage(entity.getAvatar(), holder.channel_avatar, options);

        }else{
            holder.channel_avatar.setVisibility(View.GONE);
        }

        holder.parent.setTag(getData().get(position));
    }

    public static class ResultTextViewHolder extends RecyclerView.ViewHolder {//RecyclerView.ViewHolder {
        TextView result_desc;
        TextView result_title;
        TextView result_info;
        ImageView channel_avatar;
        View parent;

        ResultTextViewHolder(View view, boolean isItem) {
            super(view);
            if(isItem){
                parent = view;
                result_desc = (TextView)view.findViewById(R.id.result_desc);
                result_title = (TextView)view.findViewById(R.id.result_title);
                result_info = (TextView)view.findViewById(R.id.result_info);
                channel_avatar = (ImageView)view.findViewById(R.id.channel_avatar);
            }
        }
    }

    public static class ResultTextViewHolderFactory implements ViewHolderFactory<ResultTextViewHolder>{
        @Override
        public ResultTextViewHolder create(View v) {
            return new ResultTextViewHolder(v, true);
        }
    }
}
