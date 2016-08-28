package com.lgq.rssreader.abstraction;

import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Looper;
import android.support.v4.util.Pair;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;

import com.lgq.rssreader.core.ReaderApp;
import com.lgq.rssreader.model.Blog;
import com.lgq.rssreader.model.Channel;
import com.lgq.rssreader.model.ImageRecord;
import com.lgq.rssreader.model.Profile;
import com.lgq.rssreader.model.Result;
import com.lgq.rssreader.model.RssAction;
import com.lgq.rssreader.model.Subscription;
import com.lgq.rssreader.model.SyncState;
import com.lgq.rssreader.model.Tag;
import com.lgq.rssreader.model.Unread;
import com.lgq.rssreader.model.UnreadCount;

import org.apache.http.entity.StringEntity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by redel on 2015-09-04.
 */
public class FeedlyParser implements RssParser {

    //Const Title
    private static final String RECOMMENDTITLE = "pop/topic/top/language/";
    public static final String READLISTTITLE = "user/-/state/com.google/reading-list";
    public static final String STARREDTTITLE = "user/-/state/com.google/starred";

    //Feedly Urls and Login Auth
    private static final String AUTH_PARAMS = "accountType=HOSTED_OR_GOOGLE&Email={0}&Passwd={1}&service=reader&source=mobilescroll";
    private static final String FEEDLY_AUTH_PARAMS = "code={0}&client_id=feedly&client_secret=0XP4XQ07VVMDWBKUHTJM4WUQ&redirect_uri=http%3A%2F%2Fdev.feedly.com%2Ffeedly.html&grant_type=authorization_code";
    private static final String LOGINURL = "https://www.google.com/accounts/ClientLogin";
    public static final String FEEDLYLOGINURL = "http://cloud.feedly.com/v3/auth/token";

    private static final String SUBSCRIPTIONURL = "http://cloud.feedly.com/v3/subscriptions?ct=feedly.desktop";
    private static final String PROFILEURL = "http://cloud.feedly.com/v3/profile";
    private static final String TAGURL = "http://cloud.feedly.com/v3/tags?ct=feedly.desktop";
    private static final String UNREADURL = "http://cloud.feedly.com/v3/markers/counts?ct=feedly.desktop";
    private static final String EDITTAGURL = "http://cloud.feedly.com/v3/markers?ck=1371868985337&ct=feedly.desktop";

    private static final String EDITSUBSCRIPTIONURL = "https://cloud.feedly.com/reader/api/0/subscription/edit?client=scroll";
    private static final String RENAMETAGURL = "https://cloud.feedly.com/reader/api/0/rename-tag?client=scroll";
    private static final String MARKALLASREADURL = "https://cloud.feedly.com/reader/api/0/mark-all-as-read?client=scroll";
    private static final String DISABLETAGURL = "https://cloud.feedly.com/reader/api/0/disable-tag?client=scroll";
    private static final String ADDSUBSURL = "https://cloud.feedly.com/reader/api/0/subscription/quickadd";
    private static final String ADDSEARCHRESULTURL = "https://cloud.feedly.com/reader/api/0/subscription/edit?source=FEED_FINDER_SEARCH_RESULT&client=scroll";
    private static final String SEARCHSUBSURL = "https://cloud.feedly.com/reader/directory/search?q={0}&ck={1}&client=scroll&start={2}";
    private static final String SYNCUNREADURL = "https://cloud.feedly.com/reader/atom/user/-/state/com.google/read?n=1000";
    private static final String SORTLISTURL = "https://cloud.feedly.com/reader/api/0/preference/stream/list?output=json";

    // 获取img标签正则
    private static final String IMGURL_REG = "<img.*src=(.*?)[^>]*?>";
    // 获取src路径的正则
    private static final String IMGSRC_REG = "http:\"?(.*?)(\"|>|\\s+)";

    private static SharedPreferences saver = ReaderApp.getContext().getSharedPreferences("RssReader", 0);

    public FeedlyParser(String token){
        RssHttpClient.setToken(token);
        //RssHttpClient.setToken("OAuth A6VnFeuf2cKeArdsTbkdz--MrJo7G8GIrsEWfVKUlP9FGD_wq34TwMOJURH3xhcinPG0ezDUWHQgxmlZhXDr-nXsJp_A4qCoYIQoOVtHayBFmDn0EguFGGu9pWMC_4hPKdhcunZUfQqS01LxLqdM-Mp--iTWydAGVmXyqgd1ToW-YXuJTd8dEV2SPBbzjhpaUypEljyK864Fj7vFy0ael55-Bw:feedly");
    }

