package com.lgq.rssreader.model;

/**
 * Created by redel on 2015-09-12.
 */
public class Result {
    boolean isSubscribed;
    String title;
    String feedId;
    String avatar;
    String subscriptCount;
    String description;

    public boolean isSubscribed() {
        return isSubscribed;
    }

    public void setSubscribed(boolean subscribed) {
        isSubscribed = subscribed;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String desc) {
        this.description = desc;
    }

    public String getFeedId() {
        return feedId;
    }

    public void setFeedId(String feedId) {
        this.feedId = feedId;
    }

    public String getSubscriptCount() {
        return subscriptCount;
    }

    public void setSubscriptCount(String subscriptCount) {
        this.subscriptCount = subscriptCount;
    }

    public Result(boolean isSubscribed, String title, String feedId, String subscriptCount, String description, String avatar){
        this.isSubscribed = isSubscribed;
        this.feedId = feedId;
        this.avatar = avatar;
        this.subscriptCount = subscriptCount;
        this.title = title;
        this.description = description;
    }

    public Result(){

    }
}
