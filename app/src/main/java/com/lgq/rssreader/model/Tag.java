package com.lgq.rssreader.model;

import java.io.Serializable;

/**
 * Created by redel on 2015-09-06.
 */
public class Tag implements  Serializable,Comparable<Tag> {
    private final String id ;
    private final String label;
    private final String sortId;

    public String getId(){
        return id;
    }

    public String getLabel(){
        return label;
    }

    public String getSortId(){
        return sortId;
    }

    public Tag(String id, String label, String sortId){
        this.id = id;
        this.label = label;
        this.sortId = sortId;
    }

    @Override
    public boolean equals(Object t){
        if(t == null){
            return false;
        }

        if(!(t instanceof Tag)){
            return false;
        }

        Tag tmp = (Tag)t;

        return id.equals(tmp.getId());
    }

    @Override
    public int hashCode(){
        return id.hashCode();
    }

    @Override
    public int compareTo(Tag arg0) {
        return id.compareTo(arg0.getId());
    }

    @Override
    public String toString() {
        return "ID:" + id + " Label:" + label + " SortID:" + sortId;
    }
}
