package com.lgq.rssreader.util;

import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {

    public static final String DEFAULT_PATTERN="yyyy-MM-dd HH:mm:ss";

    public static String convertDateToString(Date aDate) {
        if (aDate == null)
            return null;
        String pattern = DEFAULT_PATTERN;
        SimpleDateFormat df = new SimpleDateFormat(pattern);
        return df.format(aDate);
    }


    public static String getCurrentDate() {
        String pattern = "yyyyMMddHHmmss";
        SimpleDateFormat df = new SimpleDateFormat(pattern);
        return df.format(new Date());
    }


    public static String convertDateToString(Date aDate,String pattern) {
        if (aDate == null)
            return null;
        SimpleDateFormat df = new SimpleDateFormat(pattern);
        return df.format(aDate);
    }

    public static Date convertStringToDate(String strDate) {
        if (strDate==null)
            return null;
        String pattern = DEFAULT_PATTERN;
        SimpleDateFormat df = new SimpleDateFormat(pattern);
        try {
            return df.parse(strDate);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getInterval(Date createTime, String pattern){
        return getInterval(convertDateToString(createTime), pattern);
    }

    public static String getInterval(String createtime,String pattern) { //传入的时间格式必须类似于2012-8-21 17:53:20这样的格式
        String interval = null;
        SimpleDateFormat sd = new SimpleDateFormat(DEFAULT_PATTERN);
        ParsePosition pos = new ParsePosition(0);
        Date d1 = (Date) sd.parse(createtime, pos);
        if(d1==null){
            d1 = new Date();
        }
        // 得出的时间间隔是毫秒
        long time = new Date().getTime() - d1.getTime();

        if(time/1000 < 10 && time/1000 >= 0) {
            //如果时间间隔小于10秒则显示“刚刚”time/10得出的时间间隔的单位是秒
            interval ="刚刚";

        }else if(time/1000 < 60 && time/1000 > 0) {
            //如果时间间隔小于60秒则显示多少秒前
            int se = (int) ((time%60000)/1000);
            interval = se + "秒前";

        }else if(time/60000 < 60 && time/60000 > 0) {
            //如果时间间隔小于60分钟则显示多少分钟前
            int m = (int) ((time%3600000)/60000);
            interval = m + "分钟前";

        }
        else if(time/3600000 < 24 && time/3600000 > 0) {
        //如果时间间隔小于24小时则显示多少小时前
            int h = (int) (time/3600000);
            interval = h + "小时前";

        }else if(time/(3600000*24) <= 30 && time/(3600000*24) > 0) {
        //如果时间间隔在0-30天则显示多少天前
            int h = (int) (time/(3600000*24));
            interval = h + "天前";

        }else if(time/3600000/24/30 < 12 && time/3600000/24/30 > 0) {
        //如果时间间隔在0-12月之间则显示多少月前
            int h = (int) (time/3600000/24/30);
            interval = h + "月前";

        }else if(time/(3600000*24)  >= 365) {
        //如果时间间隔大于365天则显示多少年前
            int h = (int) (time/3600000/24/365);
            interval = h + "年前";

        }
        else {
            //大于24小时，则显示正常的时间，但是不显示秒
            if(pattern != null && pattern.length()>0){
                pattern = DEFAULT_PATTERN;
            }
            SimpleDateFormat sdf = new SimpleDateFormat(pattern);
            interval = sdf.format(d1);
        }
        return interval;
    }


    public static String getIntervalDate(String createtime,String pattern) { //传入的时间格式必须类似于2012-8-21 17:53:20这样的格式
        String interval = null;
        SimpleDateFormat sd = new SimpleDateFormat(DEFAULT_PATTERN);
        ParsePosition pos = new ParsePosition(0);
        Date d1 = (Date) sd.parse(createtime, pos);
        // 得出的时间间隔是毫秒
        long time = new Date().getTime() - d1.getTime();

        if(time/1000 < 10 && time/1000 >= 0) {
            //如果时间间隔小于10秒则显示“刚刚”time/10得出的时间间隔的单位是秒
            interval ="刚刚";

        }else if(time/1000 < 60 && time/1000 > 0) {
            //如果时间间隔小于60秒则显示多少秒前
            int se = (int) ((time%60000)/1000);
            interval = se + "秒前";

        }else if(time/60000 < 60 && time/60000 > 0) {
            //如果时间间隔小于60分钟则显示多少分钟前
            int m = (int) ((time%3600000)/60000);
            interval = m + "分钟前";

        }
        else if(time/3600000 < 24 && time/3600000 > 0) {
            //如果时间间隔小于24小时则显示多少小时前
            int h = (int) (time/3600000);
            interval = h + "小时前";

        }else if(time/(3600000*24) <= 30 && time/(3600000*24) > 0) {
            //如果时间间隔在0-30天则显示多少天前
            int h = (int) (time/(3600000*24));
            interval = h + "天前";

        }else if(time/3600000/24/30 < 12 && time/3600000/24/30 > 0) {
            //如果时间间隔在0-12月之间则显示多少月前
            int h = (int) (time/3600000/24/30);
            interval = h + "月前";

        }else if(time/(3600000*24)  >= 365) {
            //如果时间间隔大于365天则显示多少年前
            int h = (int) (time/3600000/24/365);
            interval = h + "年前";

        }
        else {
            //大于24小时，则显示正常的时间，但是不显示秒
            if(pattern != null && pattern.length()>0){
                pattern = DEFAULT_PATTERN;
            }
            SimpleDateFormat sdf = new SimpleDateFormat(pattern);
            interval = sdf.format(d1);
        }
        return interval;
    }
}

