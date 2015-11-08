package com.lgq.rssreader.abstraction;

import com.lgq.rssreader.model.Blog;
import com.lgq.rssreader.model.Channel;
import com.lgq.rssreader.model.ImageRecord;
import com.lgq.rssreader.model.Result;
import com.lgq.rssreader.model.RssAction;
import com.lgq.rssreader.model.SyncState;

import java.util.List;

/**
 * Created by redel on 2015-09-04.
 */
public interface RssParser {
//   void loadData(RssCallback<List<Channel>> handler)  throws InterruptedException;
//   void markTag(Blog blog, RssAction action, RssCallback<Boolean> handler);
//   void markTag(Channel displayObj, RssAction action, RssCallback<Boolean> handler);
//   void sync(String userId, List<SyncState> unsyncs, long lastTimeSyncUnread, RssCallback<List<String>> handler);
//   void getRssBlog(Channel channel, Blog blog, int count, RssCallback<List<Blog>> handler);
//   void getFavor(String tag, Blog blog, int count, RssCallback<List<ImageRecord>> handler);
//   void addRss(String rssUrl, String searchResultTitle, RssCallback<Boolean> handler);
//   void assignFolder(Channel folder, Channel single, RssCallback<Boolean> handler);
//   void searchRss(String key, int page, RssCallback<List<Result>> handler);
//   void download(Channel c, int count, RssCallback<List<Blog>> handler);

   List<Channel> loadData() throws InterruptedException;
   boolean markTag(String userId, Blog blog, RssAction action);
   boolean markTag(Channel displayObj, RssAction action);
   List<String> sync(String userId, List<SyncState> unsyncs, long lastTimeSyncUnread);
   List<Blog> getRssBlog(Channel channel, Blog blog, int count);
   List<ImageRecord> getFavor(String tag, Blog blog, int count);
   boolean addRss(String rssUrl, String searchResultTitle);
   boolean assignFolder(Channel folder, Channel single);
   List<Result> searchRss(String key, int page);
}
