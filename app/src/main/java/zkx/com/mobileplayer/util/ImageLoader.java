package zkx.com.mobileplayer.util;

/**
 * Created by zhang on 2016/10/14.
 */

import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.LruCache;
import android.widget.ImageView;


/**
 * Created by zhang on 2016/11/27.
 */
public class ImageLoader {


    private ImageView mImageView;
    private String mUrl;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (mImageView.getTag().equals(mUrl)) {
                mImageView.setImageBitmap((Bitmap) msg.obj);
            }

        }
    };
    //创建Cache，并指定它的键值对类型
    private LruCache<String, Bitmap> mCaches;

    public ImageLoader() {
        //获取最大可用内存
        int maxMemory = (int) Runtime.getRuntime().maxMemory();
        int cacheSize = maxMemory / 4;
        mCaches = new LruCache<String, Bitmap>(cacheSize) {

            //必须重写sizeof方法，它在每次存入缓存的时候调用，告诉我们的缓存，
            //告诉我们的系统，当前存入的对象到底有多大
            @Override
            protected int sizeOf(String key, Bitmap value) {
                //在每次存入缓存的时候调用
                return value.getByteCount();//返回实际的大小
            }
        };
    }

    /**
     * @param imageView
     * @param url
     */
    public void showImageByThread(ImageView imageView, final String url) {
        mImageView = imageView;
        mUrl = url;
        new Thread() {
            @Override
            public void run() {
                super.run();
                MediaMetadataRetriever media = new MediaMetadataRetriever();
                media.setDataSource(url);
                Bitmap bitmap = media.getFrameAtTime(3000);
                bitmap = ThumbnailUtils.extractThumbnail(bitmap, 100, 100);
                Message message = Message.obtain();
                message.obj = bitmap;
                mHandler.sendMessage(message);
            }
        }.start();

    }

    //增加到缓存
    public void addBitmapToCache(String url, Bitmap bitmap) {
        if (getBitmapFromCache(url) == null) {
            mCaches.put(url, bitmap);
        }
    }

    //从缓存中获取数据
    public Bitmap getBitmapFromCache(String url) {
        return mCaches.get(url);
    }


    public void showImageByAsyncTask(ImageView imageView, String url) {
        //从缓存中取出对应的图片
        Bitmap bitmap = getBitmapFromCache(url);
        //如果缓存最中没有，那么必须去网络下载
        if (bitmap == null) {
            new VideoAsyncTask(imageView, url).execute(url);
        } else {
            //有,则从直接缓存中获取，直接设置。
            // 这样不用每次都从网络中下载，直接使用网络中的数据
            //这就是典型的以内存换效率，节省流量
            imageView.setImageBitmap(bitmap);
        }

    }

    private class VideoAsyncTask extends AsyncTask<String, Void, Bitmap> {
        private ImageView mImageView;
        private String mUrl;

        public VideoAsyncTask(ImageView imageView, String url) {
            mImageView = imageView;
            mUrl = url;
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            String url = params[0];
            //从网络上获取图片
//            Bitmap bitmap = getBitmapFromURL(params[0]);
            MediaMetadataRetriever media = new MediaMetadataRetriever();
            media.setDataSource(url);
            Bitmap bitmap = media.getFrameAtTime();
            bitmap = ThumbnailUtils.extractThumbnail(bitmap, 100, 100);

            if (bitmap != null) {
                //将不在缓存中的图片加入缓存
                addBitmapToCache(url, bitmap);
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            if (mImageView.getTag().equals(mUrl)) {
                mImageView.setImageBitmap(bitmap);
            }

        }
    }



}
