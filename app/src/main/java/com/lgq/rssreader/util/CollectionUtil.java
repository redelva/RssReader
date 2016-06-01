package com.lgq.rssreader.util;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by redel on 2016-03-28.
 */
public class CollectionUtil {

    public static String[] List2Array(List<String> source){
        String[] to = new String[source.size()];
        for (int idx=0; idx<to.length;idx++) {
            to[idx] =  source.get(idx);
        }

        return to;
    }

    public static List<String> Array2List(String[] source){
        List<String> to = new ArrayList<>();

        for(String s : source)
            to.add(s);

        return to;
    }
}