    public List<Channel> loadData() throws InterruptedException{
        Pair<List<Subscription>,List<Tag>> pair = getSubsciptions();

        List<Subscription> subscriptions = pair.first;
        List<Tag> tags = pair.second;
        List<String> sorts = getSortList();
        Unread unread = getUnreadCount();

        List<Channel> channels = getChannels(subscriptions, tags, sorts, unread);

        return channels;
    }

    private List<Channel> getChannels(List<Subscription> subscriptions, List<Tag> tags, List<String> sorts, Unread unread){
        List<Channel> channels = null;

        if (tags != null && unread != null && subscriptions != null && sorts != null) {
            channels = new ArrayList<Channel>();

            List<Channel> obj = new ArrayList<Channel>();
            for(Tag t: tags){
                Channel c = new Channel();
                c.setChannelId(t.getId());
                c.setTitle(t.getLabel());
                c.setSortId(t.getSortId());
                c.setIsDirectory(true);
                c.setChildren(new ArrayList<Channel>());

                UnreadCount uc = null;
                for (UnreadCount count:  unread.getUnreads()) {
                    if(count.getId() == t.getId()) {
                        uc = count;
                        break;
                    }
                }

                if(uc != null){
                    c.setLastUpdateTime(uc.getNewestItemStamp());
                    c.setUnreadCount(uc.getCount());
                }else{
                    c.setLastUpdateTime(new Date());
                    c.setUnreadCount(0);
                }

                obj.add(c);
            }

            for(Subscription s : subscriptions)
            {
                Channel c = new Channel();
                c.setChannelId(s.getId());
                c.setTitle(s.getTitle());
                c.setSortId(s.getSortId());
                c.setIsDirectory(false);
                c.setChildren(new ArrayList<Channel>());
                c.setHasParent(false);

                UnreadCount uc = null;
                for (UnreadCount count:  unread.getUnreads()) {
                    if(count.getId().equals(s.getId())) {
                        uc = count;
                        break;
                    }
                }

                if(uc != null){
                    c.setLastUpdateTime(uc.getNewestItemStamp());
                    c.setUnreadCount(uc.getCount());
                }else{
                    c.setLastUpdateTime(new Date());
                    c.setUnreadCount(0);
                }

                if (s.getCategories().size() != 0) {
                    c.setIsDirectory(false);
                    for(Tag t : s.getCategories()) {
                        Channel d = null;
                        for (Channel child:  obj) {
                            if(child.getChannelId().equals(t.getId())) {
                                d = child;
                                break;
                            }
                        }
                        if (d != null) {
                            c.setTagId(d.getChannelId());
                            c.setHasParent(true);
                            d.getChildren().add(c);
                        }
                    }
                }
                else
                {
                    obj.add(c);
                }
            }

            Integer i = 0;
            for(Channel displayObj : obj)
            {
                displayObj.setFolder(i.toString());
                displayObj.setSortId(i.toString());
                i++;
                if (displayObj.getChildren() != null)
                {
                    for(Channel display : displayObj.getChildren())
                    {
                        displayObj.setFolder(i.toString());
                        displayObj.setSortId(i.toString());
                        i++;
                    }
                }
            }

            //new add rss feeds will not dispear in sortlist
            ArrayList<Channel> newFeeds = new ArrayList<Channel>();
            for(Channel c : obj){
                if(!sorts.contains(c.getTitle()))
                    newFeeds.add(c);
            }

            for(String sortId : sorts)
            {
                for(Channel c : obj){
                    if (c.getTitle().equals(sortId)){
                        channels.add(c);
                        break;
                    }
                }
            }

            channels.addAll(newFeeds);
        }
        return channels;
    }

