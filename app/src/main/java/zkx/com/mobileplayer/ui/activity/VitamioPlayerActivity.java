package zkx.com.mobileplayer.ui.activity;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.nineoldandroids.view.ViewHelper;
import com.nineoldandroids.view.ViewPropertyAnimator;

import java.util.ArrayList;

import io.vov.vitamio.LibsChecker;
import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.widget.VideoView;
import zkx.com.mobileplayer.R;
import zkx.com.mobileplayer.bean.VideoItem;
import zkx.com.mobileplayer.util.StringUtils;

/**
 * Created by zhang on 2016/8/1.
 */
public class VitamioPlayerActivity extends BaseActivity {
    private static final int MSG_UPDATE_SYSTEM_TIME = 0;
    private static final int MSG_UPDATE_POSITION = 1;
    private static final int MSG_HIDE_CONTROLOR = 2;


    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_UPDATE_SYSTEM_TIME:
                    startUpdateSystemTime();
                    break;
                case MSG_UPDATE_POSITION:
                    startUpdatePosition();
                    break;
                case MSG_HIDE_CONTROLOR:
                    hideControlor();
                    break;
            }
        }
    };


    private VideoView videoView;
    private ImageView iv_pause;
    private TextView tv_title;
    private OnVideoReceiver onVideoReceiver;
    private ImageView iv_battery;
    private TextView tv_system_time;
    private SeekBar sk_volume;
    private AudioManager mAudioManager;
    private ImageView iv_mute;
    private int currentVolume;
    private float mStartY;
    private int startVolume;
    private View alpha_cover;
    private float mStartAlpha;
    private SeekBar sk_position;
    private TextView tv_duration;
    private TextView tv_position;
    private ArrayList<VideoItem> mVideoItems;
    private int mPosition;
    private ImageView iv_pre;
    private ImageView iv_next;
    private LinearLayout ll_top;
    private LinearLayout ll_bottom;
    private GestureDetector gestureDetector;
    private boolean isControlorShowing;
    private ImageView iv_fullScreen;
    private LinearLayout ll_loading_cover;
    private ProgressBar pb_buffering;

    @Override
    public int getLayoutId() {
        return R.layout.vitamioplayer;
    }

    @Override
    public void initView() {

        
        videoView = (VideoView) findViewById(R.id.videoplayer_videoview);
        alpha_cover = findViewById(R.id.videoplayer_alpha_cover);
        ViewHelper.setAlpha(alpha_cover, 0.6f);
        ll_top = (LinearLayout) findViewById(R.id.videoplayer_ll_top);
        ll_bottom = (LinearLayout) findViewById(R.id.videoplayer_ll_bottom);
        ll_loading_cover =(LinearLayout) findViewById(R.id.videoplayer_ll_loading_cover);
        pb_buffering = (ProgressBar) findViewById(R.id.videoplayer_pb_buffering);

        //顶部面板
        tv_title = (TextView) findViewById(R.id.videoplayer_tv_title);
        iv_battery = (ImageView) findViewById(R.id.videoplayer_tv_battery);
        tv_system_time = (TextView) findViewById(R.id.videoplayer_tv_system_time);
        sk_volume = (SeekBar) findViewById(R.id.videoplayer_sk_volume);
        iv_mute = (ImageView) findViewById(R.id.videoplayer_iv_mute);

        //底部面板
        tv_position = (TextView) findViewById(R.id.videoplayer_tv_position);
        sk_position = (SeekBar) findViewById(R.id.videoplayer_sk_position);
        tv_duration = (TextView) findViewById(R.id.videoplayer_tv_duration);
        iv_pause = (ImageView) findViewById(R.id.videoplayer_bottom_iv_pause);
        iv_pre = (ImageView) findViewById(R.id.videoplayer_iv_pre);
        iv_next = (ImageView) findViewById(R.id.videoplayer_iv_next);
        iv_fullScreen = (ImageView) findViewById(R.id.videoplayer_iv_fullScreen);

    }

    @Override
    public void initListener() {
        //顶部面板
        iv_mute.setOnClickListener(this);
        OnVideoSeekBarChangeListener onSeekBarChangeListener = new OnVideoSeekBarChangeListener();
        sk_volume.setOnSeekBarChangeListener(onSeekBarChangeListener);
        sk_position.setOnSeekBarChangeListener(onSeekBarChangeListener);

        //底部面板
        iv_pause.setOnClickListener(this);
        iv_pre.setOnClickListener(this);
        iv_next.setOnClickListener(this);
        iv_fullScreen.setOnClickListener(this);


        //手势监听
        gestureDetector = new GestureDetector(this,new OnVideoGestureListener());

        //视频相关
        videoView.setOnPreparedListener(new OnVideoPreparedListener());
        videoView.setOnCompletionListener(new OnVideoCompletionListener());
        videoView.setOnBufferingUpdateListener(new OnVideoBufferingUpdateListener());
        videoView.setOnInfoListener(new OnVideoInfoListener());
        videoView.setOnErrorListener(new OnVideoErrorListener());

    //注册广播
        IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        onVideoReceiver = new OnVideoReceiver();
        registerReceiver(onVideoReceiver, filter);
    }

    @Override
    public void initData() {
        LibsChecker.checkVitamioLibs(this);
        //显示加载遮罩
        ll_loading_cover.setVisibility(View.VISIBLE);

        //获取数据,初始化界面
        Uri uri = getIntent().getData();
        if(uri!=null){
            //外部发起的调用
            videoView.setVideoURI(uri);
            iv_pre.setEnabled(false);
            iv_next.setEnabled(false);
            tv_title.setText(uri.getPath());
        }else {
            //内部发起的调用
            mVideoItems = (ArrayList<VideoItem>) getIntent().getSerializableExtra("videoItems");
            mPosition = getIntent().getIntExtra("position", -1);

            playItem();

        }


        //开启时间更新
        startUpdateSystemTime();

        //获取系统音量
        mAudioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        int maxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        sk_volume.setMax(maxVolume);

        int currentVolume = getCurrentVolume();
        sk_volume.setProgress(currentVolume);

        ViewHelper.setAlpha(alpha_cover, 0);

        //隐藏控制面板
        initHideControlor();
    }


    /***
     * 播放当前position选中的视频
     */
    private void playItem() {
        VideoItem videoItem = mVideoItems.get(mPosition);

        logE("VideoPlayerActivity.initData: item=" + videoItem);

        videoView.setVideoURI(Uri.parse(videoItem.getPath()));

        //初始化标题
        tv_title.setText(videoItem.getTitle());

        //更新上一曲和下一曲图片
        updatePreAndNextBtn();

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        gestureDetector.onTouchEvent(event);

        //手势滑动修改音量
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mStartY = event.getY();
                startVolume = getCurrentVolume();
                mStartAlpha = ViewHelper.getAlpha(alpha_cover);
                break;
            case MotionEvent.ACTION_MOVE:
                /*手指划过屏幕的距离=手指当前位置-手指压下的位置*/
                float currentY = event.getY();
                float moveY = currentY - mStartY;

                /*手指划过的百分比=手指划过屏幕的距离/屏幕高度*/
                int halfScreenH = getWindowManager().getDefaultDisplay().getHeight() / 2;
                int halfScreenW = getWindowManager().getDefaultDisplay().getWidth() / 2;
                float movePercent = moveY / halfScreenH;

                if (event.getX() < halfScreenW) {
                    moveAlpha(movePercent);//左侧修改亮度
                } else {
                    moveVolume(movePercent);//右侧修改音量
                }

                break;
        }
        return super.onTouchEvent(event);
    }

    private void moveAlpha(float movePercent) {
        //最终的透明度 = 手指压下的透明度+变化的透明度
        float finalAlpha = mStartAlpha + movePercent;
        if (finalAlpha >= 0 && finalAlpha <= 1) {
            ViewHelper.setAlpha(alpha_cover, finalAlpha);
        }
    }

    /**
     * 根据当前手指划过屏幕的百分比修改系统音量
     *
     * @param movePercent
     */
    private void moveVolume(float movePercent) {
    /*变化的音量=手指划过屏幕的百分比*最大音量*/
        int offsetVolume = (int) (movePercent * sk_volume.getMax());

                /*最终的音量=手指压下时的音量+变化的音量*/
        int finalVolume = startVolume - offsetVolume;

        setVolume(finalVolume);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(onVideoReceiver);
        handler.removeCallbacksAndMessages(null);
    }

    @Override
    public void processClick(View v) {
        switch (v.getId()) {
            case R.id.videoplayer_bottom_iv_pause:
                switchPauseStatus();
                break;
            case R.id.videoplayer_iv_mute:
                switchMuteStatus();
                break;
            case R.id.videoplayer_iv_pre:
                playPre();
                break;
            case R.id.videoplayer_iv_next:
                playNext();
                break;
            case R.id.videoplayer_iv_fullScreen:
                switchFullScreen();
                break;
        }
    }

    /**
     *切换视频的全屏状态
     */
    public void switchFullScreen(){
        videoView.switchFullScreen();
        updateFullscreenBtn();
    }

    /**
     *  根据全屏状态,切换全屏按钮使用的图片
     */
    private void updateFullscreenBtn() {
        if(videoView.isFullScreen()){
            //全屏状态-->设置为按下默认屏的选择器
            iv_fullScreen.setImageResource(R.drawable.video_defaultscreen_seletor);
        }else {
            //默认比例-->设置为按下全屏的选择器
            iv_fullScreen.setImageResource(R.drawable.video_fullscreen_selector);

        }
    }

    /**
     * 播放上一个视频
     */
    private void playPre() {
        if (mPosition != 0) {
            mPosition--;
            playItem();
        }

    }


    /**
     * 播放下一个视频
     */
    private void playNext() {
        if (mPosition != mVideoItems.size() - 1) {
            mPosition++;
            playItem();
        }

    }

    /**
     * 播放上一曲和下一曲使用的图片
     */
    private void updatePreAndNextBtn() {
        iv_pre.setEnabled(mPosition != 0);
        iv_next.setEnabled(mPosition != mVideoItems.size() - 1);
    }

    /**
     * 如果当前音量不为0，则记录当前音量，并设置音量为0;
     * 若果音量为0，则将音量恢复到之前的值
     */
    private void switchMuteStatus() {
        if (getCurrentVolume() != 0) {
            //非静音状态
            currentVolume = getCurrentVolume();//记录当前系统的音量
            setVolume(0);
        } else {
            //静音状态
            setVolume(currentVolume);
        }
    }

    private void setVolume(int currentVolume) {
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, currentVolume, 1);
        sk_volume.setProgress(currentVolume);
    }

    /**
     * 获取当前系统的STREAM_MUSIC音量
     *
     * @return
     */
    private int getCurrentVolume() {
        return mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
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
            handler.sendEmptyMessageDelayed(MSG_UPDATE_POSITION, 500);
        } else {
            //暂停
            iv_pause.setImageResource(R.drawable.video_paly_seletcor);
            handler.removeMessages(MSG_UPDATE_POSITION);
        }
    }



    private class OnVideoPreparedListener implements MediaPlayer.OnPreparedListener {
        @Override
        public void onPrepared(MediaPlayer mp) {
            //视频准备完成
            ll_loading_cover.setVisibility(View.GONE);
            videoView.start();
            //更新暂停按钮使用的图片
            updatePauseBtn();

            //开始更新时间进度
            int duration = (int) mp.getDuration();
            tv_duration.setText(StringUtils.formatDuration(duration));
            sk_position.setMax(duration);
            startUpdatePosition();

            //更新全屏按钮
            updateFullscreenBtn();
        }
    }

    /**
     * 更新当前播放进度，并延迟一段时间后再次更新
     */
    private void startUpdatePosition() {
        int position = (int) videoView.getCurrentPosition();
        updatePosition(position);

        handler.sendEmptyMessageDelayed(MSG_UPDATE_POSITION, 500);
    }


    /**
     * 根据position更新播放进度
     *
     * @param position
     */
    private void updatePosition(int position) {
        tv_position.setText(StringUtils.formatDuration(position));
        sk_position.setProgress(position);
    }

    private class OnVideoReceiver extends BroadcastReceiver {
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


    /**
     * 更新当前系统时间，并延迟一段时间后再次更新
     */
    private void startUpdateSystemTime() {
        tv_system_time.setText(StringUtils.formatSystemTime());
        handler.sendEmptyMessageDelayed(MSG_UPDATE_SYSTEM_TIME, 500);
    }

    /**
     * 初始化界面时隐藏控制面板
     */
    private void initHideControlor() {
        //获取top高度
        ll_top.measure(0, 0);
        ViewPropertyAnimator.animate(ll_top).translationY(-ll_top.getMeasuredHeight());

        //getHeight方法获取bottom高度
        ll_bottom.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                ll_bottom.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                ViewPropertyAnimator.animate(ll_bottom).translationY(ll_bottom.getHeight());
            }
        });
    }

    /**
     * 切换控制面板
     */
    private void switchControlor() {
        if(isControlorShowing){
            //面板处于显示状态-->隐藏
            hideControlor();

        }else{
            //面板处于隐藏状态-->显示
            showControlor();
            handler.sendEmptyMessageDelayed(MSG_HIDE_CONTROLOR, 5000);
        }

    }

    /**
     * 隐藏控制面板
     */
    private void hideControlor() {
        ViewPropertyAnimator.animate(ll_top).translationY(-ll_top.getHeight());
        ViewPropertyAnimator.animate(ll_bottom).translationY(ll_bottom.getHeight());
        isControlorShowing=false;
    }

    /**
     * 显示控制面板
     */
    private void showControlor() {
        ViewPropertyAnimator.animate(ll_top).translationY(0);
        ViewPropertyAnimator.animate(ll_bottom).translationY(0);
        isControlorShowing=true;
    }

    private class OnVideoSeekBarChangeListener implements SeekBar.OnSeekBarChangeListener {
        /**
         * 当进度条的值发生变化时会回调此方法
         */
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            //如果不是用户发起的操作则不处理
            if (!fromUser) {
                return;
            }

            switch (seekBar.getId()) {
                case R.id.videoplayer_sk_volume:
                    setVolume(progress);
                    break;
                case R.id.videoplayer_sk_position:
                    videoView.seekTo(progress);
                    updatePosition(progress);
                    break;
            }
//            mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC,progress,1);

        }

        /**
         * 当手指放到SeekBar上时会回调此方法
         *
         * @param seekBar
         */
        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            handler.removeMessages(MSG_HIDE_CONTROLOR);
        }

        /**
         * 当手指离开SeekBar时会回调此方法
         *
         * @param seekBar
         */
        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            handler.sendEmptyMessageDelayed(MSG_HIDE_CONTROLOR,5000);
        }
    }

    private class OnVideoCompletionListener implements MediaPlayer.OnCompletionListener {

        @Override
        public void onCompletion(MediaPlayer mp) {
            //视频播放结束

            //规避系统错误,将已播放时间设置为视频总长度
            handler.removeMessages(MSG_UPDATE_POSITION);
            updatePosition((int) mp.getDuration());


            //更新暂停按钮使用的图片
            updatePauseBtn();
        }
    }


    private class OnVideoGestureListener extends GestureDetector.SimpleOnGestureListener {
        
        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            switchControlor();
            return super.onSingleTapConfirmed(e);
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            switchFullScreen();
            return super.onDoubleTap(e);
        }

        @Override
        public void onLongPress(MotionEvent e) {
            switchPauseStatus();
            super.onLongPress(e);
        }
    }


    class OnVideoBufferingUpdateListener implements MediaPlayer.OnBufferingUpdateListener {

        @Override
        public void onBufferingUpdate(MediaPlayer mp, int percent) {
            //当缓冲发生变化时调用此方法

            //将百分比转换成小数
            float bufferPercent = percent / 100f;
            int bufferTime = (int) (bufferPercent*sk_position.getMax());
            sk_position.setSecondaryProgress(bufferTime);
        }
    }

   private class OnVideoInfoListener implements MediaPlayer.OnInfoListener{

       @Override
       public boolean onInfo(MediaPlayer mp, int what, int extra) {
           //播放状态发生变化时会回调此方法
           switch(what){
                case MediaPlayer.MEDIA_INFO_BUFFERING_START:
                    pb_buffering.setVisibility(View.VISIBLE);
                    break;
               case MediaPlayer.MEDIA_INFO_BUFFERING_END:
                   pb_buffering.setVisibility(View.INVISIBLE);
                   break;
           }

           return false;
       }


   }

    private class OnVideoErrorListener implements MediaPlayer.OnErrorListener {
        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            //当视频播放格式发生错误会回调到此方法
            AlertDialog.Builder builder = new AlertDialog.Builder(VitamioPlayerActivity.this);
            builder.setTitle("提示");
            builder.setMessage("该视频无法播放");
            builder.setPositiveButton("退出播放", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
            builder.show();
            return false;
        }
    }
}
