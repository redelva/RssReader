package com.lgq.rssreader.util;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

/**
 * Created by redel on 2016-6-13.
 */
public class NotificationUtil {
    private Context mContext;
    private int NOTIFICATION_ID = 1;
    private Notification mNotification;
    private NotificationManager mNotificationManager;
    private PendingIntent mContentIntent;
    private CharSequence mContentTitle;

    public NotificationUtil(Context context)
    {
        mContext = context;
    }

    /**
     * Put the notification into the status bar
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void createNotification() {
        mNotificationManager = (NotificationManager)mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        //Build the notification using Notification.Builder
        Notification.Builder builder = new Notification.Builder(mContext)
                .setSmallIcon(android.R.drawable.stat_sys_download)
                .setAutoCancel(true)
                .setContentTitle("Download")
                .setContentText("0% complete");


        //Show the notification
        mNotificationManager.notify(NOTIFICATION_ID, builder.build());
    }

    /**
     * Receives progress updates from the background task and updates the status bar notification appropriately
     * @param percentageComplete
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void progressUpdate(int percentageComplete) {
        //build up the new status message
        CharSequence contentText = percentageComplete + "% complete";
        //publish it to the status bar
        Notification.Builder builder = new Notification.Builder(mContext)
                .setSmallIcon(android.R.drawable.stat_sys_download)
                .setAutoCancel(true)
                .setContentTitle("Download")
                .setContentText(contentText);
        mNotificationManager.notify(NOTIFICATION_ID, builder.build());
    }

    /**
     * called when the background task is complete, this removes the notification from the status bar.
     * We could also use this to add a new ‘task complete’ notification
     */
    public void completed()    {
        //remove the notification from the status bar
        mNotificationManager.cancel(NOTIFICATION_ID);
    }
}