    private Pair<List<Subscription>,List<Tag>> getSubsciptions(){
        String content = RssHttpClient.get(SUBSCRIPTIONURL + "&ck=" + System.currentTimeMillis());

        try {
            JSONArray items = new JSONArray(content);

            List<Tag> tags = new ArrayList<Tag>();
            for (int i = 0; i < items.length(); i++) {
                if (items.getJSONObject(i).has("categories")) {
                    JSONArray categories = items.getJSONObject(i).getJSONArray("categories");

                    JSONObject node = null;
                    for (int j = 0; j < categories.length(); j++) {
                        if (categories.getJSONObject(j).get("id") != null) {
                            node = categories.getJSONObject(j);

                            Tag t = new Tag(node.getString("id"), node.getString("label"), "");

                            if (!tags.contains(t))
                                tags.add(t);
                        }
                    }
                }
            }

            Log.i("RssReader", "Finish tags");

            List<Subscription> subs = new ArrayList<Subscription>();

            for (int i = 0; i < items.length(); i++) {
                JSONObject subscription = items.getJSONObject(i);
                List<Tag> cates = new ArrayList<>();
                if (subscription.has("categories") && subscription.getJSONArray("categories").length() > 0) {
                    JSONArray categories = subscription.getJSONArray("categories");
                    for (int j = 0; j < categories.length(); j++) {
                        JSONObject category = categories.getJSONObject(j);
                        Tag t = new Tag(category.getString("id"), category.getString("label"), "");
                        cates.add(t);
                    }
                }

                Date firstItemMSEC;
                if (subscription.has("updated"))
                    firstItemMSEC = new Date(subscription.getLong("updated"));
                else
                    firstItemMSEC = new Date();

                Subscription s = new Subscription(
                        subscription.getString("id"),
                        subscription.getString("title"),
                        cates,
                        "",
                        firstItemMSEC
                );

                if(!subs.contains(s))
                    subs.add(s);
            }

            Log.i("RssReader", "Finish Subscriptions");

            Pair<List<Subscription>, List<Tag>> pair = new Pair<List<Subscription>, List<Tag>>(subs, tags);

            return pair;
        }catch(Exception e){
            return null;
        }
    }

    private Unread getUnreadCount(){
        final String content = RssHttpClient.get(UNREADURL + "&ck=" + System.currentTimeMillis());

        try
        {
            JSONObject root = new JSONObject(content);

            List<UnreadCount> counts= new ArrayList<>();

            JSONArray unreadcounts = root.getJSONArray("unreadcounts");

            for(int i=0; i< unreadcounts.length();i++)
            {
                JSONObject count = unreadcounts.getJSONObject(i);

                counts.add(new UnreadCount(count.getString("id"), count.getInt("count"), new Date(count.getLong("updated"))));
            }

            Unread unread = new Unread(0, counts);

            Log.i("RssReader","Finish unreadcount");

            return unread;
        }
        catch (Exception ex)
        {
            Log.i("RssReader","Error Happen unreadcount" + ex.getMessage());
            return null;
        }
    }

    private List<String> getSortList(){
        String content = RssHttpClient.get("http://cloud.feedly.com/v3/preferences?ct=feedly.desktop&ck=" + System.currentTimeMillis());

        try {
            JSONObject result = new JSONObject(content);

            List<String> SortList = new ArrayList<String>();

            if (result.has("categoriesOrdering")) {
                JSONArray orders = new JSONArray(result.getString("categoriesOrdering"));

                int length = orders.length();
                for (int i = 0; i < length; i++) {
                    SortList.add(orders.getString(i));
                }
            }

            Log.i("RssReader", "Finish Sort List");

            return SortList;
        } catch (Exception e) {
            Log.i("RssReader", "Error Sort List" + e.getMessage());
            return null;
        }
    }

