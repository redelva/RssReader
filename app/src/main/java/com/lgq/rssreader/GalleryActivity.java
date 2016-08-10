package com.lgq.rssreader;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.lgq.rssreader.controls.PicassoImageLoader;
import com.lgq.rssreader.controls.SuperImageLoader;
import com.lgq.rssreader.controls.scrollgalleryview.MediaInfo;
import com.lgq.rssreader.controls.scrollgalleryview.ScrollGalleryView;
import com.lgq.rssreader.controls.scrollgalleryview.loader.DefaultVideoLoader;
import com.lgq.rssreader.task.SaveMediaTask;
import com.lgq.rssreader.util.CollectionUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class GalleryActivity extends AppCompatActivity{
    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = true;

    private static ExecutorService FULL_TASK_EXECUTOR;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    static {
        FULL_TASK_EXECUTOR = Executors.newCachedThreadPool();
    }

    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private static final int UI_ANIMATION_DELAY = 300;
    private final Handler mHideHandler = new Handler();
    private ScrollGalleryView mContentView;
    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            // Delayed removal of status and navigation bar

            // Note that some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.
            mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };
    private View mControlsView;
    private ImageView mImageView;
    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
            ActionBar actionBar = getActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
            mControlsView.setVisibility(View.VISIBLE);
        }
    };
    private boolean mVisible;
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };
    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    private final View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
            }
            return false;
        }
    };

//    private static final ArrayList<String> images = new ArrayList<>(Arrays.asList(
//            "http://img1.goodfon.ru/original/1920x1080/d/f5/aircraft-jet-su-47-berkut.jpg",
//            "http://www.dishmodels.ru/picture/glr/13/13312/g13312_7657277.jpg",
//            "http://img2.goodfon.ru/original/1920x1080/b/c9/su-47-berkut-c-37-firkin.jpg"
//    ));
//    private static final String movieUrl = "http://www.sample-videos.com/video/mp4/720/big_buck_bunny_720p_1mb.mp4";

    private List<String> images;
    private List<String> videos;

    @Override
    protected void onResume() {
        super.onResume();
        if (AUTO_HIDE) {
            delayedHide(0);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_gallery);

        mVisible = true;
        mControlsView = findViewById(R.id.fullscreen_content_controls);
        mContentView = (ScrollGalleryView)findViewById(R.id.fullscreen_content);
        mImageView = (ImageView) findViewById(R.id.image_download);

        if(getIntent().getStringArrayExtra("images") != null)
            images = CollectionUtil.Array2List(getIntent().getStringArrayExtra("images"));
        else
            images = new ArrayList<>();
        if(getIntent().getStringArrayExtra("videos") != null)
            videos = CollectionUtil.Array2List(getIntent().getStringArrayExtra("videos"));
        else
            videos = new ArrayList<>();

        int image_index = getIntent().getIntExtra("image_index", 0);

        List<MediaInfo> infos = new ArrayList<>();
        for (String url : images)
            infos.add(MediaInfo.mediaLoader(new SuperImageLoader(url)));

        for(int i=0; i<videos.size();i=i+2)
            if(videos.get(i+1).equals("null"))
                infos.add(MediaInfo.mediaLoader(new DefaultVideoLoader(videos.get(i),R.drawable.default_video)));
            else
                infos.add(MediaInfo.mediaLoader(new DefaultVideoLoader(videos.get(i),videos.get(i+1), R.drawable.default_video)));

        mContentView = (ScrollGalleryView) findViewById(R.id.fullscreen_content);
        mContentView
                .setThumbnailSize(100)
                .setZoom(true)
                .setFragmentManager(getSupportFragmentManager())
//                .addMedia(MediaInfo.mediaLoader(new DefaultImageLoader(R.drawable.wallpaper1)))
//                .addMedia(MediaInfo.mediaLoader(new DefaultImageLoader(toBitmap(R.drawable.wallpaper7))))
//                .addMedia(MediaInfo.mediaLoader(new MediaLoader() {
//                    @Override public boolean isImage() {
//                        return true;
//                    }
//
//                    @Override public void loadMedia(Context context, ImageView imageView,
//                                                    MediaLoader.SuccessCallback callback) {
//                        imageView.setImageBitmap(toBitmap(R.drawable.wallpaper3));
//                        callback.onSuccess();
//                    }
//
//                    @Override public void loadThumbnail(Context context, ImageView thumbnailView,
//                                                        MediaLoader.SuccessCallback callback) {
//                        thumbnailView.setImageBitmap(toBitmap(R.drawable.wallpaper3));
//                        callback.onSuccess();
//                    }
//                }))
                //.addMedia(MediaInfo.mediaLoader(new DefaultVideoLoader(movieUrl, R.mipmap.default_video)))
                .addMedia(infos);

        // Set up the user interaction to manually show or hide the system UI.
        mContentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggle();
            }
        });

        mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                requestPermission();

                String url = images.get(mContentView.getCurrentIndex());

                new SaveMediaTask(GalleryActivity.this).executeOnExecutor(FULL_TASK_EXECUTOR,url);
            }
        });

        mContentView.setCurrentItem(image_index);

        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.
        //findViewById(R.id.dummy_button).setOnTouchListener(mDelayHideTouchListener);
    }

    public void requestPermission(){
        //判断当前Activity是否已经获得了该权限
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            //如果App的权限申请曾经被用户拒绝过，就需要在这里跟用户做出解释
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                Toast.makeText(this,"please give me the permission",Toast.LENGTH_SHORT).show();
            } else {
                //进行权限请求
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        10);
            }

            return;
        }
    }

    private void doNext(int requestCode, int[] grantResults) {
        if (requestCode == 10) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission Granted
            } else {
                // Permission Denied
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(final int requestCode, @NonNull final String[] permissions, @NonNull final int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 10) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted.
            } else {
                // User refused to grant permission.
                Toast.makeText(this, "Cant not save the picture to system gallery", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private Bitmap toBitmap(int image) {
        return ((BitmapDrawable) getResources().getDrawable(image)).getBitmap();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100);
    }

    private void toggle() {
        if (mVisible) {
            hide();
        } else {
            show();
        }
    }

    private void hide() {
        // Hide UI first
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mControlsView.setVisibility(View.GONE);
        mVisible = false;

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    @SuppressLint("InlinedApi")
    private void show() {
        // Show the system bar
        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        mVisible = true;

        // Schedule a runnable to display UI elements after a delay
        mHideHandler.removeCallbacks(mHidePart2Runnable);
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
    }

    /**
     * Schedules a call to hide() in [delay] milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }
}