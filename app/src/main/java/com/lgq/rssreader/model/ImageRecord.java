package com.lgq.rssreader.model;

import com.orm.SugarRecord;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by redel on 2015-09-12.
 */
public class ImageRecord  implements Serializable,Comparable<ImageRecord> {
    private int imageRecordId;
    private String blogId;
    private String originUrl;
    private String storedName;
    private String extension;
    private Date timeStamp;
    private double size;

    public int getImageRecordId(){
        return imageRecordId;
    }

    public String getBlogId(){
        return blogId;
    }

    public String getOriginUrl(){
        return originUrl;
    }

    public String getStoredName(){
        return storedName;
    }

    public String getExtension(){
        return extension;
    }

    public Date getTimeStamp(){
        return timeStamp;
    }

    public double getSize(){
        return size;
    }

    public void setImageRecordId(int imageRecordId) {
        this.imageRecordId = imageRecordId;
    }

    public void setBlogId(String blogId) {
        this.blogId = blogId;
    }

    public void setOriginUrl(String originUrl) {
        this.originUrl = originUrl;
    }

    public void setStoredName(String storedName) {
        this.storedName = storedName;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public void setTimeStamp(Date timeStamp) {
        this.timeStamp = timeStamp;
    }

    public void setSize(double size) {
        this.size = size;
    }

    public ImageRecord(){

    }

    public ImageRecord(int imageRecordId,String blogId, String originUrl, String storedName, String extension, Date timeStamp, double size){
        this.imageRecordId = imageRecordId;
        this.originUrl = originUrl;
        this.storedName = storedName;
        this.extension = extension;
        this.timeStamp = timeStamp;
        this.size = size;
        this.blogId = blogId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ImageRecord that = (ImageRecord) o;

        if (imageRecordId != that.imageRecordId) return false;
        if (Double.compare(that.size, size) != 0) return false;
        if (blogId != null ? !blogId.equals(that.blogId) : that.blogId != null) return false;
        if (originUrl != null ? !originUrl.equals(that.originUrl) : that.originUrl != null)
            return false;
        if (storedName != null ? !storedName.equals(that.storedName) : that.storedName != null)
            return false;
        if (extension != null ? !extension.equals(that.extension) : that.extension != null)
            return false;
        return !(timeStamp != null ? !timeStamp.equals(that.timeStamp) : that.timeStamp != null);

    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = imageRecordId;
        result = 31 * result + (blogId != null ? blogId.hashCode() : 0);
        result = 31 * result + (originUrl != null ? originUrl.hashCode() : 0);
        result = 31 * result + (storedName != null ? storedName.hashCode() : 0);
        result = 31 * result + (extension != null ? extension.hashCode() : 0);
        result = 31 * result + (timeStamp != null ? timeStamp.hashCode() : 0);
        temp = Double.doubleToLongBits(size);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    @Override
    public int compareTo(ImageRecord another) {
        return this.getImageRecordId() - another.getImageRecordId();
    }
}
