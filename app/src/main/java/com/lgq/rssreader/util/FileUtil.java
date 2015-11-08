package com.lgq.rssreader.util;

import java.io.File;

/**
 * Created by redel on 2015-10-09.
 */
public class FileUtil {
    public static long getFileLength(String filePath){
        File file=new File(filePath);
        return file.length();
    }
}
