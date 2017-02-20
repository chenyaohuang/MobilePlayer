package com.example.jiana.cutvideoframedemo.rv;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;

import com.example.jiana.cutvideoframedemo.R;
import com.example.jiana.cutvideoframedemo.VideoFrameImageLoader;

/**
 * Created by jiana on 16-4-3.
 */
public class VideoImageRVAdapter extends RecyclerView.Adapter<VideoImageRVAdapter.MViewHolder>{
    private Context context;
    /**
     * 视频链接的数据
     */
    private String[] videoUrls;

    private boolean isFirst = true;
    private VideoFrameImageLoader mVideoFrameImageLoader;

    public VideoImageRVAdapter(Context context, VideoFrameImageLoader vfi) {
        this.context = context;
        this.mVideoFrameImageLoader = vfi;
        this.videoUrls = vfi.getVideoUrls();
    }

    @Override
    public MViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ImageView imageView = new ImageView(context);
        imageView.setLayoutParams(new ListView.LayoutParams(150, 200));
        imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        return new MViewHolder(imageView);
    }


    @Override
    public void onBindViewHolder(MViewHolder holder, int position) {
        if (isFirst) {
            mVideoFrameImageLoader.initList();
            isFirst = false;
        }
        String mImageUrl = videoUrls[position];
        holder.mImageView.setTag(mImageUrl);
        //从缓存中获取图片
        Bitmap bitmap = mVideoFrameImageLoader.showCacheBitmap(VideoFrameImageLoader.formatVideoUrl(mImageUrl));
        if (bitmap != null) {
            holder.mImageView.setImageBitmap(bitmap);
        } else {
            //没有从缓存中加载到时，先设置一张默认图
            holder.mImageView.setImageResource(R.mipmap.ic_launcher);
        }
    }

    @Override
    public int getItemCount() {
        return videoUrls.length;
    }

    public class MViewHolder extends RecyclerView.ViewHolder {
        public ImageView mImageView;
        public MViewHolder(View itemView) {
            super(itemView);
            mImageView = (ImageView) itemView;
        }
    }
}
