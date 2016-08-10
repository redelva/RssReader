package com.lgq.rssreader;

import android.app.SearchManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.lgq.rssreader.fragment.SearchListFragment;
import com.lgq.rssreader.fragment.SubscribeListFragment;
import com.lgq.rssreader.util.ThemeUtil;

public class SubscribeActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemeUtil.onActivityCreateSetTheme(this);
        setContentView(R.layout.activity_subscribe);
        TextView queryView = (TextView) findViewById(R.id.result_query);
        queryView.setText(getIntent().getCharSequenceExtra(SubscribeListFragment.SUBSCRIBE_QUERY));
        SubscribeListFragment fragment = SubscribeListFragment.newInstance();
        getSupportFragmentManager().beginTransaction().replace(R.id.subscribe_container, fragment).commit();
    }
}