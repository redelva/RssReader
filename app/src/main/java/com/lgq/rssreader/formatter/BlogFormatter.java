package com.lgq.rssreader.formatter;

import android.util.Log;

import com.lgq.rssreader.R;
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
import java.util.List;
import java.util.regex.Pattern;

public abstract class BlogFormatter
{
    public static final String prefix = "/images";
    private static final String imageData = "data:image/gif;base64,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";
//    private static final String imageData = "data:image/gif;base64,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";

    public String BackgroundColor;
    public String FontColor;
    public boolean EnableCache;
    public boolean NoImageMode;

    protected abstract String LoadFromCache(Blog blog);
    protected abstract String Download(Blog blog);
    protected abstract String GetReadableString(String content);

    public interface FlashCompleteHandler{
        public void onFlash(Object sender, CacheEventArgs e);
    }

    protected FlashCompleteHandler FlashComplete;

    public void setFlashCompleteHandler(FlashCompleteHandler handler){
        this.FlashComplete = handler;
    }

    public String render(final Blog blog) {
        String content = LoadFromCache(blog);
        if (content.length() == 0) {
            content = Download(blog);
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
                    d.attr("src").contains("youtube") ||
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
            List<Element> links = new ArrayList<Element>();
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

                new Runnable(){
                    @Override
                    public void run() {
                        parseFlash(tmp, blog, embeds.get(tmp).clone(), tip.clone(), src);
                    }
                }.run();
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
                    img.attributes().put("xSrc", img.attr("src"));
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

    private void youku(final int cnt, final Blog blog, final Element embed, final Element tip, final String vurl){
        int index = vurl.indexOf('X');
        if (index == -1)
            return;

        try {
            String id = vurl.substring(index, index + 15);
            String content = RssHttpClient.cleanget("http://v.youku.com/player/getPlayList/VideoIDS/" + id + "/Pf/4/ctype/12/ev/1");

            JSONObject youku = new JSONObject(content);
            if (youku.getJSONArray("data").getJSONObject(0).getJSONObject("segs").getJSONArray("3gphd") != null) {
                String ip = youku.getJSONArray("data").getJSONObject(0).getString("ip");
                int h = 1;
                String q = "mp4";
                double seed = youku.getJSONArray("data").getJSONObject(0).getDouble("seed");
                String fileid = youku.getJSONArray("data").getJSONObject(0).getJSONObject("streamfileids").getString("3gphd");
                String f = getFileID(fileid, seed);
                String sidAndtoken = E("becaf9be", na(youku.getJSONArray("data").getJSONObject(0).getString("ep")));
                String sid = sidAndtoken.split("_")[0];
                String token = sidAndtoken.split("_")[1];

                tip.html("");
                for (int i = 0, len = youku.getJSONArray("data").getJSONObject(0).getJSONObject("segs").getJSONArray("3gphd").length(); i < len; i++) {
                    JSONObject child = youku.getJSONArray("data").getJSONObject(0).getJSONObject("segs").getJSONArray("3gphd").getJSONObject(i);
                    String k = child.getString("k");
                    String l = child.getString("seconds");
                    //String k = child.getString("k");
                    String k2 = child.getString("k2");
                    //String indexFileId = fileId.Insert(9, i.ToString()).Remove(10);

                    f = f.substring(0, 9) + String.valueOf(i) + f.substring(10);

                    String url = "/player/getFlvPath/sid/" + sid + "_" + "00" + "/st/" + q + "/fileid/" + f + "?K=" + k + "&hd=" + h + "&myp=0&ts=" + l + "&ypp=0";// +e;
                    f = HtmlUtil.UrlEncodeUpper(D(E("bf7e5f01", sid + "_" + f + "_" + token)));
                    url = url + ("&ep=" + f) + "&ctype=12&ev=1" + ("&token=" + token);
                    url += "&oip=" + ip;
                    url = "http://k.youku.com" + url;

                    if (FlashComplete != null) {
                        tip.html(tip.html() + url + "|");
                    }
                }

                tip.html(tip.html().substring(0, tip.html().length() - 1) + "____" + getYoukuImage(id));
                        //youku.getJSONArray("data").getJSONObject(0).getString("logo") + "/" + youku.getJSONArray("data").getJSONObject(0).getJSONObject//("preview").getJSONArray("thumbs").get(0).toString());
                        FlashComplete.onFlash(youku.getJSONArray("data").getJSONObject(0).getString("title"), new CacheEventArgs(blog, embed, tip, cnt, 0));
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private String getYoukuImage(String id){

        String content = RssHttpClient.cleanget("http://play.youku.com/play/get.json?vid=" + id + "&ct=12");

        try {
            JSONObject youku = new JSONObject(content);

            return youku.getJSONObject("data").getJSONObject("video").get("logo").toString();
        } catch (Exception e) {
            return "";
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

            String content = RssHttpClient.cleanget("http://my.tv.sohu.com/videinfo.jhtml?m=viewtv&vid=" + vid);
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
//        AsyncHttpClient xml = new AsyncHttpClient();
//        xml.setUserAgent("Mozilla/5.0 (Linux; U; Android 4.1.1; en-us; MI 2S Build/JRO03L) AppleWebKit/534.30 (KHTML, like Gecko) Version/4.0 Mobile Safari/534.30");
//        xml.get(url, new AsyncHttpResponseHandler (){
//            @Override
//            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable t){
//                String error = new String(responseBody);
//
//                String url = "";
//                if(t.getCause() != null){
//                    if(t.getCause().getCause() != null){
//                        url = t.getCause().getCause().getMessage();
//                    }
//                }
//
//                String iid = UrlUtil.findValueInUrl(url, "iid");
//                final String title = HtmlUtil.unescape(UrlUtil.findValueInUrl(url, "title"));
//                final String coverImg = HtmlUtil.unescape(UrlUtil.findValueInUrl(url, "snap_pic"));
//
//                if(iid.length() > 0){
//
//                    tip.html("http://vr.tudou.com/v2proxy/v2?it=" + iid + "&st=52&pw=____" + coverImg);
//                    FlashComplete.onFlash(title, new CacheEventArgs(blog, embed, tip, cnt, 0));
//                }else{
//                    tip.html("");
//                    FlashComplete.onFlash(ReaderApp.getContext().getResources().getString(R.string.blog_videooptimize), new CacheEventArgs(blog, embed, tip, cnt, -1));
//                }
//
//                Log.i("RssReader", url);
//            }
//
//            @Override
//            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody){
//                String result = new String(responseBody);
//                Document doc = Jsoup.parse(result);
//
//                Element video = null;
//
//                for(int i=0, len=doc.getAllElements().size(); i<len; i++){
//                    if(doc.getAllElements().get(i).nodeName().toLowerCase() == "video"){
//                        video =doc.getAllElements().get(i) ;
//                    }
//                }
//
//                if (FlashComplete != null)
//                {
//                    tip.html(video.html());
//                    FlashComplete.onFlash(blog, new CacheEventArgs(blog, embed, tip, 0, 0));
//                }
//            }
//        });
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

        String root = RssHttpClient.cleanget("http://vv.video.qq.com/geturl?vid=" + vid + "&otype=json&platform=1&ran=0%2E9652906153351068");

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

//        private void processTudou(String iid, final Blog blog, final Element embed, final Element tip)
//        {
//            AsyncHttpClient xml = new AsyncHttpClient();
//            xml.setUserAgent("Mozilla/5.0(iPad; U; CPU iPhone OS 3_2 like Mac OS X; en-us) AppleWebKit/531.21.10 (KHTML, like Gecko) Version/4.0.4 Mobile/7B314 Safari/531.21.10");
//            xml.get("http://v2.tudou.com/v?vn=02&st=1%2C2&it=" + iid, new AsyncHttpResponseHandler(){
//            	public void onSuccess(String response){
//            		 XElement root = XElement.Parse(eventArgs.Result);
//
//                     if (FlashComplete != null){
//                         tip.html();
//                         if(FlashComplete != null)
//                         FlashComplete.onFlash(blog, new CacheEventArgs(blog, embed, tip, 0, 0));
//                     }
//            	}
//            });           
//        }

    private String convertString(List<Character> array){
        StringBuilder s = new StringBuilder();
        for(Character i : array)
            s.append(i);

        return s.toString();
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