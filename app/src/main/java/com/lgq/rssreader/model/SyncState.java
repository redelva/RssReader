package com.lgq.rssreader.model;

import java.util.Date;

/**
 * Created by redel on 2015-09-12.
 */
public class SyncState {
    final int syncStateId;
    final String blogOriginId;
    final String channelId;
    final RssAction status;
    final String tag;
    final Date timeStamp;

    public int getSyncStateId(){
        return syncStateId;
    }

    public String getBlogOriginId(){
        return blogOriginId;
    }

    public String getChannelId(){
        return channelId;
    }

    public RssAction getStatus(){
        return status;
    }

    public String getTag(){
        return tag;
    }

    public Date getTimeStamp(){
        return timeStamp;
    }

    public SyncState(int syncStateId, String blogOriginId, String channelId, RssAction status, String tag, Date timeStamp){
        this.syncStateId = syncStateId;
        this.blogOriginId = blogOriginId;
        this.channelId = channelId;
        this.status = status;
        this.tag = tag;
        this.timeStamp = timeStamp;
    }
}
