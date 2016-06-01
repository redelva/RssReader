package com.lgq.rssreader.formatter;

import android.util.Log;

import com.lgq.rssreader.R;
import com.lgq.rssreader.abstraction.HttpClient;
import com.lgq.rssreader.abstraction.RssHttpClient;
import com.lgq.rssreader.core.ReaderApp;
import com.lgq.rssreader.model.Blog;
import com.lgq.rssreader.util.HtmlUtil;
import com.lgq.rssreader.util.UrlUtil;

import org.apache.http.Header;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

public abstract class BlogFormatter
{
    public static final String prefix = "/images";
    private static final String imageData = "data:image/gif;base64,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";

    public String BackgroundColor;
    public String FontColor;
    public boolean EnableCache;
    public boolean NoImageMode;

    protected abstract String LoadFromCache(Blog blog);
    protected abstract String Download(Blog blog);
    protected abstract String GetReadableString(String content);

    public interface FlashCompleteHandler{
        void onFlash(Object sender, CacheEventArgs e);
    }

    protected FlashCompleteHandler FlashComplete;

    public void setFlashCompleteHandler(FlashCompleteHandler handler){
        this.FlashComplete = handler;
    }

    public String render(final Blog blog) {
        String content = LoadFromCache(blog);
        if (content.length() == 0) {
            content = Download(blog);

            if(content == null || content.length() == 0){
                return "";
            }

            String readable = GetReadableString(content);

            if(readable == null || readable.length() == 0){
                return "";
            }

            if(blog.getLink().contains("cnbeta")){
                String sample = HtmlUtil.filterHtml(blog.getDescription()).substring(0,10).replace(" ", "");

                String puretext = HtmlUtil.trim(HtmlUtil.filterHtml(readable)).replace(" ", "");

                if(!puretext.contains(sample))
                    readable = blog.getDescription() + readable;
            }

            Document doc = Jsoup.parse(readable);

            doc = dealLink(doc);

            doc = dealFlash(doc, blog);

            doc = dealVideoLink(doc, blog);

            doc = dealWeiphone(doc, blog);

            doc = dealFont(doc);

            doc = dealStyle(doc);

            doc = dealImageLazyLoading(doc);

            if (NoImageMode)
                doc = removeImage(doc);

            final Element body = doc.body().clone();

            if (EnableCache){
                new Thread(){
                    public void run(){
                        cacheImage(body, blog);
                    }
                }.start();
            }

            return doc.outerHtml();
        }
        else {
            return content;
        }
    }

    private void cacheImage(Element body, Blog blog){
        for(final Element node : body.getElementsByTag("img")) {
            if(node.attr("src").startsWith("..")){
                continue;
            }

            if(node.hasAttr("xSrc") && !node.attr("xSrc").startsWith("..")){
                // TODO: 2015-10-09 cache image
                //ImageRecord record = ImageUtil.loadDrawable(blog, node.attr("xSrc"));
                //node.attr("xSrc", record.getStoredName().replace("/rssreader", ".."));
            }
        }
    }

    private Document dealLink(Document doc){
        for(Element node : doc.getElementsByTag("a")) {
            if (node.hasAttr("onclick"))
                node.attr("onclick","linkHandle()");
            else
                node.attributes().put("onclick", "linkHandle()");
        }

        return doc;
    }

    private Document dealFlash(Document doc, final Blog blog){
        final List<Element> embeds =  doc.getElementsByTag("embed");
        for(Element d : doc.getElementsByTag("iframe")){
            if(d.hasAttr("src") &&
                (
                    d.attr("src").contains("swf") ||
                    d.attr("src").contains("youku") ||
                    d.attr("src").contains("sohu") ||
                    d.attr("src").contains("tudou") ||
                    //d.attr("src").contains("youtube") ||
                    d.attr("src").contains("ku6")
                )
            )
            embeds.add(d);
        }
        for (int i = 0, len=embeds.size(); i < len; i++) {
            final Element tip = doc.createElement("div");
            Element msg = doc.createElement("div");
            //var click = doc.CreateElement("a");
            tip.appendChild(msg);
            //tip.AppendChild(click);
            msg.html("RemoveFlash");
            msg.attributes().put("id", "msg" + i);
            //msg.Attributes.Add("style", "color:red;");
            msg.attributes().put("style", "color:red;display:none;");
            //click.Attributes.Add("id", "click" + i);

            for (int j = 0; j < 20; j++) {
                Element click = doc.createElement("a");
                tip.appendChild(click);
                //var br = doc.CreateElement("br");
                //tip.AppendChild(br);
                click.attributes().put("id", "click" + i + j);
                //click.Attributes.Add("style", "display:none");
                click.attributes().put("onclick", "linkHandle()");
            }

            if (!embeds.get(i).html().contains("youtube")) {
                if (embeds.get(i).hasAttr("style"))
                    embeds.get(i).attr("style", "display:none;");
                else
                    embeds.get(i).attributes().put("style", "display:none;");
            }

            if (embeds.get(i).hasAttr("id"))
                embeds.get(i).attr("id", "flash" + i);
            else
                embeds.get(i).attributes().put("id", "flash" + i);

            if (doc.getElementsByAttributeValue("id", "msg" + i).size() == 0)
                embeds.get(i).before(tip);

            final String src = embeds.get(i).attr("src");

            final int tmp = i;

            new Runnable(){
                @Override
                public void run() {
                    parseFlash(tmp, blog, embeds.get(tmp).clone(), tip.clone(), src);
                }
            }.run();
        }

        return doc;
    }

