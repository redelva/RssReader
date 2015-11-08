package com.lgq.rssreader.model;


import java.io.Serializable;
import java.util.Date;

public class Blog implements Serializable,Comparable<Blog> {

    private static final long serialVersionUID = 373299365769337131L;

    final String blogId;
    final String tagId;
    final String channelId;
    final String title;
    final String description;
    final String link;
    final Date pubDate;
    final String subsTitle;
    final long timeStamp;
    final boolean isRead;
    final boolean isStarred;
    final String originId;
    final boolean isRecommend;
    final String avatar;
    final String content;

    public String getBlogId(){
        return blogId;
    }

    public String getTagId(){
        return tagId;
    }

    public String getChannelId(){
        return channelId;
    }

    public String getTitle(){
        return title;
    }

    public String getDescription(){
        return description;
    }

    public String getLink(){
        return link;
    }

    public Date getPubDate(){
        return pubDate;
    }

    public String getSubsTitle(){
        return subsTitle;
    }

    public long getTimeStamp(){
        return timeStamp;
    }

    public boolean getIsRead(){
        return isRead;
    }

    public boolean getIsStarred(){
        return isStarred;
    }

    public String getOriginId(){
        return originId;
    }

    public boolean getIsRecommend(){
        return isRecommend;
    }

    public String getAvatar(){
        return avatar;
    }

    public String getContent(){
        return content;
    }

//	public Blog(Parcel source) {
//		BlogId = source.readString();
//		TagId = source.readString();
//		ChannelId = source.readString();
//		Title = source.readString();
//		Description = source.readString();
//		Link = source.readString();
//		PubDate =  new Date(source.readLong());
//		SubsTitle = source.readString();
//		TimeStamp  = source.readLong();
//		IsRead = source.readInt() == 1;
//		IsStarred = source.readInt() == 1;
//		OriginId = source.readString();
//		IsRecommend = source.readInt() == 1;
//		Avatar = source.readString();
//		Content = source.readString();
//	}

    public Blog(String blogId,
            String tagId,
            String channelId,
            String title,
            String description,
            String link,
            Date pubDate,
            String subsTitle,
            long timeStamp,
            boolean isRead,
            boolean isStarred,
            String originId,
            boolean isRecommend,
            String avatar,
            String content) {
        this.blogId = blogId;
        this.tagId = tagId;
        this.channelId = channelId;
        this.title = title;
        this.description = description;
        this.link = link;
        this.pubDate = pubDate;
        this.subsTitle = subsTitle;
        this.timeStamp = timeStamp;
        this.isRead = isRead;
        this.isStarred = isStarred;
        this.originId = originId;
        this.isRecommend = isRecommend;
        this.avatar = avatar;
        this.content = content;
    }

    @Override
    public boolean equals(Object t){
        if(t == null){
            return false;
        }

        if(!(t instanceof Blog)){
            return false;
        }

        Blog tmp = (Blog)t;

        return blogId.equals(tmp.getBlogId());
    }

    @Override
    public int hashCode(){
        return blogId.hashCode();
    }

    @Override
    public int compareTo(Blog arg0) {
        return (int) (pubDate.getTime() - arg0.getPubDate().getTime() + timeStamp - arg0.getTimeStamp());
    }



//	public static final Parcelable.Creator<Blog> CREATOR = new Creator<Blog>() {
//        @Override
//        public Blog[] newArray(int size) {
//            return new Blog[size];
//        }
//
//        //将Parcel对象反序列化为ParcelableDate
//        @Override
//        public Blog createFromParcel(Parcel source) {
//            return new Blog(source);
//        }
//    };
//
//	@Override
//	public int describeContents() {
//		// TODO Auto-generated method stub
//		return 0;
//	}
//
//	@Override
//	public void writeToParcel(Parcel dest, int flags) {
//		dest.writeString(BlogId);
//		dest.writeString(TagId);
//		dest.writeString(ChannelId);
//		dest.writeString(Title);
//		dest.writeString(Description);
//		dest.writeString(Link);
//		dest.writeLong(PubDate.getTime());
//		dest.writeString(SubsTitle);
//		dest.writeLong(TimeStamp);
//		dest.writeInt(IsRead ? 1 : 0);
//		dest.writeInt(IsStarred ? 1 : 0);
//		dest.writeString(OriginId);
//		dest.writeInt(IsRecommend ? 1 : 0);
//		dest.writeString(Avatar);
//		dest.writeString(Content);
//	}
}

