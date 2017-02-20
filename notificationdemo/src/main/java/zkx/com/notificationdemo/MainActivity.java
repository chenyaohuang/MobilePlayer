package zkx.com.notificationdemo;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RemoteViews;


public class MainActivity extends Activity {

    static final int NOTIFICATION_ID = 0x123;
    NotificationManager nm;
    private Button show;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
//        initView();
    }

//    private void initView() {
//        show = (Button) findViewById(R.id.show);
//        show.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                show();
//            }
//        });
//    }


    public void show(View view) {
        System.out.println("show开始了");

//        showNotification();
        Intent intent = new Intent(MainActivity.this, OtherActivity.class);
        PendingIntent pi = PendingIntent.getActivity(MainActivity.this, 0, intent, 0);
        Notification notify = new Notification.Builder(this)
                .setAutoCancel(true)
                .setTicker(null)
                .setSmallIcon(R.drawable.icon)
                .setContentTitle("搁浅")
                .setContentText("周杰伦")
                .setWhen(System.currentTimeMillis())
                .setContentIntent(pi)
                .build();
        nm.notify(NOTIFICATION_ID, notify);
        System.out.println("show玩了");
    }

    public void cancel(View view) {
//        cancleNotification();
        nm.cancel(NOTIFICATION_ID);
    }


    /**
     * 显示通知
     */
    private void showNotification() {
        // 生成Notification对象
        Notification notification = getCustomNotificationNewApi();

        // 显示通知
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(0, notification);
    }


    /**
     * 使用新API生成一个自定义布局的通知
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private Notification getCustomNotificationNewApi() {
        Notification.Builder builder = new Notification.Builder(this);
        builder.setSmallIcon(R.drawable.icon);
        builder.setTicker("正在播放： beijingbeijing");
        builder.setContent(getRemoteView());
        builder.setOngoing(true);
        return builder.getNotification();
    }


    private RemoteViews getRemoteView() {
        RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.audio_notify);

        //填充文字
        remoteViews.setTextViewText(R.id.audio_notify_tv_title, "ffffrgerg");
        remoteViews.setTextViewText(R.id.audio_notify_tv_aritist, "张可新");

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
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("msg", "我是从通知栏启动的!");
        return PendingIntent.getActivity(this, 2, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    /**
     * 取消通知
     */
    private void cancleNotification() {
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancel(0);
    }

    /**
     * 点击上一曲的响应
     *
     * @return
     */
    public PendingIntent getPreIntent() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("msg", "我是上一曲!");
        return PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    /**
     * 点击下一曲的响应
     *
     * @return
     */
    public PendingIntent getNextIntent() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("msg", "我是下一曲!");
        return PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }


}