    public Profile getProfile() {
        String content = RssHttpClient.get(PROFILEURL);
        try {
            JSONObject result = new JSONObject(content);
            Profile p = new Profile();

//                			client: "feedly"
//                				created: 1412954194833
//                				dropboxConnected: false
//                				email: "redelva2008@163.com"
//                				evernoteConnected: false
//                				facebookConnected: false
//                				familyName: "陆"
//                				fullName: "陆国庆"
//                				givenName: "国庆"
//                				id: "5cdfeb7c-78fa-4389-a3ef-9e8edd496de9"
//                				locale: "en_US"
//                				paymentProviderId: {}
//                				paymentSubscriptionId: {}
//                				picture: "https://apis.live.net/v5.0/e532156f3145db48/picture"
//                				pocketConnected: false
//                				source: "feedly.desktop 24.0.861"
//                				twitterConnected: false
//                				wave: "2014.41"
//                				windowsLiveConnected: true
//                				windowsLiveId: "e532156f3145db48"
//                				wordPressConnected: false

            p.setEmail(result.getString("email"));
            p.setFamilyName(result.getString("familyName"));
            p.setGender(result.has("gender") ? result.getString("gender") : "");
            p.setGivenName(result.getString("givenName"));
            //p.Google = result.getString("google");
            if (result.has("google"))
                p.setAccount("Google");
            if (result.getBoolean("evernoteConnected"))
                p.setAccount("Evernote");
            if (result.getBoolean("facebookConnected"))
                p.setAccount("Facebook");
            if (result.getBoolean("twitterConnected"))
                p.setAccount("Twitter");
            if (result.getBoolean("windowsLiveConnected"))
                p.setAccount("WindowsLive");
            if (result.getBoolean("pocketConnected"))
                p.setAccount("Pocket");
            if (result.getBoolean("wordPressConnected"))
                p.setAccount("WordPress");
            p.setId(result.getString("id"));
            p.setLocale(result.getString("locale"));
            p.setPicture(result.getString("picture").replace("?sz=50", "?sz=420"));
            p.setReader(result.has("reader") ? result.getString("reader") : "");
            p.setWave(result.getString("wave"));
            return p;
        }
        catch(Exception e){
            return null;
        }
    }

    public List<String> sync(String userId, List<SyncState> unsyncs, long lastTimeSync){
        final ArrayList<String> results = new ArrayList<>();
        List<String> from = syncFromFeedly(lastTimeSync);
        List<String> to = syncToFeedly(userId, unsyncs);

        if(from != null){
            results.addAll(from);
        }

//        if(to != null){
//            results.addAll(to);
//        }

        return results;
    }

    private List<String> syncFromFeedly(long lastTimeSync){

        String url = "http://cloud.feedly.com/v3/markers/reads";

        url = url + "?newerThan=" + String.valueOf(lastTimeSync);

        String content = RssHttpClient.get(url);

        try {
            JSONObject data = new JSONObject(content);

            List<String> entryIds = new ArrayList<String>();

            for (int i = 0, len = data.getJSONArray("entries").length(); i < len; i++) {
                entryIds.add(data.getJSONArray("entries").getString(i));
            }

            Log.i("RssReader", "Sync from feedly complete");

            return entryIds;
        } catch (JSONException e) {
            Log.i("RssReader", "Error Sort List" + e.getMessage());
            return null;
        }
    }

    private List<String> syncToFeedly(String userId, List<SyncState> unsyncs){

        if(unsyncs == null || unsyncs.size() == 0)
            return null;

        //seperate blog and channel type first
        final List<String> readblogs = new ArrayList<String>();
        final List<String> unreadblogs = new ArrayList<String>();
        final List<String> starblogs = new ArrayList<String>();
        final List<String> unstarblogs = new ArrayList<String>();
        final List<SyncState> channels = new ArrayList<SyncState>();

        for(SyncState state : unsyncs){
            if(state.getBlogOriginId() != null && state.getBlogOriginId().length() > 0){

                if(state.getStatus() == RssAction.AsRead)
                    readblogs.add(state.getBlogOriginId());

                if(state.getStatus() == RssAction.AsUnread)
                    unreadblogs.add(state.getBlogOriginId());

                if(state.getStatus() == RssAction.AsStar)
                    starblogs.add(state.getBlogOriginId());

                if(state.getStatus() == RssAction.AsUnstar)
                    unstarblogs.add(state.getBlogOriginId());
            }

            if(state.getChannelId() != null && state.getChannelId().length() > 0){
                channels.add(state);
            }
        }

        //use batchMarkTag for blog

        ArrayList<String> results = new ArrayList<>();

        boolean read = batchMarkTag(userId, readblogs, RssAction.AsRead);
        if(read){
            results.addAll(readblogs);
        }

        boolean unread = batchMarkTag(userId, unreadblogs, RssAction.AsUnread);
        if(unread){
            results.addAll(unreadblogs);
        }

        boolean star = batchMarkTag(userId, starblogs, RssAction.AsStar);
        if(star){
            results.addAll(starblogs);
        }

        boolean unstar = batchMarkTag(userId, unstarblogs, RssAction.AsUnstar);
        if(unstar){
            results.addAll(unstarblogs);
        }

        return results;
    }

