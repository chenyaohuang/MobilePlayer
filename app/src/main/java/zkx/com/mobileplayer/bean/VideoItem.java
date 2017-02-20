package zkx.com.mobileplayer.bean;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;
import android.provider.MediaStore.Video.Media;


import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by zhang on 2016/7/31.
 */
public class VideoItem implements Serializable{
    //    private static ArrayList<VideoItem> list;
    private String title;
    private int duration;
    private int size;
    private String path;


    /**
     * 从cursor生成一个VideoItem对象
     */
    public static VideoItem instanceFromCursor(Cursor cursor){
        VideoItem videoItem = new VideoItem();
        if(cursor==null||cursor.getCount()==0){
            return videoItem;
        }
        //解析cursor的内容
        videoItem.title = cursor.getString(cursor.getColumnIndex(Media.TITLE));
        videoItem.duration = cursor.getInt(cursor.getColumnIndex(Media.DURATION));
        videoItem.size = cursor.getInt(cursor.getColumnIndex(Media.SIZE));
        videoItem.path = cursor.getString(cursor.getColumnIndex(Media.DATA));
//        videoItem.bitmap =  ThumbnailUtils.createVideoThumbnail(videoItem.path, MediaStore.Video.Thumbnails.MICRO_KIND);
//        videoItem.bitmap = ThumbnailUtils.extractThumbnail(videoItem.bitmap, 100, 100);
        return videoItem;
    }

    /**
     * 从Cursor中解析完整的播放列表
     *
     * @param cursor
     * @return
     */
    public static ArrayList<VideoItem> instanceListFromCursor(Cursor cursor) {
        ArrayList<VideoItem> videoItems = new ArrayList<>();
        if (cursor == null || cursor.getCount() == 0) {
            return videoItems;
        }

        cursor.moveToPosition(-1);
        while (cursor.moveToNext()) {
            VideoItem videoItem = instanceFromCursor(cursor);
            videoItems.add(videoItem);
        }
        return videoItems;
    }

    /**
     * 从cursor生成多个VideoItem对象
     */

//    public static ArrayList<VideoItem> instanceFromCursor2(Cursor cursor){
//        for(int i=0 ;i<6;i++){
//            VideoItem videoItem = new VideoItem();
//            if(cursor==null||cursor.getCount()==0){
//               return list;
//            }
//
//
//                //解析cursor的内容
//                videoItem.title = cursor.getString(cursor.getColumnIndex(Media.TITLE));
//                videoItem.duration = cursor.getInt(cursor.getColumnIndex(Media.DURATION));
//                videoItem.size = cursor.getInt(cursor.getColumnIndex(Media.SIZE));
//                videoItem.path = cursor.getString(cursor.getColumnIndex(Media.DATA));
//                videoItem.bitmap = ThumbnailUtils.createVideoThumbnail(videoItem.path, MediaStore.Video.Thumbnails.MICRO_KIND);
//                videoItem.bitmap = ThumbnailUtils.extractThumbnail(videoItem.bitmap, 100, 100);
//                list.add(videoItem);
//                cursor.moveToNext();
//
//
//        }
//
//        return list;
//    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public String toString() {
        return "ViewItem{" +
                "title='" + title + '\'' +
                ", duration=" + duration +
                ", size=" + size +
                ", path='" + path + '\'' +
                '}';
    }

//    public Bitmap getBitmap() {
//        return bitmap;
//    }
//
//    public void setBitmap(Bitmap bitmap) {
//        this.bitmap = bitmap;
//    }
}
