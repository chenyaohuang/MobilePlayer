package zkx.com.mobileplayer.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * Created by zhang on 2016/10/12.
 */
public class LoadBitmapUtils {
    private static Bitmap bitmap;

    /**
     * @param pathName 路径
     * @param reqWidth 裁剪宽度
     * @return
     */
    public static Bitmap decodeSampledBitmapFromResource(final String pathName, final int reqWidth, final int reqHeight) {

        ThreadUtils.runInThread(new Runnable() {
            @Override
            public void run() {
                BitmapFactory.Options options = new BitmapFactory.Options();
                //不去真正解析位图 但是能返回图片的一些信息(宽和高)
                options.inJustDecodeBounds = true;
//        ThreadUtils.runInThread();
                BitmapFactory.decodeFile(pathName, options);

                options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

                //开始真正的解析位图
                options.inJustDecodeBounds = false;
                bitmap = BitmapFactory.decodeFile(pathName, options);
            }
        });

        return bitmap;
    }

    /**
     * 计算缩放比
     *
     * @param options
     * @param reqWidth  请求宽
     * @param reqHeight 请求高
     * @return
     */
    private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        int width = options.outWidth;//获取图片的高
        int height = options.outHeight;//获取图片的高

        int inSampleSize = 1;
        if (width > reqWidth && height > reqHeight) {
            int halfWidth = width / 2;
            int halfHight = height / 2;
            while (halfWidth / inSampleSize >= reqWidth && halfHight / inSampleSize >= reqHeight) {
                inSampleSize *= 2;
            }
        }

        //[4]计算缩放比
//        int scale = 1;  //我们定义的缩放比
//        int scalex =   width/reqWidth;
//        int scaley =  height/reqHeight;
//        if (scalex >=scaley&&scalex > scale) {
//            scale = scalex;
//        }
//        if (scaley > scalex  && scaley>scale) {
//            scale = scaley;
//        }
//        return scale;
        return inSampleSize;
    }
}
