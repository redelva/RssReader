package com.lgq.rssreader.formatter;

import com.lgq.rssreader.model.Blog;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.List;

/**
 * Created by redel on 2015-10-17.
 */
public class DescriptionFormatter extends BlogFormatter {
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
        return blog.getDescription();
    }

    @Override
    protected String GetReadableString(String content) {
        return content.replace("figure", "p");
    }
}
