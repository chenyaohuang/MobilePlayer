package com.example.jiana.cutvideoframedemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private ListView mListView;
    private String[] videoUrls = Videos.imageUrls;
    private VideoImageAdapter adapter;

    /**
     * 视频帧图片截取器
     */
    private VideoFrameImageLoader mVideoFrameImageLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mListView = (ListView) findViewById(R.id.lvVideoImg);
        mVideoFrameImageLoader = new VideoFrameImageLoader(this, mListView, videoUrls);
        adapter = new VideoImageAdapter(this, mVideoFrameImageLoader);
        mListView.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add("删除缓存");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 0:
                mVideoFrameImageLoader.getFileManager().deleteFile();
                Toast.makeText(getApplication(), "清空缓存成功", Toast.LENGTH_SHORT).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        mVideoFrameImageLoader.cancelTask();
        super.onDestroy();
    }
}
