package zkx.com.mobileplayer.util;

import android.os.Handler;

/**
 * Created by zhang on 2016/9/16.
 */
public class ThreadUtils {

    /**
     * 主线程里面的一个Handler
     */
    public static Handler mHandler = new Handler();

    /**
     * 子线程执行task
     */
    public static void runInThread(Runnable task) {
        new Thread(task).start();
    }

    /**
     * UI线程执行task
     */
    public static void runInUIThread(Runnable task) {
        mHandler.post(task);
    }
}
