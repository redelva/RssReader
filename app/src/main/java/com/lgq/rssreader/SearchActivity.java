package com.lgq.rssreader;

import android.app.SearchManager;
import android.os.Bundle;
import android.app.Activity;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.lgq.rssreader.fragment.SearchListFragment;

public class SearchActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        TextView queryView = (TextView) findViewById(R.id.channel_title);
        queryView.setText(getIntent().getStringExtra(SearchManager.QUERY));
        SearchListFragment searchListFragment = SearchListFragment.newInstance();
        getSupportFragmentManager().beginTransaction().replace(R.id.search_container, searchListFragment).commit();
    }

}
