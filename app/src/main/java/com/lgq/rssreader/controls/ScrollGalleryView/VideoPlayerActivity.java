package com.lgq.rssreader.controls.scrollgalleryview;

import android.app.Activity;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.MediaController;
import android.widget.VideoView;

import com.lgq.rssreader.R;

/**
 * {describe the class purpose here}
 */
public class VideoPlayerActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String url = getIntent().getExtras().getString(Constants.URL);
        setContentView(R.layout.video_fragment);
        final VideoView videoView = (VideoView) findViewById(R.id.videoView);
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                View progress = findViewById(R.id.videoProgress);
                progress.setVisibility(View.GONE);

                videoView.requestFocus();
                MediaController vidControl = new MediaController(VideoPlayerActivity.this);
                vidControl.setAnchorView(videoView);
                videoView.setMediaController(vidControl);
                videoView.start();
            }
        });
        videoView.setVideoURI(Uri.parse(url));
        videoView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }
}
