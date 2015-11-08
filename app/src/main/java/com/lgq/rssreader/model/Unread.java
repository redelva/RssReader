package com.lgq.rssreader.model;

import java.util.List;

/**
 * Created by redel on 2015-09-12.
 */
public class Unread {

    final int max;
    final List<UnreadCount> unreads;

    public Unread(int max, List<UnreadCount> unreads) {
        this.max = max;
        this.unreads = unreads;
    }

    public int getMax() {
        return max;
    }

    public List<UnreadCount> getUnreads() {
        return unreads;
    }
}
