package zkx.com.mobileplayer.lyrics;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by zhang on 2016/11/2.
 */
public class LyricsParser {
    /**
     * 从歌词文件解析出歌词数据列表
     *
     * @param lyricsFile
     * @return
     */
    public static List<Lyrics> parserFromFile(File lyricsFile) {
        List<Lyrics> lyricsList = new ArrayList<>();
        //数据可用性检查
        if (lyricsFile == null || !lyricsFile.exists()) {
            lyricsList.add(new Lyrics(0, "没有找到歌词文件"));
            return lyricsList;
        }


        //按行解析歌词
        try {
            //为什么不使用FileReadr:因为FileReader不可以指定编码
            BufferedReader buffer = new BufferedReader(new InputStreamReader(new FileInputStream(lyricsFile), "GBK"));
            String line = buffer.readLine();
            while (line != null) {
                List<Lyrics> lineList = parserLine(line);
                lyricsList.addAll(lineList);
                line = buffer.readLine();
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //歌词排序
        Collections.sort(lyricsList);
        return lyricsList;
    }

    //解析一行歌词  [01:23.55][01:27.36]在这里失去
    private static List<Lyrics> parserLine(String line) {
        List<Lyrics> lineList = new ArrayList<Lyrics>();
        String arr[] = line.split("]");
        //[01:23.55 [01:27.36 在这里失去
        String content = arr[arr.length - 1];
        //[01:23.55 [01:27.36
        for (int i = 0; i < arr.length - 1; i++) {
            int startPoint = parserStartPoint(arr[i]);
            lineList.add(new Lyrics(startPoint, content));
        }
        return lineList;
    }

    /**
     * 解析出一行歌词的起始时间 [01:23.55
     *
     * @param startPoint
     * @return
     */
    private static int parserStartPoint(String startPoint) {
        int time = 0;
        //[01:23.55
        String arr[] = startPoint.split(":");
        //[01 23.55
        String minStr = arr[0].substring(1);//分钟
        int min = Integer.parseInt(minStr.substring(1, 3)) * 3600;

        //23.55
        arr = arr[1].split("\\.");

        //23 55
        String secStr = arr[0];//秒
        String mSecStr = arr[1];//毫秒

        time = Integer.parseInt(minStr) * 60 * 1000
                + Integer.parseInt(secStr) * 1000
                + Integer.parseInt(mSecStr) * 10;

        return time;
    }


}
