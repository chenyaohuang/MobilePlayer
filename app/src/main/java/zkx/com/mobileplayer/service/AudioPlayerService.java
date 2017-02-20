package zkx.com.mobileplayer.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.RemoteViews;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import zkx.com.mobileplayer.R;
import zkx.com.mobileplayer.bean.AudioItem;
import zkx.com.mobileplayer.ui.activity.AudioPlayerActivity;


/**
 * Created by zhang on 2016/10/19.
 */
public class AudioPlayerService extends Service {


    public static final int PLAYMODE_ALL_REPEAT = 0;
    public static final int PLAYMODE_RANDOM = 1;
    public static final int PLAYMODE_SINGLE_REPEAT = 2;
    private static final int NOTIFY_TYPE_PRE = 0;
    private static final int NOTIFY_TYPE_NEXT = 1;
    private static final int NOTIFY_TYPE_CONTENT = 2;
    private static final String NOTIFY_TYPE = "notify_type";
    private ArrayList<AudioItem> audioItems;
    private int position;
    private AudioServiceBinder mAudioServiceBinder;
    private SharedPreferences mPreferences;
    private int mPlayMode = PLAYMODE_ALL_REPEAT;//用一个变量记住当前的播放模式

    @Override
    public void onCreate() {
        super.onCreate();
        mAudioServiceBinder = new AudioServiceBinder();
        mPreferences = getSharedPreferences("audio.conf", MODE_PRIVATE);
        mPlayMode = mPreferences.getInt("play_mode", PLAYMODE_ALL_REPEAT);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int notifyType = intent.getIntExtra(NOTIFY_TYPE, -1);

        //从通知栏启动
        switch (notifyType) {
            case NOTIFY_TYPE_PRE:
                mAudioServiceBinder.playPre();
                break;
            case NOTIFY_TYPE_NEXT:
                mAudioServiceBinder.playNext();
                break;
            case NOTIFY_TYPE_CONTENT:
                notifyUpdateUI();
                break;
            default:
                //不是从通知栏启动
                //初始化数据
                int position = intent.getIntExtra("position", -1);//用一个值记住position
                if (position == this.position) {
                    //重复播放同一首歌
                    notifyUpdateUI();
                    if (!mAudioServiceBinder.isPlaying()) {
                        mAudioServiceBinder.start();
                    }
                } else {
                    //播放新的歌曲
                    audioItems = (ArrayList<AudioItem>) intent.getSerializableExtra("audioItems");
                    this.position = position;
                    mAudioServiceBinder.play();
                }
                break;
        }

        return super.onStartCommand(intent, flags, startId);
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mAudioServiceBinder;
    }


    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    /**
     * 通知界面更新
     */
    private void notifyUpdateUI() {
        //获取当前正在播放的歌曲
        AudioItem audioItem = audioItems.get(position);

        //告知界面更新
        Intent intent = new Intent("zkx.com.mobileplayer.audio_prepared");
        intent.putExtra("audioItem", audioItem);
        sendBroadcast(intent);
    }

    /**
     * 显示通知
     */
    private void showNotification() {
        //生成Notification
        Notification notification = getNormalNotificationNewApi();

        //显示通知
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(0, notification);
    }





    /*===================================通知相关方法============================================================*/

    /**
     * 取消通知
     */
    private void cancleNotification() {
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancel(0);
    }

    /**
     * 使用新Api生成Notification对象
     */
    private Notification getNormalNotificationNewApi() {
        Notification.Builder builder = new Notification.Builder(this)
                .setSmallIcon(R.drawable.icon)
                .setOngoing(true)
                .setTicker("正在播放: " + audioItems.get(position).getTitle())
                .setContentText(audioItems.get(position).getArtist());
        builder.setContent(getRemoteView());
        return builder.getNotification();
    }

    private RemoteViews getRemoteView() {
        RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.audio_notify);

        //填充文字
        remoteViews.setTextViewText(R.id.audio_notify_tv_title, audioItems.get(position).getTitle());
        remoteViews.setTextViewText(R.id.audio_notify_tv_aritist, audioItems.get(position).getArtist());

        //设置点击事件
        remoteViews.setOnClickPendingIntent(R.id.audio_notify_iv_pre, getPreIntent());
        //requsetCode都是0,所以覆盖了上一曲的Intent(PendingIntent.FLAG_UPDATE_CURRENT,
        // 使用同一个PendingIntent,覆盖Intent对象)
        remoteViews.setOnClickPendingIntent(R.id.audio_notify_iv_next, getNextIntent());
        remoteViews.setOnClickPendingIntent(R.id.audio_notify_layout, getContentIntent());

