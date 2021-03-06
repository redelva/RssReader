package com.lgq.rssreader.model;

/**
 * Created by redel on 2015-09-04.
 */

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

public class Channel implements Serializable,Comparable<Channel> {

    private static final long serialVersionUID = 8836207717484134876L;

    public static String TOKEN = "Channel";

    final String id;
    final String title;
    final String sortId;
    final Date lastUpdateTime;
    final Date lastRefreshTime;
    final int unreadCount;
    final boolean isDirectory;
    final String folder;
    final String favIcon;
    final ArrayList<Channel> children;
    final Object tag;

    public String getId(){
        return id;
    }

    public String getTitle(){
        return title;
    }

    public String getSortId(){
        return sortId;
    }

    public Date getLastUpdateTime(){
        return lastUpdateTime;
    }

    public Date getLastRefreshTime(){
        return lastRefreshTime;
    }

    public int getUnreadCount(){
        return unreadCount;
    }

    public boolean getIsDirectory(){
        return isDirectory;
    }

    public String getFolder(){
        return folder;
    }

    public String getFavIcon(){
        return favIcon;
    }

    public ArrayList<Channel> getChildren(){
        return children;
    }

    public Object getTag(){
        return tag;
    }

    public Channel(Channel obj)
    {
        this.id = obj.getId();
        this.title = obj.getTitle();
        this.sortId = obj.getSortId();
        this.lastRefreshTime = obj.getLastRefreshTime();
        this.lastUpdateTime = obj.getLastUpdateTime();
        this.unreadCount = obj.getUnreadCount();
        this.isDirectory = obj.getIsDirectory();
        this.favIcon = obj.getFavIcon();
        this.folder = obj.getFolder();
        this.children = new ArrayList<>();
        for(Channel child : obj.getChildren())
        {
            this.children.add(child);
        }
        this.tag = new Object();
    }

    public Channel(String id, String title, String sortId, Date lastUpdateTime, Date lastRefreshTime, int unreadCount,
                   boolean isDirectory, String folder, String favIcon, ArrayList<Channel> children, Object tag) {
        this.id = id;
        this.title = title;
        this.sortId = sortId;
        this.lastRefreshTime = lastRefreshTime;
        this.lastUpdateTime = lastUpdateTime;
        this.unreadCount = unreadCount;
        this.isDirectory = isDirectory;
        this.favIcon = favIcon;
        this.folder = folder;
        this.children = children;
        this.tag = tag;
    }
//
//	public Channel(Parcel source) {
//		Id = source.readString();
//		Title = source.readString();
//		SortId = source.readString();
//		LastUpdateTime = new Date(source.readLong());
//		LastRefreshTime = new Date(source.readLong());
//		UnreadCount = source.readInt();
//		IsDirectory = source.readInt() == 1;
//	    Folder = source.readString();
//	    FavIcon = source.readInt() == 1;
//	    Children = (ArrayList<Channel>)source.readArrayList(Channel.class.getClassLoader());
//	    Object[] objs= ((Object[])source.readArray(Object.class.getClassLoader()));
//	    Tag = objs[0];
//	}

    @Override
    public boolean equals(Object t){
        if(t == null){
            return false;
        }

        if(!(t instanceof Channel)){
            return false;
        }

        Channel tmp = (Channel)t;

        return id.equals(tmp.getId());
    }

    @Override
    public int hashCode(){
        return id.hashCode();
    }

    @Override
    public int compareTo(Channel arg0) {
        return unreadCount - arg0.getUnreadCount();
    }

//	@Override
//	public int describeContents() {
//		// TODO Auto-generated method stub
//		return 0;
//	}
//
//	@Override
//	public void writeToParcel(Parcel dest, int flags) {
//		dest.writeString(Id);
//		dest.writeString(Title);
//		dest.writeString(SortId);
//		dest.writeLong(LastUpdateTime.getTime());
//		dest.writeLong(LastRefreshTime.getTime());
//		dest.writeInt(UnreadCount);
//		dest.writeInt(IsDirectory ? 1 : 0);
//	    dest.writeString(Folder);
//	    dest.writeInt(FavIcon ? 1: 0);
//	    dest.writeList(Children);
//	    dest.writeArray(new Object[]{Tag});
//	}
//
//	//实例化静态内部对象CREATOR实现接口Parcelable.Creator
//    public static final Parcelable.Creator<Channel> CREATOR = new Creator<Channel>() {
//
//        @Override
//        public Channel[] newArray(int size) {
//            return new Channel[size];
//        }
//
//        //将Parcel对象反序列化为ParcelableDate
//        @Override
//        public Channel createFromParcel(Parcel source) {
//            return new Channel(source);
//        }
//    };
}