    private Document dealVideoLink(Document doc, final Blog blog){
        final List<Element> embeds =  doc.getElementsByTag("embed");
        for(Element d : doc.getElementsByTag("iframe")){
            if(d.hasAttr("src") &&
                (
                    d.attr("src").contains("swf") ||
                    d.attr("src").contains("youku") ||
                    d.attr("src").contains("sohu") ||
                    d.attr("src").contains("tudou") ||
                    d.attr("src").contains("youtube") ||
                    d.attr("src").contains("ku6")
                )
            )
            embeds.add(d);
        }

        //region Video Link
        if (embeds.size() == 0 ){
            final List<Element> links = new ArrayList<Element>();
            List<String> urls = new ArrayList<String>();

            for(Element d : doc.getElementsByTag("a")){
                if(d.hasAttr("href") &&
                    (
                        d.attr("href").contains("youku") ||
                        d.attr("href").contains("sohu") ||
                        d.attr("href").contains("youtube") ||
                        d.attr("href").contains("ku6") ||
                        d.attr("href").contains("tudou") ||
                        d.attr("href").contains("swf")
                    )
                )
                    if(!urls.contains(d.attr("href"))){
                        links.add(d);
                        urls.add(d.attr("href"));
                    }
            }

            for(Element p : doc.getElementsByTag("p")){
                if(p.html().contains("youku") ||
                    p.html().contains("sohu") ||
                    p.html().contains("youtube") ||
                    p.html().contains("ku6") ||
                    p.html().contains("tudou") ||
                    p.html().contains("swf")
                )
                    if(!urls.contains(p.html())){
                        links.add(p);
                        urls.add(p.html());
                    }
            }

            for (int i = 0, len = links.size(); i<len; i++) {
                final Element tip = doc.createElement("div");
                Element msg = doc.createElement("div");
                //var click = doc.CreateElement("a");
                tip.appendChild(msg);
                //tip.AppendChild(click);
                //msg.InnerHtml = Resources.StringResources.RemoveFlash;
                msg.attributes().put("id", "msg" + i);
                //click.Attributes.Add("id", "click" + i);
                //click.Attributes.Add("style", "display:none");
                //click.Attributes.Add("onclick", "linkHandle()");

                for (int j = 0; j < 20; j++)
                {
                    Element click = doc.createElement("a");
                    tip.appendChild(click);
                    click.attributes().put("id", "click" + i + j);
                    //var br = doc.CreateElement("br");
                    //tip.AppendChild(br);
                    //click.Attributes.Add("style", "display:none");
                    click.attributes().put("onclick", "linkHandle()");
                }

                if (links.get(i).hasAttr("id"))
                    links.get(i).attr("id", "flash" + i);
                else
                    links.get(i).attributes().put("id", "flash" + i);
                if (doc.getElementsByAttributeValue("id", "msg" + i).size() == 0)
                    links.get(i).before(tip);

                final String src = links.get(i).attr("href");
                final int tmp = i;



                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        parseFlash(tmp, blog, links.get(tmp).clone(), tip.clone(), src);
                    }
                }).start();
            }
        }

        return doc;
    }

    private Document dealWeiphone(Document doc, final Blog blog){
        final List<Element> embeds = doc.getElementsByTag("embed");
        for(Element d : doc.getElementsByTag("iframe")){
            if(d.hasAttr("src") &&
                    (
                            d.attr("src").contains("swf") ||
                                    d.attr("src").contains("youku") ||
                                    d.attr("src").contains("sohu") ||
                                    d.attr("src").contains("tudou") ||
                                    d.attr("src").contains("youtube") ||
                                    d.attr("src").contains("ku6")
                    )
                    )
                embeds.add(d);
        }

        final List<Element> loadings = new ArrayList<Element>();
        for(Element d :doc.getElementsByTag("p")){
            if(d.attr("id").startsWith("weiphoneplayer")){
                loadings.add(d);
            }
        }
        for (int i = 0; i < loadings.size(); i++) {
            final Element tip = doc.createElement("div");
            Element msg = doc.createElement("div");
            //var click = doc.CreateElement("a");
            tip.appendChild(msg);
            //tip.AppendChild(click);
            msg.html("RemoveFlash");
            msg.attributes().put("id", "msg" + (i+embeds.size()));
            //msg.Attributes.Add("style", "color:red;");
            msg.attributes().put("style", "color:red;display:none;");
            //click.Attributes.Add("id", "click" + i);

            for (int j = 0; j < 20; j++)
            {
                Element click = doc.createElement("a");
                tip.appendChild(click);
                //var br = doc.CreateElement("br");
                //tip.AppendChild(br);
                click.attributes().put("id", "click" + (i+embeds.size()) + j);
                //click.Attributes.Add("style", "display:none");
                click.attributes().put("onclick", "linkHandle()");
            }

            if (!loadings.get(i).html().contains("youtube"))
            {
                if (loadings.get(i).hasAttr("style"))
                    loadings.get(i).attr("style", "display:none;");
                else
                    loadings.get(i).attributes().put("style", "display:none;");
            }

            if (loadings.get(i).hasAttr("id"))
                loadings.get(i).attr("id", "flash" + (i+embeds.size()));
            else
                loadings.get(i).attributes().put("id", "flash" + (i+embeds.size()));

            if (doc.getElementsByAttributeValue("id", "msg" + (i+embeds.size())).size() == 0) {
                loadings.get(i).before(tip);
            }

            final int tmp = i;

            new Runnable(){
                @Override
                public void run() {
                    parseFlash(tmp, blog, loadings.get(tmp).clone(), tip.clone(), "weiphone");
                }
            }.run();
        }

        return doc;
    }

    private Document dealFont(Document doc){
        Elements fonts = doc.getElementsByTag("font");

        for (int i=0, len=fonts.size(); i < len; i++){
            Element spanFont = doc.createElement("span");

            spanFont.html(fonts.get(i).html());

            fonts.get(i).before(spanFont);

            fonts.get(i).remove();
        }

        return doc;
    }

    private Document dealStyle(Document doc){
        for(Element c : doc.getElementsByAttribute("style")){
            c.attr("style", c.attr("style").toLowerCase().replace("width", "w"));
            c.attr("style", c.attr("style").toLowerCase().replace("height", "h"));
            c.attr("style", c.attr("style").toLowerCase().replace("font", "f"));
            c.attr("style", c.attr("style").toLowerCase().replace("background", "b"));
        }

        for(Element c : doc.getElementsByAttribute("height")){
            //c.attr("height", c.attr("height").toLowerCase().replace("height", "h"));
            c.removeAttr("height");
        }

        for(Element c : doc.getElementsByAttribute("width")){
            //c.attr("width", c.attr("width").toLowerCase().replace("width", "w"));
            c.removeAttr("width");
        }

        for(Element c : doc.getElementsByTag("object")){
            c.attr("style", c.attr("style").toLowerCase().replace("width", "w"));
            c.attr("style", c.attr("style").toLowerCase().replace("height", "h"));
            c.attr("style", c.attr("style").toLowerCase().replace("font", "f"));
            c.attr("style", c.attr("style").toLowerCase().replace("background", "b"));

            for(Element param : c.children()){
                if(param.tagName().equals("allowfullscreen")){
                    param.attr("allowfullscreen","false");
                    break;
                }
            }

            c.removeAttr("width");
            c.removeAttr("style");
            c.removeAttr("height");
            c.attr("width", "350px");
            c.attr("height", "290px");
        }

        for(Element c : doc.getElementsByTag("iframe")){

            if(c.hasAttr("style")){

                if(c.attr("style").toLowerCase().contains("width")){
                    String[] attrs = c.attr("style").split(";");
                    for(String attr  : attrs){
                        if(attr.toLowerCase().contains("width")){
                            c.attr("style", c.attr("style").toLowerCase().replace(attr.toLowerCase(), "width:99%"));
                        }
                    }
                }else{
                    c.attr("style", c.attr("style") + "width:99%;");
                }

//        			if(c.attr("style").toLowerCase().contains("height")){
//        				String[] attrs = c.attr("style").split(";");
//                        for(String attr  : attrs){                        	
//                        	if(attr.toLowerCase().contains("height")){
//                        		c.attr("style", c.attr("style").toLowerCase().replace(attr.toLowerCase(), "height:100%"));
//                        	}
//                        }
//        			}else{
//        				c.attr("style", c.attr("style") + "height:100%;");
//        			}
            }else{
                c.attr("style", "width:100%;");
            }
        }

        return doc;
    }

    private Document dealImageLazyLoading(Document doc){
        List<Element> imgs = new ArrayList<Element>();
        for(Element d : doc.getElementsByTag("img")){

            if (d.hasAttr("width"))
                d.removeAttr("width");
            if (d.hasAttr("height"))
                d.removeAttr("height");

            if(d.hasAttr("src") &&
                    (!d.hasAttr("xSrc") || !d.attr("xSrc").contains(prefix))
                    )
                imgs.add(d);
        }
        for(Element img : imgs) {
            if (!img.hasAttr("xSrc") && img.hasAttr("src")) {
                if(!img.attr("src").startsWith(prefix)){

                    String value = img.attr("src");

                    value = HtmlUtil.extraReplace(value);

                    img.attributes().put("xSrc",value);
                    img.attr("src", imageData);
                }
            }

            if (img.hasAttr("style")){
                img.attr("style", img.attr("style") + "margin:auto;");
            }else{
                img.attributes().put("style", "margin:auto;");
            }
        }

        return doc;
    }

    private Document removeImage(Document doc){
        for(Element img : doc.getElementsByTag("img")){
            img.remove();
        }
        return doc;
    }

    private void parseFlash(final int cnt, final Blog blog, final Element embed, final Element tip, final String url) {
        if (url.contains("youku")){
            youku(cnt, blog, embed, tip, url);
        }
        else if (url.contains("youtube")){
            youtube(cnt, blog, embed, tip, url);
        }
        else if (url.contains("sohu")){
            sohu(cnt, blog, embed, tip, url);
        }
        else if (url.contains("weiphone")){
            weiphone(cnt, blog, embed, tip, url);
        }
        else if (url.contains("tudou")){
            tudou(cnt, blog, embed, tip, url);
        }
        else if (url.contains("ku6")){
            ku6(cnt, blog, embed, tip, url);
        }
        else if (url.contains("qq")){
            qq(cnt, blog, embed, tip, url);
        }
        else if (url.contains("56")){
            fivesix(cnt, blog, embed, tip, url);
        }
        else{

            new Thread(){
                public void run(){
                    if (FlashComplete != null){
                        tip.html("");
                        FlashComplete.onFlash(ReaderApp.getContext().getResources().getString(R.string.blog_videooptimize), new CacheEventArgs(blog, embed, tip, cnt, -1));
                    }
                }
            }.start();
        }
    }

    static HashMap<String, String> stream_types = new HashMap<String, String>(){{
        put("flv","flv");
        put("mp4","mp4");
        put("hd2","flv");
        put("mp4hd","mp4");
        put("mp4hd2","mp4");
        put("3gphd","mp4");
        put("3gp","flv");
        put("flvhd","flv");
    }};

    private void youku(final int cnt, final Blog blog, final Element embed, final Element tip, final String vurl){
        int index = vurl.indexOf('X');
        if (index == -1)
            return;

        try {
            String id = "";//vurl.substring(index, index + 15);
            String[] parts = vurl.split("/");
            for (String part : parts) {
                int i = part.indexOf('X');
                if(i > -1){
                    id = part.substring(i, i+15);
                }
            }

            if(id.length() == 15)
                id = id + "==";

            String content = HttpClient.get("http://play.youku.com/play/get.json?vid=" + id +"&ct=12", new HashMap<String, String>());
            JSONObject youku = new JSONObject(content);
            if (youku.getJSONObject("data").getJSONArray("stream").getJSONObject(0).getJSONArray("segs") != null) {
                String q = "";
                String fileid = youku.getJSONObject("data").getJSONArray("stream").getJSONObject(0).getString("stream_fileid");
                String stream_type = youku.getJSONObject("data").getJSONArray("stream").getJSONObject(0).getString("stream_type");
                String key = youku.getJSONObject("data").getJSONArray("stream").getJSONObject(0).getJSONArray("segs").getJSONObject(0).getString("key");
                String encrypt_string = youku.getJSONObject("data").getJSONObject("security").getString("encrypt_string");
                String ip = youku.getJSONObject("data").getJSONObject("security").getString("ip");
                String sidAndtoken = rc4(translate("b4eto0b4").toString(), decode64(encrypt_string));
                String sid = sidAndtoken.split("_")[0];
                String token = sidAndtoken.split("_")[1];
                double ts = youku.getJSONObject("data").getJSONArray("stream").getJSONObject(0).getJSONArray("segs").getJSONObject(0).getDouble("total_milliseconds_video") / 1000;

                String r = "/player/getFlvPath/sid/" + sid + "_00/st/" + stream_types.get(stream_type) + "/fileid/" + fileid + "?K=" + key + "&hd=1&myp=0&ts=" + ts + "&ypp=0" + q;

                String t = encode64(rc4(translate("boa4poz1").toString(), sid + "_" + fileid + "_" + token));
                r += "&ep=" + t;
                r += "&ctype=12";
                r += "&ev=1";
                r += "&token=" + token;
                r += "&oip=" + ip;
                r = "http://k.youku.com" + r;

                tip.html("");

                String url = RssHttpClient.youku(r);

                if(url == null)
                   url = r;

                if (FlashComplete != null && url != null) {
                    tip.html(tip.html() + url + "|");
                }

                String imgUrl = youku.getJSONObject("data").getJSONObject("video").get("logo").toString();

                tip.html(tip.html().substring(0, tip.html().length() - 1) + "____" + imgUrl);
                FlashComplete.onFlash(youku.getJSONObject("data").getJSONObject("video").getString("title"), new CacheEventArgs(blog, embed, tip, cnt, 0));
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void youtube(final int cnt, final Blog blog, final Element embed, final Element tip, final String url){
        Pattern p = Pattern.compile("(?:youtube\\.com/(?:user/.+/|(?:v|e(?:mbed)?)/|.*[?&]v=)|youtu\\.be/)([^\"&?/ ]{11})");
        try{
            final String group = p.matcher(url).toMatchResult().group();
            final String id = group.substring(group.length() - 11);

            String response = RssHttpClient.get("https://www.youtube.com/get_video_info?video_id=" + id);
            if (!response.contains("fail")){
                String results = HtmlUtil.unescape(response);
                List<String> result = processYoutube(results);
                tip.html(HtmlUtil.unescape(result.get(0)) + "____" + HtmlUtil.unescape(result.get(1)));
                if (FlashComplete != null)
                    FlashComplete.onFlash("Youtube Video", new CacheEventArgs(blog, embed, tip, cnt, 0));
            }
        }catch(Exception e){

        }
    }

    private void sohuSwf(final int cnt, final Blog blog, final Element embed, final Element tip, final String url){
        String vid = "";
        boolean hasId = false;
        for(int i=0,len = url.split("&").length; i< len;i++){
            if(url.split("&")[i].contains("id")){
                hasId = true;
                vid = url.split("&")[i];
                break;
            }
        }

        for(int i=0,len = url.split("/").length; i< len;i++){
            if(url.split("/")[i].contains(".shtml")){
                hasId = true;
                vid = url.split("/")[i].replace(".shtml","");
                break;
            }
        }

        if(hasId){
            vid = vid.split("=")[1];

            String content = HttpClient.get("http://my.tv.sohu.com/videinfo.jhtml?m=viewtv&vid=" + vid, new HashMap<String, String>());
            try{
                JSONObject sohu = new JSONObject(content);

                if (!sohu.isNull("data")) {
                    tip.html("");

                    final Object syncLock = new Object();
                    final int count = 0;

                    String allot = sohu.getString("allot");
                    String prot = sohu.getString("prot");
                    final int len=sohu.getJSONObject("data").getJSONArray("clipsURL").length();
                    for(int i=0; i<len;i++){
                        final String su = sohu.getJSONObject("data").getJSONArray("su").getString(i);
                        String clipsURL = sohu.getJSONObject("data").getJSONArray("clipsURL").getString(i);

                        if (FlashComplete != null) {
                            tip.html(tip.html() + clipsURL + "|");

                            synchronized(syncLock){
                                if(tip.html().split("[|]").length == len){
                                    try {
                                        tip.html(tip.html().substring(0, tip.html().length() - 1) + "____" + sohu.getJSONObject("data").getString("coverImg"));
                                        if (FlashComplete != null)
                                            FlashComplete.onFlash(sohu.getJSONObject("data").getString("tvName"), new CacheEventArgs(blog, embed, tip, cnt, 0));
                                    } catch (Exception e) {
                                        // TODO Auto-generated catch block
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }
                    }
                }
            }
            catch(Exception e){
                e.printStackTrace();
            }
        }
        else
        {
            tip.html("");
            FlashComplete.onFlash(ReaderApp.getContext().getResources().getString(R.string.blog_videooptimize), new CacheEventArgs(blog, embed, tip, cnt, -1));
        }
    }

    private void sohuNonSwf(final int cnt, final Blog blog, final Element embed, final Element tip, final String url){
        String result = RssHttpClient.get(url);
        int index = result.indexOf("vid");

        if (index == -1) {
            tip.html("");
            FlashComplete.onFlash(ReaderApp.getContext().getResources().getString(R.string.blog_videooptimize), new CacheEventArgs(blog, embed, tip, cnt, -1));
        }

        int comma = result.indexOf("\"", index + 5);
        String vid = result.substring(index + 5, comma - 5 - index);

        String content = RssHttpClient.get("http://hot.vrs.sohu.com/vrs_flash.action?vid=" + vid);
        try{
            JSONObject sohu = new JSONObject(content);
            if (sohu.isNull("data"))
            {
                tip.html("");
                for(int i=0, len=sohu.getJSONObject("data").getJSONArray("clipsURL").length(); i< len; i++)
                {
                    String child = sohu.getJSONObject("data").getJSONArray("clipsURL").getString(i);
                    if (FlashComplete != null)
                    {
                        tip.html(tip.html() + child + "|");
                    }
                }
                tip.html(tip.html().substring(0, tip.html().length() - 1) + "____" + sohu.getJSONObject("data").getString("coverImg"));
                if (FlashComplete != null)
                    FlashComplete.onFlash(sohu.getJSONObject("data").getString("tvName"), new CacheEventArgs(blog, embed, tip, cnt, 0));
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private void sohu(final int cnt, final Blog blog, final Element embed, final Element tip, final String url){
        if(url.contains("swf")){
            sohuSwf(cnt, blog, embed, tip, url);
        }
        else{
            sohuNonSwf(cnt, blog, embed, tip, url);
        }
    }

    private void weiphone(final int cnt, final Blog blog, final Element embed, final Element tip, final String url){
        new Thread(){
            public void run(){
                String result = embed.attr("weiphone_src");

                if(result.indexOf("swf") == -1){
                    tip.html(HtmlUtil.unescape(result) + "____");
                    if (FlashComplete != null)
                        FlashComplete.onFlash("Weiphone", new CacheEventArgs(blog, embed, tip, cnt, 0));
                }else{
                    tip.html("");
                    FlashComplete.onFlash(ReaderApp.getContext().getResources().getString(R.string.blog_videooptimize), new CacheEventArgs(blog, embed, tip, cnt, -1));
                }
            }
        }.start();
    }

    private void tudou(final int cnt, final Blog blog, final Element embed, final Element tip, final String url){
        String location = RssHttpClient.tudou(url);

        String iid = UrlUtil.findValueInUrl(location, "iid");
        String title = HtmlUtil.unescape(UrlUtil.findValueInUrl(location, "title"));
        String coverImg = HtmlUtil.unescape(UrlUtil.findValueInUrl(location, "snap_pic"));

        if(iid.length() > 0){
            tip.html("http://vr.tudou.com/v2proxy/v2?it=" + iid + "&st=52&pw=____" + coverImg);
            FlashComplete.onFlash(title, new CacheEventArgs(blog, embed, tip, cnt, 0));
        }else{
            String content = HttpClient.get(url, new HashMap<String, String>());
            //String[] parts = content.split(",");
            iid = UrlUtil.findValueInConetent(content, ",", ":", "iid");
            coverImg = UrlUtil.findValueInConetent(content, ",", ":", "picUrl");

            tip.html("http://vr.tudou.com/v2proxy/v2?it=" + iid + "&st=52&pw=____" + coverImg);
            FlashComplete.onFlash(ReaderApp.getContext().getResources().getString(R.string.blog_videooptimize), new CacheEventArgs(blog, embed, tip, cnt, -1));
        }
    }

    private void ku6(final int cnt, final Blog blog, final Element embed, final Element tip, final String url){

        if(url.contains("refer")){
            String[] segs = null;
            segs = url.split("/");
            int index = Arrays.asList(segs).indexOf("refer");
            String id = segs[index + 1];

            String content = RssHttpClient.get("http://v.ku6.com/fetch.htm?t=getVideo4Player&vid=" + id);

            try {
                JSONObject root = new JSONObject(content);
                if (FlashComplete != null){
                    tip.html(root.getJSONObject("data").getString("f"));
                    FlashComplete.onFlash(root.getJSONObject("data").getString("f"), new CacheEventArgs(blog, embed, tip, cnt, 0));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else{
            String[] segs = null;
            segs = embed.attr("flashvars").split("&");

            String vidUrl = "http://v.ku6vms.com/phpvms/player/forplayer" +
                    "/vid/" + segs[0].split("=")[1] +
                    "/style/" + segs[1].split("=")[1] +
                    "/sn/" + segs[2].split("=")[1];

            String content = RssHttpClient.post(vidUrl);

            try {
                JSONObject vidRoot = new JSONObject(content);
                String rc = RssHttpClient.get("http://v.ku6.com/fetch.htm?t=getVideo4Player&vid=" + vidRoot.getString("ku6vid"));

                        try {
                            JSONObject root = new JSONObject(rc);
                            if (FlashComplete != null){
                                tip.html(root.getJSONObject("data").getString("f") + "?stype=mp4____" + vidRoot.getString("picpath"));
                                FlashComplete.onFlash(root.getJSONObject("data").getString("f"), new CacheEventArgs(blog, embed, tip, cnt, 0));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void qq(final int cnt, final Blog blog, final Element embed, final Element tip, final String url){
        String[] segs = url.split("&");
        String vid = "";

        for(String seg : segs){
            if(seg.contains("vid")){
                vid = seg.split("=")[1];
            }
        }

        if(vid.length() == 0)
            return;

        String root = HttpClient.get("http://vv.video.qq.com/geturl?vid=" + vid + "&otype=json&platform=1&ran=0%2E9652906153351068",new HashMap<String, String>());

        String tmp = root.replace("QZOutputJson=", "");

        JSONObject result;
        try {
            result = new JSONObject(tmp.substring(0, tmp.length() - 1));
            if (FlashComplete != null){

                String videoUrl = result.getJSONObject("vd").getJSONArray("vi").getJSONObject(0).getString("url");

                tip.html(videoUrl + "____");
                FlashComplete.onFlash(ReaderApp.getContext().getResources().getString(R.string.blog_videooptimize), new CacheEventArgs(blog, embed, tip, cnt, 0));
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void fivesix(final int cnt, final Blog blog, final Element embed, final Element tip, final String url){
        String vid = "";

        //http://www.56.com/u80/v_NjAzNjM0MDU.html
        int start = url.indexOf("v_");
        int end = url.indexOf(".html");

        if(start == -1 || end == -1 || start > end)
            return;

        vid = url.substring(start + 2, end);

        if(vid.length() == 0)
            return;

        String content = RssHttpClient.get("http://vxml.56.com/json/" + vid + "/");
        try {
            JSONObject root = new JSONObject(content);
            if (FlashComplete != null){
                String title = root.getJSONObject("info").getString("Subject");
                String videoUrl = root.getJSONObject("info").getJSONArray("rfiles").getJSONObject(0).getString("url");
                String img = root.getJSONObject("info").getString("bimg");
                tip.html(videoUrl + "____" + img);
                FlashComplete.onFlash(title, new CacheEventArgs(blog, embed, tip, cnt, 0));
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private List<String> processYoutube(String rdata) {
        List<String> result = new ArrayList<String>();
        String[] rdataArray = HtmlUtil.unescape(rdata).split("&");
        for (int i = 0; i < rdataArray.length; i++) {
            if (rdataArray[i].length() > 13) {
                String r0 = rdataArray[i].substring(0, 13);
                if (r0 == "thumbnail_url")
                {
                    String r1 = HtmlUtil.unescape(rdataArray[i].substring(14)).replace("/default", "/hqdefault");
                    result.add(1, r1);
                }
            }
            if (rdataArray[i].length() > 26) {
                String r0 = rdataArray[i].substring(0, 26);
                if (r0 == "url_encoded_fmt_stream_map") {
                    String r1 = HtmlUtil.unescape(rdataArray[i].substring(0,27));
                    String[] temp1 = r1.split(",");
                    ArrayList<Integer> fmt = new ArrayList<Integer>();
                    ArrayList<String> fmt_url = new ArrayList<String>();
                    for (int j = 0; j < temp1.length; j++) {
                            /*
                            temp1[j] = temp1[j].substr(4);
                            var temp2 = temp1[j].split('&itag=');
                            fmt.push(parseInt(temp2[1], 10));
                            fmt_url.push(temp2[0]);
                            */
                        String[] temp2 = temp1[j].split("&");
                        for (int jj = 0; jj < temp2.length; jj++) {
                            int temp_itag = -1;
                            String temp_type = "";
                            if (temp2[jj].substring(0, 5).equals("itag=")) {
                                temp_itag = Integer.valueOf(temp2[jj].substring(5));
                                fmt.add(temp_itag);
                            }
                            else if (temp2[jj].substring(0, 4).equals("url=")) {
                                fmt_url.add(temp2[jj].substring(4));
                            }
                            else if (temp2[jj].substring(0, 5).equals("type=")) {
                                temp_type = '(' + HtmlUtil.unescape(temp2[jj].substring(5)) + ')';
                            }

                            //if(fmt_str[temp_itag] == 'undefined')
                            //{
                            //    fmt_str[temp_itag] = temp_type;
                            //}
                        }
                    }

                    int index = 0;
                    for(int k : fmt) {
                        if (k == 18 || k == 22 || k == 37 || k == 38 || k == 82 || k == 83 || k == 84 || k == 85) {
                            result.add(0, HtmlUtil.unescape(fmt_url.get(index)));
                        }
                        index++;
                    }
                }
            }

        }
        return result;
    }

    private String convertString(List<Character> array){
        StringBuilder s = new StringBuilder();
        for(Character i : array)
            s.append(i);

        return s.toString();
    }

    private String decode64(String a) {
        if (a == null || a.length() == 0)
            return "";
        int c, b;
        int[] h = new int[]{-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 62, -1, -1, -1, 63, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, -1, -1, -1, -1, -1, -1, -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, -1, -1, -1, -1, -1, -1, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, -1, -1, -1, -1, -1};
        int i = a.length();
        int f = 0;
        List<Character> d = new ArrayList<Character>();
        for (; f < i;) {
            do c = h[a.charAt(f++) & 255];
            while (f < i && -1 == c);
            if (-1 == c) break;
            do b = h[a.charAt(f++) & 255];
            while (f < i && -1 == b);
            if (-1 == b) break;
            d.add((char) (c << 2 | (b & 48) >> 4));
            do {
                c = a.charAt(f++) & 255;
                if (61 == c)
                    return convertString(d);
                c = h[c];
            } while (f < i && -1 == c);
            if (-1 == c) break;
            d.add((char) ((b & 15) << 4 | (c & 60) >> 2));
            do {
                b = a.charAt(f++) & 255;
                if (61 == b)
                    return convertString(d);
                b = h[b];
            } while (f < i && -1 == b);
            if (-1 == b) break;
            d.add((char) ((c & 3) << 6 | b));
        }
        return convertString(d);
    }

    private String encode64(String a) {
        if (a == null || a.length() == 0)
            return "";
        a = a.toString();
        int f, g;
        String h = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";
        int d = a.length();
        int c = 0;
        int e;
        String b = "";
        for (; d > c; ) {
            e = 255 & (int)a.charAt(c++);
            if ( c == d) {
                b += h.charAt(e >> 2);
                b += h.charAt((3 & e) << 4);
                b += "==";
                break;
            }
            f = (int)a.charAt(c++);
            if (c == d) {
                b += h.charAt(e >> 2);
                b += h.charAt((3 & e) << 4 | (240 & f) >> 4);
                b += h.charAt((15 & f) << 2);
                b += "=";
                break;
            }
            g = (int)a.charAt(c++);
            b += h.charAt(e >> 2);
            b += h.charAt((3 & e) << 4 | (240 & f) >> 4);
            b += h.charAt((15 & f) << 2 | (192 & g) >> 6);
            b += h.charAt(63 & g);
        }
        return b;
    }

    private String na(String a) {
        if (a == null || a.length() == 0)
            return "";
        int c, b;
        int[] h = new int[]{-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 62, -1, -1, -1, 63, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, -1, -1, -1, -1, -1, -1, -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, -1, -1, -1, -1, -1, -1, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, -1, -1, -1, -1, -1};
        int i = a.length();
        int f = 0;
        List<Character> d = new ArrayList<Character>();
        for (; f < i;) {
            do c = h[a.charAt(f++) & 255];
            while (f < i && -1 == c);
            if (-1 == c) break;
            do b = h[a.charAt(f++) & 255];
            while (f < i && -1 == b);
            if (-1 == b) break;
            d.add((char) (c << 2 | (b & 48) >> 4));
            do {
                c = a.charAt(f++) & 255;
                if (61 == c)
                    return convertString(d);
                c = h[c];
            } while (f < i && -1 == c);
            if (-1 == c) break;
            d.add((char) ((b & 15) << 4 | (c & 60) >> 2));
            do {
                b = a.charAt(f++) & 255;
                if (61 == b)
                    return convertString(d);
                b = h[b];
            } while (f < i && -1 == b);
            if (-1 == b) break;
            d.add((char) ((c & 3) << 6 | b));
        }
        return convertString(d);
    }

    private String D(String a) {
        if(a == null || a.length() == 0)
            return "";
        String b = "";
        int d, g, h;
        int f = a.length();
        int e = 0;
        for (; e < f;) {
            d = a.charAt(e++) & 255;
            if (e == f) {
                b += "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/".charAt(d >> 2);
                b += "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/".charAt((d & 3) << 4);
                b += "==";
                break;
            }
            g = a.charAt(e++);
            if (e == f) {
                b += "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/".charAt(d >> 2);
                b += "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/".charAt((d & 3) << 4 | (g & 240) >> 4);
                b += "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/".charAt((g & 15) << 2);
                b += "=";
                break;
            }
            h = a.charAt(e++);
            b += "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/".charAt(d >> 2);
            b += "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/".charAt((d & 3) << 4 | (g & 240) >> 4);
            b += "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/".charAt((g & 15) << 2 | (h & 192) >> 6);
            b += "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/".charAt(h & 63);
        }
        return b;
    }

    private String translate(String a) {//, int[] b
        int[] b = {19, 1, 4, 7, 30, 14, 28, 8, 24, 17, 6, 35, 34, 16, 9, 10, 13, 22, 32, 29, 31, 21, 18, 3, 2, 23, 25, 27, 11, 20, 5, 15, 12, 0, 33, 26};
        //for(var c=[],d=0;d<a.length;d++){var e=0;e=a[d]>="a"&&a[d]<="z"?a[d].charCodeAt(0)-"a".charCodeAt(0):a[d]-"0"+26;for(var f=0;36>f;f++)if(b[f]==e){e=f;break}e>25?c[d]=e-26:c[d]=String.fromCharCode(e+97)}return c.join("")

        Character[] c = new Character[a.length()];
        for(int d=0;d < a.length();d++){
            int e = 0;
            e = a.charAt(d) >= 'a' && a.charAt(d) <= 'z' ? a.charAt(d) - 'a' : a.charAt(d) - '0' + 26;
            for(int f=0; 36 > f; f++){
                if(b[f]==e){
                    e=f;
                    break;
                }
            }
            if(e>25){
                c[d] = new Character(String.valueOf(e - 26).charAt(0));
            }
            else {
                int val = e + 97;
                c[d] = new Character((char)val);
            }

        }
        //return c.join("");

        String result = "";
        for (Character ch: c) {
            result = result + ch.toString();
        }

        return result;

//        List<Character> b = new ArrayList<Character>();
//        int f = 0;
//        int h = 0;
//        for (; 256 > h; h++)
//            b.add((char) h);
//        for (h = 0; 256 > h; h++) {
//            f = (f + b.get(h) + a.charAt(h % a.length())) % 256;
//            Character i = b.get(h);
//            b.set(h, b.get(f));
//            b.set(f, i);
//        }
//        List<Character> d = new ArrayList<Character>();
//        for (int q = f = h = 0; q < c.length(); q++) {
//            h = (h + 1) % 256;
//            f = (f + b.get(h)) % 256;
//            Character i = b.get(h);
//            b.set(h, b.get(f));
//            b.set(f, i);
//            d.add((char) (c.charAt(q) ^ b.get((b.get(h) + b.get(f)) % 256)));
//        }
//        return convertString(d);
    }

    //E=rc4
    private String rc4(String a, String c) {
        List<Character> b = new ArrayList<Character>();
        int f = 0;
        int h = 0;
        for (; 256 > h; h++)
            b.add((char) h);
        for (h = 0; 256 > h; h++) {
            f = (f + b.get(h) + a.charAt(h % a.length())) % 256;
            Character i = b.get(h);
            b.set(h, b.get(f));
            b.set(f, i);
        }
        List<Character> d = new ArrayList<Character>();
        for (int q = f = h = 0; q < c.length(); q++) {
            h = (h + 1) % 256;
            f = (f + b.get(h)) % 256;
            Character i = b.get(h);
            b.set(h, b.get(f));
            b.set(f, i);
            d.add((char) (c.charAt(q) ^ b.get((b.get(h) + b.get(f)) % 256)));
        }
        return convertString(d);
    }

    private String E(String a, String c) {
        List<Character> b = new ArrayList<Character>();
        int f = 0;
        int h = 0;
        for (; 256 > h; h++)
            b.add((char) h);
        for (h = 0; 256 > h; h++) {
            f = (f + b.get(h) + a.charAt(h % a.length())) % 256;
            Character i = b.get(h);
            b.set(h, b.get(f));
            b.set(f, i);
        }
        List<Character> d = new ArrayList<Character>();
        for (int q = f = h = 0; q < c.length(); q++) {
            h = (h + 1) % 256;
            f = (f + b.get(h)) % 256;
            Character i = b.get(h);
            b.set(h, b.get(f));
            b.set(f, i);
            d.add((char) (c.charAt(q) ^ b.get((b.get(h) + b.get(f)) % 256)));
        }
        return convertString(d);
    }

    private String getFileID(String fileid, double seed) {
        String mixed = getFileIDMixString(seed);
        String[] ids = fileid.split("\\*");
        StringBuilder realId = new StringBuilder();
        int idx;
        for (int i = 0; i < ids.length; i++) {
            idx = Integer.valueOf(ids[i]);
            realId.append(mixed.toCharArray()[idx]);
        }
        return realId.toString();
    }

    private String getFileIDMixString(double seed) {
        StringBuilder mixed = new StringBuilder();
        StringBuilder source = new StringBuilder("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ/\\:._-1234567890");
        int index, len = source.length();
        for (int i = 0; i < len; ++i) {
            seed = (seed * 211 + 30031) % 65536;
            index = (int)Math.floor(seed / 65536 * source.length());
            mixed.append(source.toString().toCharArray()[index]);
            source.delete(index,index+ 1);
        }
        return mixed.toString();
    }
}