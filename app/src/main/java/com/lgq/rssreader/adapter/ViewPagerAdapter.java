package com.lgq.rssreader.adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.lgq.rssreader.fragment.TitleFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * 主页面VierPager适配器
 * Created by Lichenwei
 * Date: 2015-08-16
 * Time: 13:47
 */
public class ViewPagerAdapter extends FragmentPagerAdapter {

    private List<Fragment> mFragments;
    private List<String> mTitles;

    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
        mFragments = new ArrayList<Fragment>();
        mTitles = new ArrayList<String>();
    }

    /**
     * 新添Fragment内容和标题
     * @param fragment
     * @param title
     */
    public void addFragment(Fragment fragment, String title) {
        mFragments.add(fragment);
        Bundle args = new Bundle();
        args.putString(TitleFragment.EXTRA_TITLE, title);
        fragment.setArguments(args);
        mTitles.add(title);
    }

    @Override
    public Fragment getItem(int position) {
        return mFragments.get(position);
    }

    @Override
    public int getCount() {
        return mFragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mTitles.get(position);
    }
}