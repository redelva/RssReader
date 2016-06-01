package com.lgq.rssreader.controls;

/**
 * Created by redel on 2016-03-24.
 */
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.lgq.rssreader.R;
import com.lgq.rssreader.controls.scrollgalleryview.loader.MediaLoader;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

/**
 * Author: Alexey Nevinsky
 * Date: 06.12.15 1:38
 */
public class SuperImageLoader implements MediaLoader {

    private String url;

    public SuperImageLoader(String url) {
        this.url = url;
    }

    @Override
    public boolean isImage() {
        return true;
    }

    @Override
    public void loadMedia(Context context, final ImageView imageView, final SuccessCallback callback) {
//        Picasso.with(context)
//                .load(url)
//                .placeholder(R.drawable.placeholder_image)
//                .into(imageView, new ImageCallback(callback));

        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.placeholder_image)         //加载开始默认的图片
                .showImageForEmptyUri(R.drawable.placeholder_image)     //url爲空會显示该图片，自己放在drawable里面的
                .showImageOnFail(R.drawable.placeholder_image)                //加载图片出现问题，会显示该图片
                .cacheInMemory(true)                                               //缓存用
                .cacheOnDisk(true)                                                    //缓存用
                .build();

        ImageLoader.getInstance().loadImage(url, options, new SimpleImageLoadingListener(){
            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                imageView.setImageBitmap(loadedImage);
                callback.onSuccess();
            }
        });
    }

    @Override
    public void loadThumbnail(Context context, final ImageView thumbnailView, final SuccessCallback callback) {
//        Picasso.with(context)
//                .load(url)
//                .resize(100, 100)
//                .placeholder(R.drawable.placeholder_image)
//                .centerInside()
//                .into(thumbnailView, new ImageCallback(callback));

        ImageSize targetSize = new ImageSize(100, 100);
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.placeholder_image)         //加载开始默认的图片
                .showImageForEmptyUri(R.drawable.placeholder_image)     //url爲空會显示该图片，自己放在drawable里面的
                .showImageOnFail(R.drawable.placeholder_image)                //加载图片出现问题，会显示该图片
                .cacheInMemory(true)                                               //缓存用
                .cacheOnDisk(true)                                                    //缓存用
                .build();

        ImageLoader.getInstance().loadImage(url, targetSize, options, new SimpleImageLoadingListener(){
            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                thumbnailView.setImageBitmap(loadedImage);
                callback.onSuccess();
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                Log.e("RssReader", imageUri + " failed at " + failReason.toString());
                super.onLoadingFailed(imageUri, view, failReason);
            }
        });
    }

    private static class ImageCallback implements Callback {

        private final SuccessCallback callback;

        public ImageCallback(SuccessCallback callback) {
            this.callback = callback;
        }

        @Override
        public void onSuccess() {
            callback.onSuccess();
        }

        @Override
        public void onError() {

        }
    }
}