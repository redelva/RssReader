package com.lgq.rssreader.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by redel on 2015-09-06.
 */
public class Subscription  implements Serializable,Comparable<Subscription> {
    final String id ;
    final String title;
    final List<Tag> categories;
    final String sortId;
    final Date firstItemTime;

    public String getId(){
        return id;
    }

    public String getTitle(){
        return title;
    }

    public List<Tag> getCategories(){
        return categories;
    }

    public String getSortId(){
        return sortId;
    }

    public Date getFirstItemTime(){
        return firstItemTime;
    }

    public Subscription(String id, String title, List<Tag> categories, String sortId, Date firstItemTime){
        this.id = id;
        this.title = title;
        this.categories = categories;
        this.sortId = sortId;
        this.firstItemTime = firstItemTime;
    }

    @Override
    public boolean equals(Object t){
        if(t == null){
            return false;
        }

        if(!(t instanceof Subscription)){
            return false;
        }

        Subscription tmp = (Subscription)t;

        return id.equals(tmp.getId());
    }

    @Override
    public int hashCode(){
        return id.hashCode();
    }

    @Override
    public int compareTo(Subscription arg0) {
        return id.compareTo(arg0.getId());
    }
}
