package zkx.com.mobileplayer.ui.activity;

import android.net.Uri;
import android.view.View;
import android.widget.MediaController;
import android.widget.VideoView;

import zkx.com.mobileplayer.R;
import zkx.com.mobileplayer.bean.VideoItem;

/**
 * Created by zhang on 2016/8/1.
 */
public class VideoPlayerActivity extends BaseActivity {
    private VideoView videoView;

    @Override
    public int getLayoutId() {
        return R.layout.videoplayer;
    }

    @Override
    public void initView() {
        videoView = (VideoView) findViewById(R.id.videoplayer_videoview);
    }

    @Override
    public void initListener() {

    }

    @Override
    public void initData() {
        //获取数据,初始化界面
        VideoItem videoItem = (VideoItem) getIntent().getSerializableExtra("videoItem");
        logE("VideoPlayerActivity.initData: item="+videoItem);

        videoView.setVideoURI(Uri.parse(videoItem.getPath()));
//        videoView.setVideoPath(videoItem.getPath());
        videoView.start();
        videoView.setMediaController(new MediaController(this));
    }

    @Override
    public void processClick(View v) {

    }
}
