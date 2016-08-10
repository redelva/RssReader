package com.lgq.rssreader.util;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import com.lgq.rssreader.core.Constant;
import com.lgq.rssreader.model.Blog;
import com.lgq.rssreader.model.Channel;
import com.lgq.rssreader.model.ImageRecord;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Created by redel on 2015-10-03.
 */
public class ImageUtil {

    public static void saveImageToGallery(Context context, Bitmap bmp) {
        // 首先保存图片
        File appDir = new File(Environment.getExternalStorageDirectory(), Constant.IMAGES_LOCATION);
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        String fileName = System.currentTimeMillis() + ".jpg";
        File file = new File(appDir, fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 其次把文件插入到系统图库
        try {
            MediaStore.Images.Media.insertImage(context.getContentResolver(), file.getAbsolutePath(), fileName, null);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        // 最后通知图库更新
        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse(file.getAbsolutePath())));
    }

    public static void saveImage(Context context, Bitmap bmp)  throws IOException{
        File file = createImageFile();

        try {
            FileOutputStream fos = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        addPicToGallery(context, file.getPath());
    }

    public static File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(System.currentTimeMillis());
        File storageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM) + "/RssReader/");
        if (!storageDir.exists())
            storageDir.mkdirs();
        File image = File.createTempFile(
                timeStamp,                   /* prefix */
                ".jpeg",                     /* suffix */
                storageDir                   /* directory */
        );
        return image;
    }

    public static void addPicToGallery(Context context, String photoPath) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(photoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        context.sendBroadcast(mediaScanIntent);
    }

    public static Drawable getUrlDrawable(String url){
        try{
            URL aryURI=new URL(url);
            URLConnection conn=aryURI.openConnection();
            InputStream is=conn.getInputStream();
            Bitmap bmp= BitmapFactory.decodeStream(is);
            return new BitmapDrawable(bmp);
        }catch(Exception e){
            Log.e("ERROR", "urlImage2Drawable failed with image url at " + url, e);
            return null;
        }
    }

    private static byte[] readInputStream(InputStream inStream) throws Exception {
        ByteArrayOutputStream outSteam = new ByteArrayOutputStream();
        byte[] buffer = new byte[4096];
        int len = 0;
        while ((len = inStream.read(buffer)) != -1) {
            outSteam.write(buffer, 0, len);
        }
        outSteam.close();
        inStream.close();
        return outSteam.toByteArray();
    }

    public static Drawable loadImageFromUrl(String folder, String url) {
        try {
            if(url.indexOf("?")>0){
                url=url.substring(0,url.indexOf("?"));
            }
            int index = folder.lastIndexOf("/");
            String fileName = folder.substring(index + 1);
            String path = folder.substring(0, index);
            URL imageUrl = new URL(url);
            byte[] data = readInputStream((InputStream) imageUrl.openStream());
            Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
            String status = Environment.getExternalStorageState();
            if (status.equals(Environment.MEDIA_MOUNTED)) {
                File SDFile = android.os.Environment.getExternalStorageDirectory();
                FileUtil.makeDir(SDFile.getAbsolutePath() + path);
                File img = new File(SDFile.getAbsolutePath() + folder);
                if(!img.exists()){
                    img.createNewFile();
                }
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, new FileOutputStream(img));
                Bitmap bitmapCompress = BitmapFactory.decodeFile(SDFile.getAbsolutePath() + folder);
                return new BitmapDrawable(bitmapCompress);
            }
        } catch (Exception e) {
            Log.e("download_img_err", e.toString());
        }
        return null;
    }

    public static String getImageFolder(Blog blog) {
        String folder = Constant.IMAGES_LOCATION;

        List<Channel> channels = PreferencesUtil.getChannels();

        Channel target = null;
        Channel parent = null;

        for(Channel c : channels){
            if(c.getChannelId().equals(blog.getChannelId()) || c.getChannelId().equals(blog .getTagId())){
                target = c;
                break;
            }
        }

        if(target == null){
            for(Channel c : channels){
                if(c.getIsDirectory() && c.getChildren() != null && c.getChildren().size() != 0){
                    for(Channel child : c.getChildren()){
                        if(child.getTitle().equals(blog.getSubsTitle())){
                            parent = c;
                            target = child;
                            break;
                        }
                    }
                }
            }
        }

        if(target == null ){

            Log.e("RssReader", blog.getTitle() + " " + blog.getChannelId() + " " + blog.getTagId() + " " + blog.getSubsTitle());

            return folder;
        }

        if(parent != null)
            folder = folder + parent.getFolder() + "/" + target.getFolder() + "/";
        else
            folder = folder + target.getFolder() + "/";

        return folder;
    }

    public static ImageRecord saveImage(final Blog blog, final String imageUrl) {
        if (imageUrl.trim().equals("")) {
            return null;
        }

        String image_url = HtmlUtil.extraReplace(imageUrl);

        final String folder = getImageFolder(blog);
        final String originName = image_url.substring(image_url.lastIndexOf("/") + 1);
        final String extension = originName.split("[.]").length > 1 ? originName.split("[.]")[1] : "";
        final String storeName = folder + UUID.randomUUID().toString();

        String[] args = new String[]{image_url};
        List<ImageRecord> records = ImageRecord.find(ImageRecord.class, "origin_url = ?", args);

        if(records != null && records.size() == 1){
            ImageRecord record = records.get(0);
            //File file = new File(record.getStoredName());
            return record;
        }else{
            Drawable drawable = loadImageFromUrl(storeName, image_url);

            if(drawable != null){
                ImageRecord record = new ImageRecord();
                record.setExtension(extension);
                record.setOriginUrl(image_url);
                record.setBlogId(blog.getBlogId());
                record.setStoredName(storeName);
                record.setTimeStamp(new Date());
                record.setSize(FileUtil.getFileLength(storeName));

                record.save();

                return record;
            }

            return null;
        }
    }

    public static Bitmap GetBitmap(String imageUrl){
        Bitmap mBitmap = null;
        try {
            URL url = new URL(imageUrl);
            URLConnection conn=url.openConnection();
            InputStream is = conn.getInputStream();
            mBitmap = BitmapFactory.decodeStream(is);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return mBitmap;
    }

    public static Bitmap DrawableToBitmap(Drawable drawable) {
        try {
            Bitmap bitmap = Bitmap
                    .createBitmap(
                            drawable.getIntrinsicWidth(),
                            drawable.getIntrinsicHeight(),
                            drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                                    : Bitmap.Config.RGB_565);
            Canvas canvas = new Canvas(bitmap);
            // canvas.setBitmap(bitmap);
            drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable
                    .getIntrinsicHeight());
            drawable.draw(canvas);

            return bitmap;
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            return null;
        }
    }
}
