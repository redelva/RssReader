package com.lgq.rssreader.model;

import java.util.Date;

/**
 * Created by redel on 2015-09-12.
 */
public class UnreadCount {
    final String id ;
    final int count ;
    final Date newestItemStamp ;

    public UnreadCount(String id, int count, Date newestItemStamp) {
        this.id = id;
        this.count = count;
        this.newestItemStamp = newestItemStamp;
    }

    public String getId() {
        return id;
    }

    public int getCount() {
        return count;
    }

    public Date getNewestItemStamp() {
        return newestItemStamp;
    }
}
