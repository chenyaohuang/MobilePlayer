package zkx.com.mobileplayer.ui.activity;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.drawable.AnimationDrawable;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

import zkx.com.mobileplayer.R;
import zkx.com.mobileplayer.bean.AudioItem;
import zkx.com.mobileplayer.lyrics.LyricsLoader;
import zkx.com.mobileplayer.lyrics.LyricsView;
import zkx.com.mobileplayer.service.AudioPlayerService;
import zkx.com.mobileplayer.util.StringUtils;

/**
 * Created by zhang on 2016/10/17.
 */
public class AudioPlayerActivity extends BaseActivity {

    private static final int MSG_UPDATE_POSITION = 0;
    private static final int MSG_ROLL_LYSRCS = 1;
    private AudioServiceConnection mConn;
    private AudioPlayerService.AudioServiceBinder mAudioServiceBinder;
    private ImageView iv_pause;
    private OnAudioReceiver mOnAudioReceiver;
    private TextView tv_title;
    private TextView tv_artist;
    private ImageView iv_wave;
    private TextView tv_position;
    private SeekBar sk_position;
    private ImageView iv_pre;
    private ImageView iv_next;
    private ImageView iv_playMode;
    private LyricsView lyricsView;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_UPDATE_POSITION:
                    startUpdatePosition();
                    break;
                case MSG_ROLL_LYSRCS:
                    startRollLyric();
                    break;
            }
        }
    };

    @Override
    public int getLayoutId() {
        return R.layout.audioplayer;
    }

    @Override
    public void initView() {
        tv_title = (TextView) findViewById(R.id.audioplayer_tv_title);//歌曲名
        tv_artist = (TextView) findViewById(R.id.audioplayer_tv_artist);//歌手名
        iv_wave = (ImageView) findViewById(R.id.audioplayer_iv_wave);//示波器
        lyricsView = (LyricsView) findViewById(R.id.audioplayer_lyricsView);
        tv_position = (TextView) findViewById(R.id.audioplayer_tv_positon);//已播放时间
        sk_position = (SeekBar) findViewById(R.id.audioplayer_sk_position);//播放seekBar
        iv_pause = (ImageView) findViewById(R.id.audioplayer_iv_pause);//播放/暂停按钮
        iv_pre = (ImageView) findViewById(R.id.audioplayer_iv_pre);//上一曲按钮
        iv_next = (ImageView) findViewById(R.id.audioplayer_iv_next);//下一曲按钮
        iv_playMode = (ImageView) findViewById(R.id.audioplayer_iv_playMode);//播放模式
    }

    @Override
    public void initListener() {
        iv_pause.setOnClickListener(this);
        sk_position.setOnSeekBarChangeListener(new OnAudioSeekBarChangeListener());
        iv_pre.setOnClickListener(this);
        iv_next.setOnClickListener(this);
        iv_playMode.setOnClickListener(this);

        IntentFilter filter = new IntentFilter("zkx.com.mobileplayer.audio_prepared");
        mOnAudioReceiver = new OnAudioReceiver();
        registerReceiver(mOnAudioReceiver, filter);

    }


    @Override
    public void initData() {
        //初始化获取数据
//        ArrayList<AudioItem> audioItems = (ArrayList<AudioItem>) getIntent().getSerializableExtra("audioItems");
//        int position = getIntent().getIntExtra("position", -1);
//
//        Intent service = new Intent(this,AudioPlayerService.class);
//        service.putExtra("audioItems",audioItems);
//        service.putExtra("position",position);

        Intent service = new Intent(getIntent());
        service.setClass(this, AudioPlayerService.class);
        mConn = new AudioServiceConnection();
        /**
         * BIND_AUTO_CREATE：bind时，服务还没有启动,那么就自动启起来
         */
        bindService(service, mConn, Service.BIND_AUTO_CREATE);
        startService(service);
        //开启示波器帧动画
        AnimationDrawable animationDrawable = (AnimationDrawable) iv_wave.getDrawable();
        animationDrawable.start();
    }

    @Override
    public void processClick(View v) {
        switch (v.getId()) {
            case R.id.audioplayer_iv_pause:
                switchPauseStatus();
                break;
            case R.id.audioplayer_iv_pre:
                playPre();
                break;
            case R.id.audioplayer_iv_next:
                playNext();
                break;
            case R.id.audioplayer_iv_playMode:
                switchPlayMode();
                break;
        }
    }

    /**
     * 切换播放模式
     */
    private void switchPlayMode() {
        mAudioServiceBinder.switchPlayMode();
        updatePlayModeButton();
        updatePlayModeToast();
    }

    public void updatePlayModeToast() {
        switch (mAudioServiceBinder.getPlayMode()) {
            case AudioPlayerService.PLAYMODE_ALL_REPEAT:
                Toast.makeText(this, "列表循环", Toast.LENGTH_SHORT).show();
                break;
            case AudioPlayerService.PLAYMODE_RANDOM:
                Toast.makeText(this, "随机播放", Toast.LENGTH_SHORT).show();
                break;
            case AudioPlayerService.PLAYMODE_SINGLE_REPEAT:
                Toast.makeText(this, "单曲循环", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    /**
     * 根据当前使用的播放模式,更新使用的图片
     */
    private void updatePlayModeButton() {
        switch (mAudioServiceBinder.getPlayMode()) {
            case AudioPlayerService.PLAYMODE_ALL_REPEAT:
                iv_playMode.setImageResource(R.drawable.audio_playmode_allrepeat_selector);
                break;
            case AudioPlayerService.PLAYMODE_RANDOM:
                iv_playMode.setImageResource(R.drawable.audio_playmode_random_selector);
                break;
            case AudioPlayerService.PLAYMODE_SINGLE_REPEAT:
                iv_playMode.setImageResource(R.drawable.audio_playmode_singlerepeat_selector);
                break;
        }
    }

    /**
     * 播放上一首歌
     */
    private void playPre() {
        mAudioServiceBinder.playPre();
    }

    /**
     * 播放下一首歌
     */
    private void playNext() {
        mAudioServiceBinder.playNext();
    }


    /**
     * 切换播放状态
     */
    private void switchPauseStatus() {

        if (mAudioServiceBinder.isPlaying()) {
            //如果正在播放
            mAudioServiceBinder.pause();
        } else {
            //如果是暂停状态
            mAudioServiceBinder.start();
        }

        updatePauseButton();
    }

    /**
     * 根据当前播放状态切换只暂停图片
     */
    private void updatePauseButton() {
        if (mAudioServiceBinder.isPlaying()) {
            //如果播放状态
            iv_pause.setImageResource(R.drawable.audio_pause_selector);
        } else {
            //如果是暂停状态
            iv_pause.setImageResource(R.drawable.audio_play_selector);
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mConn);
        unregisterReceiver(mOnAudioReceiver);
        mHandler.removeCallbacksAndMessages(null);
    }

    /**
     * 并更新歌词高亮行,并延迟再更新
     */
    private void startRollLyric() {
        lyricsView.roll(mAudioServiceBinder.getCurrentPosition(), mAudioServiceBinder.getDuration());
        mHandler.sendEmptyMessage(MSG_ROLL_LYSRCS);
    }

    /**
     * 获取当前已播放时间，更新播放进度,
     * 并延迟一段时间后刷新
     */
    private void startUpdatePosition() {
        //音乐当前播放时间
        int position = mAudioServiceBinder.getCurrentPosition();
        updatePosition(position);
    }

    /**
     * 根据当前播放时间 修改播放进度
     *
     * @param position
     */
    private void updatePosition(int position) {
        //音乐文件的总时长
        int duration = mAudioServiceBinder.getDuration();

        //生成时间字符串
        String durationStr = StringUtils.formatDuration(duration);
        String positionStr = StringUtils.formatDuration(position);

        tv_position.setText(positionStr + "/" + durationStr);
        sk_position.setProgress(position);
        mHandler.sendEmptyMessageDelayed(MSG_UPDATE_POSITION, 500);
    }

    private class OnAudioSeekBarChangeListener implements SeekBar.OnSeekBarChangeListener {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (!fromUser) {//不是发起的变更不处理
                return;
            }
            //跳转播放进度
            mAudioServiceBinder.seekTo(progress);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    }

    public class AudioServiceConnection implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mAudioServiceBinder = (AudioPlayerService.AudioServiceBinder) service;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    }

    private class OnAudioReceiver extends BroadcastReceiver {
        //onPrepared完成之后,将告知此接收者
        @Override
        public void onReceive(Context context, Intent intent) {
            //音乐准备完成，开始播放
//            iv_pause.setImageResource(R.drawable.audio_pause_selector);
            //更新暂停按钮
            updatePauseButton();

            //获取播放的歌曲
            AudioItem audioItem = (AudioItem) intent.getSerializableExtra("audioItem");

            //初始化标题和歌手名
            tv_title.setText(audioItem.getTitle());
            tv_artist.setText(audioItem.getArtist());

            //开启时间更新(MediaPlayer.OnPreparedListener的onPrepared方法之后才能获取信息)
            int duration = mAudioServiceBinder.getDuration();
            sk_position.setMax(duration);
            startUpdatePosition();

            //更新播放模式使用的图片
            updatePlayModeButton();

            //开启歌词更新
//            File lyricFile =new File(Environment.getDataDirectory(),"Download/test/audio/"+audioItem.getTitle()+".lrc");
            File lyricFile = LyricsLoader.loadLyricFile(audioItem.getTitle());
            lyricsView.setLyricFile(lyricFile);
            startRollLyric();
        }


    }
}
