package zkx.com.mobileplayer.util;

import android.util.Log;

/**
 * Created by zhang on 2016/7/28.
 */
public class LogUtils {
    private static final boolean ENABLE = true;

    /**
     * 打印一个debug等级的log
     */
    public static void d(String tag,String msg){
        if(ENABLE){
            Log.d("zkx_"+tag, msg);
        }
    }

    /**
     * 打印一个error等级的log
     */
    public static void e(String tag,String msg){
        if(ENABLE){
            Log.e("zkx_"+tag, msg);
        }
    }


    /**
     * 打印一个子类的名称为tag的log
     */
    public static void e(Class cls,String msg){
        if(ENABLE){
            Log.e("zkx_"+cls.getSimpleName(), msg);
        }
    }
}
