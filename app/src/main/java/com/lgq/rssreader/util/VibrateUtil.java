package com.lgq.rssreader.util;

import android.os.Vibrator;

import com.lgq.rssreader.core.ReaderApp;

/**
 * Created by redel on 2015-12-27.
 */
public class VibrateUtil {
    public static void vibrate(){

        Vibrator vibrator = (Vibrator) ReaderApp.getContext().getSystemService(ReaderApp.getContext().VIBRATOR_SERVICE);

        //if(ReaderApp.getSettings().EnableVibrate){
            vibrator.vibrate(1000);
            //vibrator.vibrate(new long[]{1000,2000,3000,4000}, 0);
        //}
    }
}
