package com.lgq.rssreader.util;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

/**
 * Created by redel on 2016-03-07.
 */
public abstract class BroadcastReceiverEx extends BroadcastReceiver {
    Context ct = null;
    BroadcastReceiverEx receiver;

    public BroadcastReceiverEx(Context c){
        ct = c;
        receiver = this;
    }

    public abstract void onBroadcast();

    //注册
    public void registerAction(String action){
        IntentFilter filter = new IntentFilter();
        filter.addAction(action);
        ct.registerReceiver(receiver, filter);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        onBroadcast();
    }
}