        return remoteViews;
    }

    /**
     * 点击通知空白部分的响应
     *
     * @return
     */
    private PendingIntent getContentIntent() {
        Intent intent = new Intent(this, AudioPlayerActivity.class);
        intent.putExtra(NOTIFY_TYPE, NOTIFY_TYPE_CONTENT);
        return PendingIntent.getActivity(this, 2, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    /**
     * 点击上一曲的响应
     *
     * @return
     */
    public PendingIntent getPreIntent() {
        Intent intent = new Intent(this, AudioPlayerService.class);
        intent.putExtra(NOTIFY_TYPE, NOTIFY_TYPE_PRE);
        return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    /**
     * 点击下一曲的响应
     *
     * @return
     */
    public PendingIntent getNextIntent() {
        Intent intent = new Intent(this, AudioPlayerService.class);
        intent.putExtra(NOTIFY_TYPE, NOTIFY_TYPE_NEXT);
        return PendingIntent.getService(this, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public class AudioServiceBinder extends Binder {

        private MediaPlayer mediaPlayer;

        /**
         * 播放当前选中的歌曲
         */
        private void play() {
            AudioItem audioItem = audioItems.get(position);

            if (mediaPlayer != null) {
                if (mediaPlayer.isPlaying()) {
//                    mediaPlayer.stop();
//                    mediaPlayer.release();
                    mediaPlayer.reset();
                }
            }
            //播放选中的item
            mediaPlayer = new MediaPlayer();
            try {
                mediaPlayer.setDataSource(audioItem.getPath());
                mediaPlayer.setOnPreparedListener(new OnAudioPreparedListener());
                mediaPlayer.setOnCompletionListener(new OnAudioCompletionListener());
                mediaPlayer.prepareAsync();
//                mediaPlayer.start();导致卡顿原因
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        /**
         * 开始播放(再次开启播放,按开始按钮执行的方法)
         */
        public void start() {
            mediaPlayer.start();
            showNotification();
        }

        /**
         * 暂停播放(按暂停按钮执行的方法)
         */
        public void pause() {
            mediaPlayer.pause();

            //隐藏通知
            cancleNotification();
        }

        /**
         * @return 如果正在播放，则返回true
         */
        public boolean isPlaying() {
            return mediaPlayer.isPlaying();
        }

        /**
         * 返回当前音乐的总时长
         */
        public int getDuration() {
            return mediaPlayer.getDuration();
        }

        /**
         * 返回当前音乐已播放时间
         */
        public int getCurrentPosition() {
            return mediaPlayer.getCurrentPosition();
        }

        /**
         * 跳转播放进度
         *
         * @param mSec
         */
        public void seekTo(int mSec) {
            mediaPlayer.seekTo(mSec);
        }

        /**
         * 播放上一首歌
         */
        public void playPre() {
            if (position != 0) {
                position--;
                play();
            } else {
                Toast.makeText(getApplicationContext(), "已经是第一首歌了", Toast.LENGTH_SHORT).show();
            }

        }

        /**
         * 播放下一首歌
         */
        public void playNext() {
            if (position != audioItems.size() - 1) {
                position++;
                play();
            } else {
                Toast.makeText(getApplicationContext(), "已经是最后一首歌了", Toast.LENGTH_SHORT).show();
            }

        }

        /**
         * 按照列表循环、随机播放、单曲循环 的顺序切换播放模式
         */
        public void switchPlayMode() {
            switch (mPlayMode) {
                case PLAYMODE_ALL_REPEAT:
                    mPlayMode = PLAYMODE_RANDOM;
                    break;
                case PLAYMODE_RANDOM:
                    mPlayMode = PLAYMODE_SINGLE_REPEAT;
                    break;
                case PLAYMODE_SINGLE_REPEAT:
                    mPlayMode = PLAYMODE_ALL_REPEAT;
                    break;
            }

            //保存播放模式配置
            mPreferences.edit().putInt("play_mode", mPlayMode).commit();
        }

        /**
         * 返回当前使用的播放模式
         * {@link #PLAYMODE_ALL_REPEAT},
         * {@link #PLAYMODE_RANDOM},
         * {@link #PLAYMODE_SINGLE_REPEAT}
         */
        public int getPlayMode() {
            return mPlayMode;
        }

        /**
         * 根据当前播放模式自动播放下一首歌
         */
        private void autoPlayNext() {
            // 根据播放模式修改poition的位置
            switch (mPlayMode) {
                case PLAYMODE_ALL_REPEAT:
                    // 如果是最后一首歌则返回第0首歌，否则选择下一首歌
                    if (position == audioItems.size() - 1) {
                        position = 0;
                    } else {
                        position++;
                    }
                    break;
                case PLAYMODE_RANDOM:
                    // 在列表中随机选择一首
                    position = new Random().nextInt(audioItems.size());
                    break;
                case PLAYMODE_SINGLE_REPEAT:
                    // 保持当前的位置
                    break;
            }

            // 使用选中的position播放歌曲
            play();
        }

        private class OnAudioPreparedListener implements MediaPlayer.OnPreparedListener {
            @Override
            public void onPrepared(MediaPlayer mp) {
                //歌曲准备完成
                mediaPlayer.start();

                //显示通知
                showNotification();
                notifyUpdateUI();
            }
        }


//        /**
//         * 当歌曲播放结束,根据当前播放模式,自动播放下一首
//         */
//        private void autoPlayNext() {
//            //根据当前播放修改position的位置
//            switch (mPlayMode) {
//                case PLAYMODE_ALL_REPEAT:
//                    if (position == audioItems.size() - 1) {
//                        position = 0;
//                    } else {
//                        position++;
//                    }
//                    System.out.println("PLAYMODE_ALL_REPEAT");
//                    break;
//                case PLAYMODE_RANDOM:
//                    position = new Random().nextInt(audioItems.size());
//                    System.out.println("PLAYMODE_RANDOM");
//                    break;
//                case PLAYMODE_SINGLE_REPEAT:
//                    //position位置不变(单曲循环啥都不干,保持当前歌曲)
//                    System.out.println("PLAYMODE_SINGLE_REPEAT");
//                    break;
//            }
//            play();//切换完成play
//        }

        private class OnAudioCompletionListener implements MediaPlayer.OnCompletionListener {
            @Override
            public void onCompletion(MediaPlayer mp) {
                // 歌曲播放结束

                // 自动播放下一首歌
                autoPlayNext();
            }
        }

    }
}
