package com.lgq.rssreader.formatter;

import android.os.Environment;

import com.lgq.rssreader.model.Blog;
import com.lgq.rssreader.model.ImageRecord;
import com.lgq.rssreader.readability.Readability;
import com.lgq.rssreader.util.HtmlUtil;
import com.lgq.rssreader.util.ImageUtil;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.zip.GZIPInputStream;

/**
 * Created by redel on 2015-10-17.
 */
public class DownloadDescriptionFormatter extends BlogFormatter {
    private static final String imageData = "data:image/gif;base64,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";

    @Override
    protected String LoadFromCache(Blog blog) {
        if (blog != null && blog.getDescription().length() == 0) {
            return "";
        }

        Document doc = Jsoup.parse(blog.getDescription());

        List<Element> embeds = doc.getElementsByTag("embed");

        for(Element d : doc.getElementsByTag("iframe")){
            if(d.hasAttr("src")&&
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

        for(Element d : doc.getElementsByTag("a")){
            if(d.hasAttr("href")&&
                    (
                            d.attr("href").contains("swf") ||
                                    d.attr("href").contains("youku") ||
                                    d.attr("href").contains("sohu") ||
                                    d.attr("href").contains("tudou") ||
                                    d.attr("href").contains("youtube") ||
                                    d.attr("href").contains("ku6")
                    )
                    )
                embeds.add(d);
        }

        if (embeds.size() != 0)
            return "";

        for(Element img : doc.getElementsByTag("img")){
            if(img.hasAttr("src") && !img.attr("src").startsWith(prefix)){
                return "";
            }
        }

        return blog.getDescription();
    }

    @Override
    protected String Download(final Blog blog) {
        return downloadDescription(blog);
    }

    private String downloadDescription(final Blog blog){
        String content = blog.getDescription();

        if(content.length() == 0){
            return "";
        }

        Document doc = Jsoup.parse(content);

        Elements imgs = doc.getElementsByTag("img");

        for (int i = 0, len = imgs.size(); i < len; i++) {
            //if (imgs.get(i).hasAttr("src") && imgs.get(i).attr("src").startsWith(prefix))
            if (imgs.get(i).hasAttr("src") && !imgs.get(i).hasAttr("xsrc")){
                ImageRecord record = ImageUtil.saveImage(blog, imgs.get(i).attr("src"));

                if(record != null){
                    imgs.get(i).attr("src", imageData);
                    imgs.get(i).attr("xSrc", "file://" + Environment.getExternalStorageDirectory().getAbsolutePath().toString() + record.getStoredName());
                }
            }
        }

        return HtmlUtil.unescape(doc.body().html());
    }

    private String downloadUrl(String url, String coding){
        try{
            URL u = new URL(url);
            URLConnection conn = u.openConnection();
            conn.setRequestProperty("Accept-Encoding", "gzip");
            //建立连接
            conn.connect();

            Reader reader = null;
            if ("gzip".equals(conn.getContentEncoding())) {
                reader = new InputStreamReader(new GZIPInputStream(conn.getInputStream()));
            }
            else {
                reader = new InputStreamReader(conn.getInputStream(), coding);
            }

            BufferedReader br = new BufferedReader(reader);
            String buf = "";
            StringBuilder content = new StringBuilder();
            while((buf = br.readLine()) != null)
            {
                content.append(buf);
            }
            //转换编码
            return new String(content.toString().getBytes("UTF-8"));
        }catch(IOException e){
            e.printStackTrace();
            return "";
        }
    }

    @Override
    protected String GetReadableString(String content) {
        Readability readability = Readability.Create(content);

        if(readability.Content != null && HtmlUtil.trim(HtmlUtil.filterHtml(readability.Content)).replace(" ","").length() == 0){
            return content;
        }

        return readability.Content;
    }
}
