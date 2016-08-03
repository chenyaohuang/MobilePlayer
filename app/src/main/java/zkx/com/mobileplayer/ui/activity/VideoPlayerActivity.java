package zkx.com.mobileplayer.ui.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import zkx.com.mobileplayer.R;
import zkx.com.mobileplayer.bean.VideoItem;
import zkx.com.mobileplayer.util.StringUtils;

/**
 * Created by zhang on 2016/8/1.
 */
public class VideoPlayerActivity extends BaseActivity {
    private static final int MSG_UPDATE_SYSTEM_TIME = 0;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case MSG_UPDATE_SYSTEM_TIME:
                    startUpdateSystemTime();
                    break;
            }
        }
    };

    private VideoView videoView;
    private ImageView iv_pause;
    private TextView tv_title;
    private VideoReceiver videoReceiver;
    private ImageView iv_battery;
    private TextView tv_system_time;

    @Override
    public int getLayoutId() {
        return R.layout.videoplayer;
    }

    @Override
    public void initView() {
        videoView = (VideoView) findViewById(R.id.videoplayer_videoview);

        //顶部面板
        tv_title = (TextView) findViewById(R.id.videoplayer_tv_title);
        iv_battery = (ImageView) findViewById(R.id.videoplayer_tv_battery);
        tv_system_time = (TextView) findViewById(R.id.videoplayer_tv_system_time);
        //底部面板
        iv_pause = (ImageView) findViewById(R.id.videoplayer_bottom_iv_pause);
    }

    @Override
    public void initListener() {
        iv_pause.setOnClickListener(this);
        videoView.setOnPreparedListener(new OnVideoPreparedListener());
        //注册广播
        IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        videoReceiver = new VideoReceiver();
        registerReceiver(new VideoReceiver(), filter);
    }

    @Override
    public void initData() {
        //获取数据,初始化界面
        VideoItem videoItem = (VideoItem) getIntent().getSerializableExtra("videoItem");
        logE("VideoPlayerActivity.initData: item=" + videoItem);

        videoView.setVideoURI(Uri.parse(videoItem.getPath()));
//        videoView.setVideoPath(videoItem.getPath());
//        videoView.start();
//        videoView.setMediaController(new MediaController(this));
        //初始化标题
        tv_title.setText(videoItem.getTitle());
        //开启时间更新

        startUpdateSystemTime();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(videoReceiver);
    }

    @Override
    public void processClick(View v) {
        switch (v.getId()) {
            case R.id.videoplayer_bottom_iv_pause:
                switchPauseStatus();
                break;
        }
    }

    /**
     * 切换视频播放/暂停状态
     */
    private void switchPauseStatus() {
        if (videoView.isPlaying()) {
            //正在播放,点击按钮变暂停
            videoView.pause();
        } else {
            //暂停状态,点击按钮变播放
            videoView.start();
        }
        updatePauseBtn();
    }

    /**
     * 根据当前播放状态，更改暂停按钮使用的图片
     */
    private void updatePauseBtn() {
        if (videoView.isPlaying()) {
            //正在播放
            iv_pause.setImageResource(R.drawable.video_pause_seletor);
        } else {
            //暂停
            iv_pause.setImageResource(R.drawable.video_paly_seletor);
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

    private class VideoReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            //获取系统电量
            int level = intent.getIntExtra("level", 0);
            updateBatteryBtn(level);
        }


    }

    /**
     * 根据level等级,修改电池使用的图片
     */
    private void updateBatteryBtn(int level) {
        if (level < 10) {
            iv_battery.setImageResource(R.drawable.ic_battery_0);
        } else if (level < 20) {
            iv_battery.setImageResource(R.drawable.ic_battery_10);
        } else if (level < 40) {
            iv_battery.setImageResource(R.drawable.ic_battery_20);
        } else if (level < 60) {
            iv_battery.setImageResource(R.drawable.ic_battery_40);
        } else if (level < 80) {
            iv_battery.setImageResource(R.drawable.ic_battery_60);
        } else if (level < 100) {
            iv_battery.setImageResource(R.drawable.ic_battery_80);
        } else {
            iv_battery.setImageResource(R.drawable.ic_battery_100);

        }
    }


    /**更新当前系统时间，并延迟一段时间后再次更新*/
    private void startUpdateSystemTime() {
        tv_system_time.setText(StringUtils.formatSystemTime());
        handler.sendEmptyMessageDelayed(MSG_UPDATE_SYSTEM_TIME,500);
        handler.removeCallbacksAndMessages(null);
    }

}
