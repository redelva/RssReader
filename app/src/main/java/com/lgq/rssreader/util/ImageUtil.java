//package com.lgq.rssreader.util;
//
//import android.graphics.drawable.Drawable;
//
//import com.lgq.rssreader.model.Blog;
//import com.lgq.rssreader.model.ImageRecord;
//
//import java.io.File;
//import java.util.Date;
//import java.util.UUID;
//
///**
// * Created by redel on 2015-10-03.
// */
//public class ImageUtil {
//    public static ImageRecord loadDrawable(final Blog blog, final String imageUrl) {
//        if (imageUrl.trim().equals("")) {
//            return null;
//        }
//
//        final String folder = ImageCacher.GetImageFolder(blog);
//        final String originName = imageUrl.substring(imageUrl.lastIndexOf("/") + 1);
//        final String extension = originName.split("[.]").length > 1 ? originName.split("[.]")[1] : "";
//        final String storeName = folder + UUID.randomUUID().toString();
//
//        ImageRecord record;
//        final ImageRecordDalHelper recordHelper = new ImageRecordDalHelper();
//
//        if(recordHelper.Exist(imageUrl)){
//            record = recordHelper.GetImageRecordEntity(imageUrl);
//            File file = new File(record.StoredName);
//            if (file.exists()) {
//                recordHelper.Close();
//                return record;
//            }
//        }else{
//            Drawable drawable = NetHelper.loadImageFromUrlWithStore(storeName, imageUrl);
//
//            record = new ImageRecord();
//            record.setExtension(extension);
//            record.setOriginUrl(imageUrl);
//            record.setBlogId(blog.getBlogId());
//            record.setStoredName(storeName);
//            record.setTimeStamp(new Date());
//            record.setSize(FileUtil.getFileLength(storeName));
//
//            recordHelper.SynchronyData2DB(record);
//
//            if (drawable != null) {
//                recordHelper.Close();
//                return record;
//            }
//        }
//        recordHelper.Close();
//        return record;
//    }
//}
