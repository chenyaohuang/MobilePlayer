package zkx.com.mobileplayer.ui.activity;

import android.media.MediaPlayer;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.VideoView;

import zkx.com.mobileplayer.R;
import zkx.com.mobileplayer.bean.VideoItem;

/**
 * Created by zhang on 2016/8/1.
 */
public class VideoPlayerActivity extends BaseActivity {
    private VideoView videoView;
    private ImageView iv_pause;

    @Override
    public int getLayoutId() {
        return R.layout.videoplayer;
    }

    @Override
    public void initView() {
        videoView = (VideoView) findViewById(R.id.videoplayer_videoview);
        iv_pause = (ImageView) findViewById(R.id.videoplayer_bottom_iv_pause);
    }

    @Override
    public void initListener() {
        iv_pause.setOnClickListener(this);
        videoView.setOnPreparedListener(new OnVideoPreparedListener());
    }

    @Override
    public void initData() {
        //获取数据,初始化界面
        VideoItem videoItem = (VideoItem) getIntent().getSerializableExtra("videoItem");
        logE("VideoPlayerActivity.initData: item="+videoItem);

        videoView.setVideoURI(Uri.parse(videoItem.getPath()));
//        videoView.setVideoPath(videoItem.getPath());
//        videoView.start();
//        videoView.setMediaController(new MediaController(this));
    }

    @Override
    public void processClick(View v) {
        switch (v.getId()){
            case R.id.videoplayer_bottom_iv_pause:
                switchPauseStatus();
                break;
        }
    }

    /**
     * 切换视频播放/暂停状态
     */
    private void switchPauseStatus() {
        if(videoView.isPlaying()){
            //正在播放,点击按钮变暂停
            videoView.pause();
        }else{
            //暂停状态,点击按钮变播放
            videoView.start();
        }
        updatePauseBtn();
    }

    /**
     * 根据当前播放状态，更改暂停按钮使用的图片
     */
    private void updatePauseBtn() {
        if (videoView.isPlaying()){
            //正在播放
            iv_pause.setImageResource(R.drawable.video_pause_seletor);
        }else{
            //暂停
            iv_pause.setImageResource(R.drawable.video_paly_seletcor);
        }
    }

    private class OnVideoPreparedListener implements MediaPlayer.OnPreparedListener {
        @Override
        public void onPrepared(MediaPlayer mp) {
            //视频准备完成
            videoView.start();
            //更新暂停按钮使用的图片
            updatePauseBtn();
        }
    }
}
