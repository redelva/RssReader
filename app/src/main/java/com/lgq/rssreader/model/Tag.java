package com.lgq.rssreader.model;

/**
 * Created by redel on 2015-09-06.
 */
public class Tag {
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
    public String toString() {
        return "ID:" + id + " Label:" + label + " SortID:" + sortId;
    }
}
