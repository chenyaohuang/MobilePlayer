package com.example.jiana.cutvideoframedemo;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;

/**
 * Created by jiana on 16-4-3.
 */
public class VideoImageAdapter extends BaseAdapter {

    private Context context;
    /**
     * 视频链接的数据
     */
    private String[] videoUrls;

    private VideoFrameImageLoader mVideoFrameImageLoader;

    public VideoImageAdapter(Context context, VideoFrameImageLoader vfi) {
        this.context = context;
        this.mVideoFrameImageLoader = vfi;
        this.videoUrls = vfi.getVideoUrls();
    }

    @Override
    public int getCount() {
        return videoUrls.length;
    }

    @Override
    public String getItem(int position) {
        return videoUrls[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView mImageView;
        String mImageUrl = videoUrls[position];
        if (convertView == null) {
            mImageView = new ImageView(context);
            //初始化显示数据
            mVideoFrameImageLoader.initList();
        } else {
            mImageView = (ImageView) convertView;
        }
        mImageView.setLayoutParams(new ListView.LayoutParams(150, 200));
        mImageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        mImageView.setTag(mImageUrl);
        //从缓存中获取图片
        Bitmap bitmap = mVideoFrameImageLoader.showCacheBitmap(VideoFrameImageLoader.formatVideoUrl(mImageUrl));
        if (bitmap != null) {
            mImageView.setImageBitmap(bitmap);
        } else {
            //没有从缓存中加载到时，先设置一张默认图
            mImageView.setImageResource(R.mipmap.ic_launcher);
        }
        return mImageView;
    }

}






