package com.lgq.rssreader.fragment;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;

import com.lgq.rssreader.core.ReaderApp;
import com.squareup.leakcanary.RefWatcher;

/**
 * Created by redel on 2015-10-09.
 */
public abstract class BaseFragment extends Fragment {

    @Override public void onDestroy() {
        super.onDestroy();
        RefWatcher refWatcher = ReaderApp.getRefWatcher(getActivity());
        refWatcher.watch(this);
        //mCallback = null;
    }

    public String getToken(){
        return "OAuth A6zxdjics3EkNdd-kHY73TcP7izy30kSk31eXYr85rSuT7oSUKhPeM1PupGlDko1fTwvkMcNc0fALQ-YI6KNoWFH-Zkdreqsd6e9Hb4JyV9nj7XROm9nh7lALqbIeEqkxSYDkfFWTttZCxnjLRObTIMA0hNfVoIdklhnv6BP5ZB_zhuaKZ2rs6a6xzIMkVT9C9nh9JCf2NCnLFdDYOKGUlo:feedly";
    }

//    private FragmentCallback mCallback;
//
//    @Override
//    public void onAttach(Context content) {
//        super.onAttach(content);
//        try {
//            mCallback = (FragmentCallback) content;
//        } catch (ClassCastException e) {
//            throw new ClassCastException(content.toString() + " must implement OnFragmentListener");
//        }
//    }
}