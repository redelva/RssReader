package com.lgq.rssreader.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.lgq.rssreader.R;
import com.lgq.rssreader.model.Blog;
import com.lgq.rssreader.model.ImageRecord;
import com.lgq.rssreader.util.DateUtil;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;
import java.util.List;

//import com.lgq.rssreader.db.DataAccess;

/**
 * 订阅Blog列表
 */
public class ImageRecyclerViewAdapter extends BaseRecyclerViewAdapter<ImageRecord,ImageRecyclerViewAdapter.ImageViewHolder>{
    public ImageRecyclerViewAdapter(Context context, List<ImageRecord> data, ViewHolderFactory<ImageRecyclerViewAdapter.ImageViewHolder> factory){
        super(context, data, factory, R.layout.image_view);
    }

    @Override
    public void bindItemViewHolder(ImageRecyclerViewAdapter.ImageViewHolder holder, int position) {

        ImageRecord record = this.getData().get(position);

        String url = "file:///" + android.os.Environment.getExternalStorageDirectory().getAbsolutePath() + record.getStoredName();

        //显示图片的配置
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();

        ImageLoader.getInstance().displayImage(url, holder.mImageView, options);

        holder.parent.setTag(getData().get(position));
    }

    public static class ImageViewHolder extends RecyclerView.ViewHolder {//RecyclerView.ViewHolder {
        ImageView mImageView;
        View parent;

        ImageViewHolder(View view, boolean isItem) {
            super(view);
            if(isItem){
                parent = view;
                mImageView = (ImageView)view.findViewById(R.id.image);
            }
        }
    }

    public static class ImageViewHolderFactory implements ViewHolderFactory<ImageViewHolder>{

        @Override
        public ImageViewHolder create(View v) {
            return new ImageViewHolder(v, true);
        }
    }
}