    private boolean batchMarkTag(final String userId, final List<String> blogs, RssAction action) {

        if(blogs == null || blogs.size() == 0)
            return false;

        String actionParams = "";

        String method = "";
        String url = "";
        if (action == RssAction.AsStar)
        {
            url = "http://cloud.feedly.com/v3/tags/user%2F" + userId + "%2Ftag%2Fglobal.saved?ct=feedly.desktop";
            method = "PUT";

            StringBuffer sb = new StringBuffer();
            for(String blog : blogs){
                sb.append("\"" + blog + "\",");
            }

            if(sb.length() > 0 )
                sb.deleteCharAt(sb.length() - 1);

            actionParams = "{\"entryId\":[" + sb.toString() + "]}";
            //params.put("entryId", sb.toString());
        }
        else if (action == RssAction.AsRead)
        {
            StringBuffer sb = new StringBuffer();
            for(String blog : blogs){
                sb.append("\"" + blog + "\",");
            }

            if(sb.length() > 0 )
                sb.deleteCharAt(sb.length() - 1);

            actionParams = "{\"action\":\"markAsRead\",\"type\":\"entries\",\"entryIds\":[" + sb.toString() + "]}";
            //params.put("action", "markAsRead");
            //params.put("type", "entries");
            //params.put("entryId", sb.toString());
            method = "POST";
            url = "http://cloud.feedly.com/v3/markers?ct=feedly.desktop";
        }
        else if (action == RssAction.AsUnread)
        {
            StringBuffer sb = new StringBuffer();
            for(String blog : blogs){
                sb.append("\"" + blog + "\",");
            }

            if(sb.length() > 0 )
                sb.deleteCharAt(sb.length() - 1);

            actionParams = "{\"action\":\"keepUnread\",\"type\":\"entries\",\"entryIds\":[" + sb.toString() + "]}";
            //params.put("action", "keepUnread");
            //params.put("type", "entries");
            //params.put("entryId", sb.toString());
            method = "POST";
            url = "http://cloud.feedly.com/v3/markers?ct=feedly.desktop";
        }

        url = url + "&ck=" + System.currentTimeMillis();

        if(actionParams.length() == 0)
        {
            return RssHttpClient.delete(url);
        }
        else
        {
//            StringEntity se = null;
//            try {
//                se = new StringEntity(actionParams.toString(),"UTF-8");
//            } catch (Exception e) {
//                e.printStackTrace();
//                return;
//            }

            return RssHttpClient.post(url, actionParams);
        }
    }

    public boolean markTag(String userId, Blog blog, RssAction action){
        String actionParams = "";

        String method = "";
        String url = "";
        if (action == RssAction.AsStar) {
            url = "http://cloud.feedly.com/v3/tags/user%2F" + userId + "%2Ftag%2Fglobal.saved?ct=feedly.desktop";
            method = "PUT";

            actionParams = "{\"entryId\":\"" + blog.getBlogId() + "\"}";
            //params.put("entryId", sb.toString());
        } else if (action == RssAction.AsUnstar) {
            url = "http://cloud.feedly.com/v3/tags/user%2F" + userId + "%2Ftag%2Fglobal.saved/" + blog.getBlogId() + "?ct=feedly.desktop";
            method = "DELETE";

            actionParams = "{\"entryId\":" + blog.getBlogId() + "}";
        } else if (action == RssAction.AsRead) {

            actionParams = "{\"action\":\"markAsRead\",\"type\":\"entries\",\"entryIds\":[\"" + blog.getBlogId() + "\"]}";
            //params.put("action", "markAsRead");
            //params.put("type", "entries");
            //params.put("entryId", sb.toString());
            method = "POST";
            url = "http://cloud.feedly.com/v3/markers?ct=feedly.desktop";
        }else if (action == RssAction.AsUnread) {
            actionParams = "{\"action\":\"keepUnread\",\"type\":\"entries\",\"entryIds\":[\"" + blog.getBlogId() + "\"]}";
            //params.put("action", "keepUnread");
            //params.put("type", "entries");
            //params.put("entryId", sb.toString());
            method = "POST";
            url = "http://cloud.feedly.com/v3/markers?ct=feedly.desktop";
        }

        url = url + "&ck=" + System.currentTimeMillis();

        boolean result = false;

        if(method.equals("DELETE")) {
            result = RssHttpClient.delete(url);
        }else if(method.equals("POST")){
            result = RssHttpClient.post(url, actionParams);
        }else if(method.equals("PUT")){
            result = RssHttpClient.put(url, actionParams);
        }

        Log.i("RssReader", "mark" + blog.getTitle() + " as " + action + " reulst is " + result);

        return result;
    }

