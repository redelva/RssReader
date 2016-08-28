package com.lgq.rssreader.formatter;

import com.lgq.rssreader.model.Blog;
import com.lgq.rssreader.readability.Readability;
import com.lgq.rssreader.util.HtmlUtil;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.util.List;
import java.util.zip.GZIPInputStream;

/**
 * Created by redel on 2015-10-17.
 */
public class ContentFormatter extends BlogFormatter {
    @Override
    protected String LoadFromCache(Blog blog) {
        if (blog.getContent() == null) {
            return "";
        }

        if (blog.getContent().length() == 0) {
            return "";
        }

        if (blog.getContent().contains("embed")) {
            return "";
        }

        Document doc = Jsoup.parse(blog.getContent());

        Elements imgs = doc.getElementsByTag("img");

        for (int i = 0, len = imgs.size(); i < len; i++) {
            if (imgs.get(i).hasAttr("src") && imgs.get(i).attr("src").startsWith(prefix))
                return "";
        }
        return blog.getContent();
    }

    @Override
    protected String Download(final Blog blog) {
        if (blog.getContent() == null || blog.getContent().length() == 0 || blog.getContent().contains("embed")) {
            String content = downloadGbUrl(blog.getLink(), "utf-8");

            if(content.length() == 0){
                return "";
            }

            int index = content.indexOf("body");

            String body = content.substring(0, index) + "<body></body></html>";

            Document doc = Jsoup.parse(body);

            for (int j = 0, length = doc.getElementsByTag("meta").size(); j < length; j++) {
                Element meta = doc.getElementsByTag("meta").get(j);
                if (meta.outerHtml().contains("charset")) {
                    String[] pairs = meta.outerHtml().split(" ");
                    for (int i = 0, len = pairs.length; i < len; i++) {
                        String p = pairs[i].toLowerCase();
                        if (p.contains("charset") && p.contains("gbk")) {
                            content = downloadGbUrl(blog.getLink(), "GBK");
                            break;
                        }
                        if (p.contains("charset") && p.contains("gb2312")) {
                            content = downloadGbUrl(blog.getLink(), "gb2312");//CharHelper.change(new String(content.getBytes("utf-8"), "gb2312"), "gb2312", "UTF-8");//new String(content.getBytes("UTF-8"), "gb2312");;
                            break;
                        }
                    }
                }
            }

            return HtmlUtil.unescape(content);
        } else if (blog.getContent().length() != 0) {
            return blog.getContent();
        }else{
            return "";
        }
    }

    private String downloadGbUrl(String url, String coding){
        try{
            URL u = new URL(url);
            URLConnection conn = u.openConnection();
            conn.setRequestProperty("Accept-Encoding", "gzip");
            //建立连接
            conn.connect();

            Reader reader = null;
            if ("gzip".equals(conn.getContentEncoding())) {
                reader = new InputStreamReader(new GZIPInputStream(conn.getInputStream()), coding);
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

        return readability.Content;
    }
}
