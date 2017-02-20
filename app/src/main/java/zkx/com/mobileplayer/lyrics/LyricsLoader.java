package zkx.com.mobileplayer.lyrics;

import android.os.Environment;

import java.io.File;

/**
 * Created by zhang on 2016/11/2.
 */
public class LyricsLoader {
    public static final File root = new File(Environment.getDataDirectory(), "DownLoad/test/audio");

    /*从多种方式获取歌词文件*/
    public static File loadLyricFile(String title) {
        //本地查找lrc文件
        File lrcFile = new File(root, title + ".lrc");
        if (lrcFile.exists()) {
            return lrcFile;
        }
        //本地查找txt文件
        File textFile = new File(root, title + ".txt");
        if (textFile.exists()) {
            return textFile;
        }
        //查找服务器
        //...
        return null;
    }
}
