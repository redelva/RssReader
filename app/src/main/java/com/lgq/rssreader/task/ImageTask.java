package com.lgq.rssreader.task;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import java.io.InputStream;

/**
 * Created by redel on 2015-09-22.
 */
public class ImageTask extends AsyncTask<String, Void, Bitmap> {
    ImageView bmImage;

    public ImageTask(ImageView bmImage) {
        this.bmImage = bmImage;
    }

    protected Bitmap doInBackground(String... urls) {
        String urldisplay = urls[0];
        Bitmap icon = null;
        try {
            if(urldisplay != null && urldisplay.length() > 0 ){
                InputStream in = new java.net.URL(urldisplay).openStream();
                icon = BitmapFactory.decodeStream(in);
            }
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            e.printStackTrace();
        }
        return icon;
    }

    protected void onPostExecute(Bitmap result) {
        if(result != null){
            bmImage.setImageBitmap(result);
        }
        else{
            bmImage.setVisibility(View.GONE);
        }
    }
}
