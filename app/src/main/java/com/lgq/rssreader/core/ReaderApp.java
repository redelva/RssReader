package com.lgq.rssreader.core;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.Toast;

import com.lgq.rssreader.MainActivity;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

import java.io.File;
import java.io.FileOutputStream;
import java.util.concurrent.Executors;

/**
 * Created by redel on 2015-09-26.
 */
public class ReaderApp extends com.orm.SugarApp {
    private static String ERRORLOG_LOCATION = "/Android/data/com.lgq.rssreader/error";

    public static Context getContext() {
        return ReaderApp.getSugarContext();
    }

    public static RefWatcher getRefWatcher(Context context) {
        ReaderApp application = (ReaderApp) context.getApplicationContext();
        return application.refWatcher;
    }

    private RefWatcher refWatcher;

    @Override public void onCreate() {
        super.onCreate();
        refWatcher = LeakCanary.install(this);
        //创建默认的ImageLoader配置参数
        ImageLoaderConfiguration configuration = new ImageLoaderConfiguration.Builder(this)
                .taskExecutor(Executors.newCachedThreadPool())
                .taskExecutorForCachedImages(Executors.newCachedThreadPool())
                .build();

        //Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(configuration);

        initExpcetionHandler();
    }

    private void initExpcetionHandler(){
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread paramThread, Throwable paramThrowable) {

                String sDStateString = android.os.Environment.getExternalStorageState();

                if (sDStateString.equals(android.os.Environment.MEDIA_MOUNTED)) {
                    try {
                        File SDFile = android.os.Environment.getExternalStorageDirectory();

                        File dir = new File(SDFile.getAbsolutePath() + ERRORLOG_LOCATION);

                        if (!dir.exists()) {
                            dir.mkdirs();
                        }

                        File myFile = new File(SDFile.getAbsolutePath() + ERRORLOG_LOCATION + "/" + String.valueOf(System.currentTimeMillis()) + ".txt");

                        myFile.createNewFile();

                        FileOutputStream outputStream = new FileOutputStream(myFile);
                        StringBuilder error = new StringBuilder();

                        error.append("Error msg:" + paramThrowable.getMessage() + "\r\n");

                        error.append("Error throwable:" + paramThrowable.toString() + "\r\n");

                        for(StackTraceElement element : paramThrowable.getStackTrace()){
                            error.append(element.toString() + "\r\n");
                        }

                        if(paramThrowable.getCause() != null){
                            error.append("Caused by:" + paramThrowable.getCause().toString() + "\r\n");

                            error.append("Error caused:" + paramThrowable.getCause().toString() + "\r\n");

                            for(StackTraceElement element : paramThrowable.getCause().getStackTrace()){
                                error.append(element.toString() + "\r\n");
                            }
                        }

                        outputStream.write(error.toString().getBytes("utf-8"));
                        outputStream.close();

                        Toast.makeText(ReaderApp.getContext(), "很抱歉,程序出现异常,即将退出.", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(ReaderApp.getContext(), MainActivity.class);
                        PendingIntent restartIntent = PendingIntent.getActivity(ReaderApp.getContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                        //退出程序
                        AlarmManager mgr = (AlarmManager)ReaderApp.getContext().getSystemService(Context.ALARM_SERVICE);
                        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 1000, restartIntent); // 1秒钟后重启应用
                        System.exit(0);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }// end of try
                }

//            	new AlertDialog.Builder(ReaderApp.getAppContext())
//				.setTitle(ReaderApp.getAppContext().getResources().getString(R.string.app_name))
//				.setMessage(ReaderApp.getAppContext().getResources().getString(R.string.error))
//			 	.setPositiveButton(ReaderApp.getAppContext().getResources().getString(R.string.com_btn_ok), new OnClickListener(){
//
//					@Override
//					public void onClick(DialogInterface dialog, int which) {
//						System.exit(0);
//				        Intent intent = new Intent(ReaderApp.getAppContext(), MainActivity.class);
//				        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
//				        startActivity(intent);
//					}
//			 	})
//			 	.show();
            }
        });
    }
}