    public boolean markTag(String userId, Channel channel, RssAction action){
        String actionParams = "";
        String url = "";
        String method = "";

        if (action == RssAction.UnSubscribe)
        {
            url = "https://cloud.feedly.com/v3/subscriptions/" + Uri.encode(channel.getChannelId(),"UTF-8") + "?ck=" + System.currentTimeMillis() + "&ct=feedly.desktop&cv=17.1.614";
            actionParams = "";
            method = "DELETE";
        }

        if (action == RssAction.RemoveTag)
        {
            //http://cloud.feedly.com/v3/subscriptions/feed%2Fhttp%3A%2F%2Ffeeds2.feedburner.com%2Fcnbeta-full?ck=1371959857730&ct=feedly.desktop
            //DELETE
            url = "https://cloud.feedly.com/v3/subscriptions/" + Uri.encode(channel.getChannelId(),"UTF-8") + "?ck="+ System.currentTimeMillis() +"&ct=feedly.desktop";
            actionParams = "";
            method = "DELETE";
        }

        if (action == RssAction.MoveTag)
        {
            //{"id":"feed/http://feeds2.feedburner.com/cnbeta-full","title":"cnBetaå…¨æ–‡ç‰ˆ","categories":[{"id":"user/d1ef3938-a54b-4404-9d9d-f97842124281/category/test","label":"test"}]}:
            //{"id":"feed/http://www.cnbeta.com/backend.php","title":"cnBeta.COM","categories":[{"id":"user/d1ef3938-a54b-4404-9d9d-f97842124281/category/Test","label":"Test"}]}
            String newTitle = String.valueOf(channel.getTag());
            if(newTitle.equals("Root"))
                actionParams = "{\"id\":\"" + TextUtils.htmlEncode(channel.getChannelId()) + "\",\"title\":\"" + TextUtils.htmlEncode(channel.getTitle()) + "\",\"categories\":[]}";
            else
                actionParams = "{\"id\":\"" + TextUtils.htmlEncode(channel.getChannelId()) + "\",\"title\":\"" + TextUtils.htmlEncode(channel.getTitle()) + "\",\"categories\":[{\"id\":\"user/" + userId + "/category/" + newTitle + "\",\"label\":\"" + newTitle + "\"}]}";
            url = "http://cloud.feedly.com/v3/subscriptions?ct=feedly.desktop";
            method = "POST";
        }

        if (action == RssAction.Rename)
        {
            String newTitle = String.valueOf(channel.getTag());
            actionParams = "{\"id\":\"" + channel.getChannelId() + "\",\"title\":\"" + newTitle + "\",\"categories\":[]}";
            url = "http://cloud.feedly.com/v3/subscriptions?ct=feedly.desktop";
            method = "POST";
        }

        if (action == RssAction.AllAsRead)
        {
            if(channel.getIsDirectory())
            {
                //{"action":"markAsRead","type":"categories","categoryIds":["user/d1ef3938-a54b-4404-9d9d-f97842124281/category/æŒ‡å¯¼"],"asOf":1371898725006}
                actionParams = "{\"action\":\"markAsRead\",\"type\":\"categories\",\"categoryIds\":[\"user/" +userId + "/category/" + TextUtils.htmlEncode(channel.getTitle()) + "\"],\"asOf\":" + System.currentTimeMillis() + "}";
                url = "http://cloud.feedly.com/v3/markers?ct=feedly.desktop";
            }
            else
            {
                //{"action":"markAsRead","type":"feeds","feedIds":["feed/http://www.wpdang.com/feed"],"asOf":1371917583346}
                actionParams = "{\"action\":\"markAsRead\",\"type\":\"feeds\",\"feedIds\":[\"" + channel.getChannelId() +"\"],\"asOf\":" + System.currentTimeMillis() + "}";
                url = "http://cloud.feedly.com/v3/markers?ct=feedly.desktop";
            }
            method = "POST";
        }

        url = url + "&ck=" + System.currentTimeMillis();

        boolean result = false;
        if(method.equals("DELETE"))
        {
            result = RssHttpClient.delete(url);
        }

        if(method.equals("POST"))
        {
            result = RssHttpClient.post(url, actionParams);
        }

        return result;
    }

