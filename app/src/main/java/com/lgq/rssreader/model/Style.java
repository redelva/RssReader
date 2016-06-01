package com.lgq.rssreader.model;

import java.io.Serializable;

/**
 * Created by redel on 2016-04-10.
 */
public class Style implements Serializable{
    private String webViewBackgroundColor;
    private String webViewFontColor;
    private String titleFontColor;
    private String titleBackgroundColor;
    private String name;

    public String getWebViewBackgroundColor() {
        return webViewBackgroundColor;
    }

    public void setWebViewBackgroundColor(String webViewBackgroundColor) {
        this.webViewBackgroundColor = webViewBackgroundColor;
    }

    public String getWebViewFontColor() {
        return webViewFontColor;
    }

    public void setWebViewFontColor(String webViewFontColor) {
        this.webViewFontColor = webViewFontColor;
    }

    public String getTitleFontColor() {
        return titleFontColor;
    }

    public void setTitleFontColor(String titleFontColor) {
        this.titleFontColor = titleFontColor;
    }

    public String getTitleBackgroundColor() {
        return titleBackgroundColor;
    }

    public void setTitleBackgroundColor(String titleBackgroundColor) {
        this.titleBackgroundColor = titleBackgroundColor;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static Style White = new Style(){{
        setWebViewBackgroundColor("#ffffff");
        setWebViewFontColor("#000000");
        setTitleFontColor("#ffffff");
        setTitleBackgroundColor("#056F00");
        setName("White");
    }};
    public static Style Black = new Style(){{
        setWebViewBackgroundColor("#000000");
        setWebViewFontColor("#ffffff");
        setTitleFontColor("#ffffff");
        setTitleBackgroundColor("#000000");
        setName("Black");
    }};
    public static Style Dark = new Style(){{
        setWebViewBackgroundColor("#1F1F1F");
        setWebViewFontColor("#585858");
        setTitleFontColor("#585858");
        setTitleBackgroundColor("#1F1F1F");
        setName("Dark");
    }};
    public static Style Gray = new Style(){{
        setWebViewBackgroundColor("#E1C7A6");
        setWebViewFontColor("#283448");
        setTitleFontColor("#283448");
        setTitleBackgroundColor("#E1C7A6");
        setName("Gray");
    }};
    public static Style Green = new Style(){{
        setWebViewBackgroundColor("#C8EDCC");
        setWebViewFontColor("#323B33");
        setTitleFontColor("#323B33");
        setTitleBackgroundColor("#C8EDCC");
        setName("Green");
    }};
}
