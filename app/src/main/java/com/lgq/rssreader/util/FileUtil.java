package com.lgq.rssreader.util;

import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;

import com.lgq.rssreader.core.Config;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by redel on 2015-10-09.
 */
public class FileUtil {
    public static long getFileLength(String filePath){
        File file=new File(filePath);
        return file.length();
    }

    public static List<File> detectFonts(){
        List<File> fonts = new ArrayList<File>();

        String sDStateString = android.os.Environment.getExternalStorageState();

        if (sDStateString.equals(android.os.Environment.MEDIA_MOUNTED)) {
            try {
                File SDFile = android.os.Environment.getExternalStorageDirectory();

                File dir = new File(SDFile.getAbsolutePath() + Config.FONTS_LOCATION);

                if(dir.exists())
                    return Arrays.asList(dir.listFiles());
            }
            catch(Exception e){
                Log.e("RssReader", "Error at detect fonts " + e.getMessage());
            }
        }

        return fonts;
    }
}