    public List<Blog> getRssBlog(final Channel channel, final Blog blog, final int count) {
        HashMap<String, String> containers = new HashMap();

        ArrayList<Blog> blogs = new ArrayList<>();

        Pair<List<Blog>, Boolean> pair;

        if(containers != null){
            do {
                pair = getRssBlog(containers, channel, blog, count);

                if(pair == null){
                    return null;
                }

                if(pair.first != null){
                    blogs.addAll(pair.first);
                }

            }while(pair.second);
        }

        return blogs;
    }

    private Pair<List<Blog>, Boolean> getRssBlog(final HashMap<String, String> containers, final Channel channel, final Blog blog, final int count){
        String url = "";
        if(channel.getChannelId().equals("unread"))
            url = "https://cloud.feedly.com/v3/streams/contents?streamId=" + Uri.encode(channel.getChannelId()+ "&unreadOnly=true", "UTF-8");
        else if(channel.getChannelId().length() > 0)
            url = "https://cloud.feedly.com/v3/streams/contents?streamId=" + Uri.encode(channel.getChannelId(), "UTF-8");
        else
            url = "https://cloud.feedly.com/v3/streams/contents?streamId=" + Uri.encode("user/d1ef3938-a54b-4404-9d9d-f97842124281/category/global.all", "UTF-8");

        if (blog.getTimeStamp() > 0)
        {
            if (containers.containsKey("UP" + channel.getChannelId()) && containers.get("UP" + channel.getChannelId()).length() > 0)
            {
                url = url + "&count=" + count + "&continuation=" + containers.get("UP" + channel.getChannelId()) + "&newerThan=" + blog.getTimeStamp();
            }
            else
            {
                url = url + "&count=" + count;
            }
        }
        else if (blog.getTimeStamp() < 0)
        {
            if (saver.contains("DOWN" + blog.getChannelId()) && saver.getString("DOWN" + blog.getChannelId(), "").length() > 0)
            {
                url = url + "&count=" + count + "&continuation=" + saver.getString("DOWN" + blog.getChannelId(),"");
            }
            else
            {
                url = url + "&count=" + count;
            }
        }
        else if (blog.getTimeStamp() == 0)
        {
            url = url + "&count=" + count;
        }

        url = url + "&ct=feedly.desktop&unreadOnly=false&ranked=newest&ck=" + System.currentTimeMillis();

        String content = RssHttpClient.get(url);

        Log.i("RssReader", "Finish get rss blog request at " + new Date());

        try {
            JSONObject result = new JSONObject(content);

            String continuation = !result.has("continuation")
                    ? ""
                    : result.getString("continuation");
            if (blog.getTimeStamp() > 0) {
                //get lastest
                containers.put("UP" + channel.getChannelId(), continuation);
            } else if (blog.getTimeStamp() <= 0) {
                //get older
                saver.edit().putString("DOWN" + channel.getChannelId(), continuation).commit();
            }

            List<Blog> blogs = new ArrayList<Blog>();
            JSONArray array = result.getJSONArray("items");
            JSONObject item = null;
            for (int i = 0; i < array.length(); i++) {
                item = array.getJSONObject(i);

                Blog b = new Blog();

                b.setTagId("");
                b.setContent("");
                if (item.has("categories")) {
                    JSONObject obj = null;
                    JSONArray categories = item.getJSONArray("categories");
                    for (int j = 0; j < categories.length(); j++) {
                        obj = categories.getJSONObject(j);

                        if (obj.getString("id").contains("category")) {
                            b.setTagId(obj.getString("id"));
                            break;
                        }
                    }
                    //b.TagId = item.get("categories").Children().First(c => c["id"].Value<String>().Contains("category"))["id"].Value<String>();
                }
                b.setBlogId(item.getString("id"));
                b.setChannelId(item.getJSONObject("origin").getString("streamId"));

                if (item.has("title"))
                    b.setTitle(Html.fromHtml(item.getString("title")).toString());
                else
                    b.setTitle(Html.fromHtml(item.getJSONObject("origin").getString("title")).toString());

                b.setDescription("empty desc");

                if (item.has("summary"))
                    b.setDescription(item.getJSONObject("summary").getString("content"));
                if (item.has("content"))
                    b.setDescription(item.getJSONObject("content").getString("content"));
                if (item.has("alternate")) {
                    int alt = item.getJSONArray("alternate").length();
                    for (int j = 0; j < alt; j++) {
                        if (item.getJSONArray("alternate").getJSONObject(j).has("href"))
                            b.setLink(item.getJSONArray("alternate").getJSONObject(j).getString("href"));
                        if (item.getJSONArray("alternate").getJSONObject(j).has("originId"))
                            b.setLink(item.getString("originId"));

                        if (b.getLink().length() > 0)
                            break;
                    }
                }

                //remove cnbeta ad
                if (b.getLink()!= null && b.getLink().contains("cnbeta.com")) {
                    int index = b.getDescription().indexOf("<img");
                    if (index != -1)
                        b.setDescription(b.getDescription().substring(0, index));
                }

                b.setPubDate(new Date(item.getLong("crawled")));
                b.setSubsTitle(item.getJSONObject("origin").has("title") ? item.getJSONObject("origin").getString("title") : "");
                b.setTimeStamp(item.getLong("published"));
                b.setIsRead(item.has("unread") ? !item.getBoolean("unread") : false);
                if(item.has("visual") && item.getJSONObject("visual").has("url")){
                    b.setAvatar(item.getJSONObject("visual").has("url") ? item.getJSONObject("visual").getString("url") : "");
                    if (b.getAvatar().contains("dgtle.com")) {
                        b.setAvatar(b.getAvatar().replace("!600px",""));
                    }
                }

                if (item.has("tags")) {
                    JSONObject obj = null;
                    JSONArray tags = item.getJSONArray("tags");
                    for (int j = 0; j < tags.length(); j++) {
                        if (tags.getString(j).contains("saved"))
                            b.setIsStarred(true);
                    }
                }

                if(item.has("actionTimestamp")){
                    b.setActionTime(item.getLong("actionTimestamp"));
                }

                b.setOriginId(item.getString("id"));
                b.setIsRecommend (false);

                blogs.add(b);

                Log.d("RssReader", "Processing at index " + i);
            }

            //deal with long time no updates
            boolean hasMore = true;
            for (Blog b : blogs) {
                long between = (b.getPubDate().getTime() - blog.getPubDate().getTime()) / 1000;

                if (between > 0L)
                    hasMore = hasMore && true;
                else
                    hasMore = hasMore && false;
            }

            if (blog.getTimeStamp() <= 0L)
                hasMore = false;

            if(!hasMore){
                if (blog.getTimeStamp() > 0)
                    containers.put("UP" + channel.getChannelId(), "");
            }

            return new Pair<>(blogs, hasMore);
        } catch (Exception json) {
            json.printStackTrace();
            return null;
        }
    }

