package com.lgq.rssreader.core;

/**
 * Created by redel on 2016-5-26.
 */
public class Constant {
    public static final int ThemeChanged = 10000;
    public static final int ADD_SUBSCRIBE = 10001;
    public static final int BLOG_LIST = 10002;

    public static  final int WRITE_EXTERNAL_STORAGE_REQUEST_CODE = 100001;

    public static final String EMPTY_HTML = "<html>\n" +
            " <head></head>\n" +
            " <body>\n" +
            "  <p> </p> \n" +
            "  <p><span><br></span></p> \n" +
            "  <p> </p>\n" +
            " </body>\n" +
            "</html>";

    public static final String FONTS_LOCATION = "/Android/data/com.lgq.rssreader/fonts/";
    public static final String IMAGES_LOCATION = "/Android/data/com.lgq.rssreader/images/";

    public static final String UNCATEGORY = "Root";
}
