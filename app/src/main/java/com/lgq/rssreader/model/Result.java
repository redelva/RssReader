package com.lgq.rssreader.model;

/**
 * Created by redel on 2015-09-12.
 */
public class Result {
    final boolean isSubscribed;
    final String title;
    final String streamId;
    final String subscriptCount;

    public boolean getIsSubscribed(){
        return isSubscribed;
    }

    public String getTitle(){
        return title;
    }

    public String getStreamId(){
        return streamId;
    }
    public String getSubscriptCount(){
        return subscriptCount;
    }

    public Result(boolean isSubscribed, String title, String streamId, String subscriptCount){
        this.isSubscribed = isSubscribed;
        this.streamId = streamId;
        this.subscriptCount = subscriptCount;
        this.title = title;
    }
}
