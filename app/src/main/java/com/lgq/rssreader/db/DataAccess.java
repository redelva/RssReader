//package com.lgq.rssreader.db;
//
//import android.os.Looper;
//import android.preference.PreferenceActivity;
//import android.util.Log;
//import android.widget.Toast;
//
//import com.lgq.rssreader.abstraction.AsyncFeedlyParser;
//import com.lgq.rssreader.abstraction.RssHandler;
//import com.lgq.rssreader.abstraction.RssParser;
//import com.lgq.rssreader.model.Channel;
//import com.lgq.rssreader.model.Subscription;
//import com.lgq.rssreader.model.Tag;
//import com.loopj.android.http.*;
//
//import org.apache.http.Header;
//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;
//
//
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.List;
//import java.util.concurrent.Callable;
//import java.util.concurrent.ExecutionException;
//import java.util.concurrent.Future;
//import java.util.concurrent.FutureTask;
//
///**
// * Created by redel on 2015-09-06.
// */
//public class DataAccess {
//    private final static String SUBSCRIPTIONS_URL = "http://feedly.com/v3/subscriptions?ck=%s&ct=feedly.desktop&cv=29.0.1014";
//
////    public static List<Subscription> LoadSubscriptions() throws InterruptedException, ExecutionException{
////        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
////
////        Future<List<Subscription>> f = asyncHttpClient.prepareGet(String.format(SUBSCRIPTIONS_URL, System.currentTimeMillis())).execute(
////            new AsyncCompletionHandler<List<Subscription>>() {
////
////                @Override
////                public List<Subscription> onCompleted(Response response) throws Exception {
////                    try {
////
////                        JSONArray items = new JSONArray(response.getResponseBody());
////
////                        List<Subscription> subs = new ArrayList<Subscription>();
////
////                        for (int i = 0; i < items.length(); i++) {
////                            JSONObject subscription = items.getJSONObject(i);
////
////                            List<Tag> tags = new ArrayList<Tag>();
////                            if (subscription.has("categories") && subscription.getJSONArray("categories").length() > 0) {
////                                JSONArray categories = subscription.getJSONArray("categories");
////                                for (int j = 0; j < categories.length(); j++) {
////                                    JSONObject category = categories.getJSONObject(j);
////                                    Tag t = new Tag(
////                                            category.getString("id"),
////                                            category.getString("label"),
////                                            ""
////                                    );
////
////                                    tags.add(t);
////                                }
////                            }
////
////                            Date firstItemTime;
////
////                            if (subscription.has("updated"))
////                                firstItemTime = new Date(subscription.getLong("updated"));
////                            else
////                                firstItemTime = new Date();
////
////                            Subscription s = new Subscription(
////                                    subscription.getString("id"),
////                                    subscription.getString("title"),
////                                    tags,
////                                    "",
////                                    firstItemTime
////                            );
////
////                            subs.add(s);
////                        }
////
////                        return subs;
////                    } catch (JSONException e) {
////
////                        e.printStackTrace();
////
////                        Log.i("RssReader", "Error Happen Subscriptions" + e.getMessage());
////                    }
////
////                    return null;
////            }
////
////                @Override
////                public void onThrowable(Throwable t){
////                    // Something wrong happened.
////                }
////            });
////
////        return f.get();
////    }
//
//    interface DataCallback {
//        void callback(List<Subscription> subscriptionList);
//    }
//
//    public static class SubscriptionCallback implements DataCallback{
//
//        @Override
//        public void callback(List<Subscription> subscriptionList) {
//
//        }
//    }
//
////    public static void LoadChannelInfo(final SubscriptionCallback callback){
////        AsyncHttpClient client = new AsyncHttpClient();
////        client.addHeader("Host", "cloud.feedly.com");
////        client.addHeader("Accept-Charset", "utf8");
////        client.addHeader("Referer", "http://cloud.feedly.com/");
////        client.addHeader("X-Feedly-Access-Token", "AkPt6gl7ImEiOiJmZWVkbHkiLCJlIjoxNDQyMTQ5NDE1MzUwLCJpIjoiZDFlZjM5MzgtYTU0Yi00NDA0LTlkOWQtZjk3ODQyMTI0MjgxIiwicCI6NiwidCI6MSwidiI6InByb2R1Y3Rpb24iLCJ3IjoiMjAxMy4xMSIsIngiOiJzdGFuZGFyZCJ9:feedly");
////
////        client.get(String.format(SUBSCRIPTIONS_URL, System.currentTimeMillis()), new JsonHttpResponseHandler() {
////            @Override
////            public void onSuccess(int statusCode, Header[] heaeders, final JSONArray items) {
////                try {
////
////                    List<Subscription> subs = new ArrayList<Subscription>();
////
////                    for (int i = 0; i < items.length(); i++) {
////                        JSONObject subscription = items.getJSONObject(i);
////
////                        List<Tag> tags = new ArrayList<Tag>();
//////                        if (subscription.has("categories") && subscription.getJSONArray("categories").length() > 0) {
//////                            JSONArray categories = subscription.getJSONArray("categories");
//////                            for (int j = 0; j < categories.length(); j++) {
//////                                JSONObject category = categories.getJSONObject(j);
//////                                Tag t = new Tag(
//////                                    category.getString("id"),
//////                                    category.getString("label"),
//////                                    ""
//////                                );
//////
//////                                tags.add(t);
//////                            }
//////                        }
////
////                        Date firstItemTime;
////
////                        if (subscription.has("updated"))
////                            firstItemTime = new Date(subscription.getLong("updated"));
////                        else
////                            firstItemTime = new Date();
////
////                        Subscription s = new Subscription(
////                                subscription.getString("id"),
////                                subscription.getString("title"),
////                                tags,
////                                "",
////                                firstItemTime
////                        );
////
////                        subs.add(s);
////                    }
////
////                    callback.callback(subs);
////
////                    Log.i("RssReader", "Finish Subscriptions");
////                } catch (JSONException e) {
////
////                    e.printStackTrace();
////
////                    Log.i("RssReader", "Error Happen Subscriptions" + e.getMessage());
////                }
////            }
////        });
////    }
//}