    public List<ImageRecord> getFavor(String tag, Blog blog, int count){
        return null;
    }

    public boolean addRss(String rssUrl, String searchResultTitle){
        String actionParams = "";

        actionParams = "{\"id\":\"" + rssUrl + "\"," +
                "\"title\":\"" + searchResultTitle + "\"," +
                "\"categories\":[]," +
                "\"via\":\"" + rssUrl + "\"," +
                "\"viaType\":\"direct\"," +
                "\"viaPage\":\"subscription/" + rssUrl + "\"}";

        String url = "http://feedly.com/v3/subscriptions?ck=" + System.currentTimeMillis() + "&ct=feedly.desktop&cv=16.0.548";

        return RssHttpClient.post(url, actionParams);
    }

    public boolean assignFolder(Channel folder, Channel single){
        return true;
    }

    public List<Result> searchRss(String key, int page){
        String url = "http://www.feedly.com/v3/search/feeds?q=" + Uri.encode(key, "UTF-8") + "&n=20&d=true&ck=" + System.currentTimeMillis();

        String content = RssHttpClient.get(url);

        List<Result> results = new ArrayList<Result>();

        try{
            JSONObject obj = new JSONObject(content);
            JSONObject r = null;
            int len = obj.getJSONArray("results").length();
            for(int i=0; i<len;i++){
                r = obj.getJSONArray("results").getJSONObject(i);
                Result result = new Result();

                result.setSubscribed(false);
                result.setTitle(r.getString("title"));
                result.setFeedId(r.getString("feedId"));
                result.setSubscriptCount(r.getString("subscribers"));
                result.setDescription(r.getString("description"));
                results.add(result);
            }
        }catch(JSONException je){
            je.printStackTrace();
        }

        return results;
    }
}
