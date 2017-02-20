package com.example.jiana.cutvideoframedemo.rv;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.jiana.cutvideoframedemo.R;
import com.example.jiana.cutvideoframedemo.VideoFrameImageLoader;
import com.example.jiana.cutvideoframedemo.VideoImageAdapter;
import com.example.jiana.cutvideoframedemo.Videos;

public class Main2Activity extends AppCompatActivity {
    private RecyclerView rvList;
    private String[] videoUrls = Videos.imageUrls;
    private VideoImageRVAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        rvList = (RecyclerView) findViewById(R.id.rvList);
        rvList.setLayoutManager(new LinearLayoutManager(this));
        VideoFrameImageLoader mVideoFrameImageLoader = new VideoFrameImageLoader(this, rvList, videoUrls);
        adapter = new VideoImageRVAdapter(this, mVideoFrameImageLoader);
        rvList.setAdapter(adapter);
    }
}
