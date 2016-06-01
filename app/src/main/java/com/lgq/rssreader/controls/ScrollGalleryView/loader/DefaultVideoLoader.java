package com.lgq.rssreader.controls.scrollgalleryview.loader;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.lgq.rssreader.R;
import com.lgq.rssreader.controls.scrollgalleryview.Constants;
import com.lgq.rssreader.controls.scrollgalleryview.VideoPlayerActivity;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Author: Alexey Nevinsky
 * Date: 17.12.15 0:01
 */
public class DefaultVideoLoader implements MediaLoader {

    private String url;
    private int mId;
    private int mDefaultId;
    private String poster;
    private Bitmap mBitmap;
    private Bitmap mThumbnailBitmap;

    public DefaultVideoLoader(String url, int mId) {
        this.url = url;
        this.mId = mId;
    }

    public DefaultVideoLoader(String url, String poster, int mDefaultId) {
        this.url = url;
        this.mDefaultId = mDefaultId;
        this.poster = poster;
    }

    @Override
    public boolean isImage() {
        return false;
    }

    @Override
    public void loadMedia(final Context context, ImageView imageView, SuccessCallback callback) {
        loadBitmap(context, imageView, false);
        imageView.setImageBitmap(mBitmap);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayVideo(context, url);
            }
        });
    }

    @Override
    public void loadThumbnail(Context context, ImageView thumbnailView, SuccessCallback callback) {
        loadBitmap(context, thumbnailView, true);
        thumbnailView.setImageBitmap(mThumbnailBitmap);
        callback.onSuccess();
    }

    private void displayVideo(Context context, String url) {
        Intent intent = new Intent(context, VideoPlayerActivity.class);
        intent.putExtra(Constants.URL, url);
        context.startActivity(intent);
    }

    private void loadBitmap(final Context context, final ImageView imageView, final boolean isThumbnailView) {
        //if (mBitmap == null)
        if(!isThumbnailView)
        {
            if(mId > 0) {
                mBitmap = ((BitmapDrawable) context.getResources().getDrawable(mId)).getBitmap();
            }
            else {
                ImageLoader.getInstance().loadImage(poster, new ImageLoadingListener(){

                    @Override
                    public void onLoadingStarted(String imageUri, View view) {
                        mBitmap = ((BitmapDrawable) context.getResources().getDrawable(mDefaultId)).getBitmap();
                        imageView.setImageBitmap(mBitmap);
                    }

                    @Override
                    public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                        mBitmap = ((BitmapDrawable) context.getResources().getDrawable(mDefaultId)).getBitmap();
                        imageView.setImageBitmap(mBitmap);
                    }

                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                        mBitmap = loadedImage;
                        imageView.setAdjustViewBounds(true);
                        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                        imageView.setImageBitmap(mBitmap);
                    }

                    @Override
                    public void onLoadingCancelled(String imageUri, View view) {
                        mBitmap = ((BitmapDrawable) context.getResources().getDrawable(mDefaultId)).getBitmap();
                        imageView.setImageBitmap(mBitmap);
                    }
                });
            }
        }

        if (mThumbnailBitmap == null)
        {
            if(mId > 0) {
                mThumbnailBitmap = ((BitmapDrawable) context.getResources().getDrawable(mId)).getBitmap();
            }
            else {
                ImageLoader.getInstance().loadImage(poster, new ImageLoadingListener(){

                    @Override
                    public void onLoadingStarted(String imageUri, View view) {

                    }

                    @Override
                    public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

                    }

                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                        mThumbnailBitmap = loadedImage;
                        imageView.setImageBitmap(((BitmapDrawable) context.getResources().getDrawable(mDefaultId)).getBitmap());
                    }

                    @Override
                    public void onLoadingCancelled(String imageUri, View view) {

                    }
                });
            }
        }
    }
}
