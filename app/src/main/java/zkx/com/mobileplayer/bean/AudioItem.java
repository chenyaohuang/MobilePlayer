package zkx.com.mobileplayer.bean;

import android.database.Cursor;
import android.provider.MediaStore.Audio.Media;

import java.io.Serializable;
import java.util.ArrayList;

import zkx.com.mobileplayer.util.StringUtils;

/**
 * Created by zhang on 2016/10/17.
 */
public class AudioItem implements Serializable {
    private String title;//音频展示名
    private String artist;//艺术家
    private String path;//音频存储路径


    /**
     * 从cursor生成一个MusicItem对象
     */
    public static AudioItem instanceFromCursor(Cursor cursor) {
        AudioItem audioItem = new AudioItem();
        if (cursor == null || cursor.getCount() == 0) {
            return audioItem;
        }

        //解析cursor的内容
        audioItem.title = cursor.getString(cursor.getColumnIndex(Media.DISPLAY_NAME));
        audioItem.title = StringUtils.formatDispalyName(audioItem.title);
        audioItem.artist = cursor.getString(cursor.getColumnIndex(Media.ARTIST));
        audioItem.path = cursor.getString(cursor.getColumnIndex(Media.DATA));
        return audioItem;
    }

    public static ArrayList<AudioItem> instanceListFromCursor(Cursor cursor) {
        ArrayList<AudioItem> audioItems = new ArrayList<>();
        if (cursor == null || cursor.getCount() == 0) {
            return audioItems;
        }

        //把cursor移动-1的位置(如果不这样做,那么cursor解析列表会不完整,直接从第2项开始)
        cursor.moveToPosition(-1);
        while (cursor.moveToNext()) {
            AudioItem audioItem = instanceFromCursor(cursor);
            audioItems.add(audioItem);
        }

        return audioItems;
    }

    @Override
    public String toString() {
        return "AudioItem{" +
                "title='" + title + '\'' +
                ", artist='" + artist + '\'' +
                ", path='" + path + '\'' +
                '}';
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }


}
