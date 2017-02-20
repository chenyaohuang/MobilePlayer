package zkx.com.mobileplayer.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by zhang on 2016/7/31.
 */
public class StringUtils {
    private static final int HOUR = 60*60*1000;//1小时的毫秒数
    private static final int MIN = 60*1000;//1分钟的毫秒数
    private static final int SEC = 1000;//1秒钟的毫秒数

    /**将时间戳转换为01:01:01或01:01的格式*/
    public static String formatDuration(int duration){
        int hour = duration / HOUR;//计算小时数
        //计算分钟数
        int min = duration % HOUR / MIN;
        //计算秒数
        int sec = duration % MIN / SEC;
        //生成格式化字符串
        if (hour == 0){
            //不足1小时01:01
            return String.format("%02d:%02d",min,sec);
        }else {
            //大于一小时
            return String.format("%02d:%2d:%2d",hour,min,sec);
        }
    }

    /**
     * 格式化当前系统时间为01:01:01的格式
     */
    public static String formatSystemTime() {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
        return format.format(new Date());
    }

    //音乐的DISPLAYNAME有后缀名，所以写此方法将后缀名去掉
    public static String formatDispalyName(String displayName) {
        return displayName.substring(0, displayName.indexOf("."));
    }
}
