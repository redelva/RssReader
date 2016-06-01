package com.lgq.rssreader.util;

import android.app.Activity;
import android.content.Intent;

import com.lgq.rssreader.R;
import com.lgq.rssreader.core.Constant;
import com.lgq.rssreader.model.Style;

/**
 * Created by redel on 2016-5-26.
 */
public class ThemeUtil {
    //private static Style currentStyle = PreferencesUtil.getAppSettings().getStyle();

    public static void changeToTheme(Activity activity) {
        activity.setResult(Constant.ThemeChanged);
        activity.finish();
        activity.getIntent().putExtra("themeChanged", true);
        activity.startActivity(activity.getIntent());
        activity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    public static void onActivityCreateSetTheme(Activity activity) {
        Style style = PreferencesUtil.getAppSettings().getStyle();

        //if(style.getTitleBackgroundColor().equals(PreferencesUtil.getStyle(activity).getTitleBackgroundColor())){
        //    return;
        //}

        if(style.getTitleBackgroundColor().equals(Style.Black.getTitleBackgroundColor()))
            activity.setTheme(R.style.BlackTheme);
        if(style.getTitleBackgroundColor().equals(Style.Dark.getTitleBackgroundColor()))
            activity.setTheme(R.style.DarkTheme);
        if(style.getTitleBackgroundColor().equals(Style.Gray.getTitleBackgroundColor()))
            activity.setTheme(R.style.GrayTheme);
        if(style.getTitleBackgroundColor().equals(Style.Green.getTitleBackgroundColor()))
            activity.setTheme(R.style.GreenTheme);
        if(style.getTitleBackgroundColor().equals(Style.White.getTitleBackgroundColor()))
            activity.setTheme(R.style.WhiteTheme);
    }

}
