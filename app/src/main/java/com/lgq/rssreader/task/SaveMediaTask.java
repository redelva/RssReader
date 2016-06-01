package com.lgq.rssreader.task;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.Toast;

import com.lgq.rssreader.R;
import com.lgq.rssreader.util.ImageUtil;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by redel on 2015-10-21.
 */
public class SaveMediaTask extends AsyncTask<String, Void, Void> {

    WeakReference<Context> mContext;

    public SaveMediaTask(Context mContext){
        this.mContext = new WeakReference<>(mContext);
    }

    @Override
    protected Void doInBackground(String... urls){
        URL aURL;
        if(urls == null || urls.length == 0 || mContext.get() == null){
            return null;
        }
        String url = urls[0];
        try {
            aURL = new URL(url);
            URLConnection conn = aURL.openConnection();
            conn.connect();
            InputStream is = conn.getInputStream();
            BufferedInputStream bis = new BufferedInputStream(is);
            Bitmap bm = BitmapFactory.decodeStream(bis);

            ImageUtil.saveImage(mContext.get(), bm);

//            ContentResolver cr = mContext.get().getContentResolver();
//            String path = MediaStore.Images.Media.insertImage(cr, bm, "RssReader", "this is a Photo from RssReader");
//
//            MediaScannerConnection.scanFile(mContext.get(), new String[] {
//                    Environment.getExternalStorageDirectory().getAbsolutePath()
//            }, null, new MediaScannerConnection.OnScanCompletedListener() {
//
//                @Override
//                public void onScanCompleted(String path, Uri uri) {
//                    // TODO Auto-generated method stub
//
//                }
//            });

        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        Toast.makeText(mContext.get(), mContext.get().getResources().getString(R.string.content_savetolibrary), Toast.LENGTH_SHORT).show();

        //if(mContext.get() != null)
        //    mContext.get().sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://"+ Environment.getExternalStorageDirectory())));;
    }
}
