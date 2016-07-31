package zkx.com.mobileplayer.util;

import android.database.Cursor;
import android.util.Log;

/**
 * Created by zhang on 2016/7/31.
 */
public class CursorUtils {
    private static final String TAG = "CursorUtils";

    /**
     * 打印cursor的所有内容
     */
    public static void printCursor(Cursor cursor) {
        LogUtils.e(TAG, "CursorUtils.printCursor:查询到的数据个数为:" + cursor.getCount());
        while (cursor.moveToNext()) {
            LogUtils.e(TAG, "==========================================================================");
            for (int i = 0; i < cursor.getColumnCount(); i++) {
                LogUtils.e(TAG, "VideoListFragment.initData:name=" + cursor.getColumnName(i) + ";value=" + cursor.getString(i));
            }
        }
    }
}
