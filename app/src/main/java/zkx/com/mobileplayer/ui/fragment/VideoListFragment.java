package zkx.com.mobileplayer.ui.fragment;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.provider.MediaStore.Video.Media;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;


import zkx.com.mobileplayer.R;
import zkx.com.mobileplayer.adapter.VideoListAdapter;
import zkx.com.mobileplayer.bean.VideoItem;
import zkx.com.mobileplayer.db.MobileAsyncQueryHandler;
import zkx.com.mobileplayer.ui.activity.VitamioPlayerActivity;

/**
 * Created by zhang on 2016/7/29.
 */
public class VideoListFragment extends BaseFragment {

    private ListView listView;
    private VideoListAdapter mAdapter;

    @Override
    public int getLayoutId() {
        return R.layout.main_video_list;
    }

    @Override
    public void initView() {
        listView = (ListView) findViewById(R.id.simple_listview);
//        new NewAsyncTask().execute();
    }

    @Override
    public void initListener() {
        mAdapter = new VideoListAdapter(getActivity(), null);
        listView.setAdapter(mAdapter);
        listView.setOnItemClickListener(new OnVideoItemClickListener());

    }


    @Override
    public void initData() {
        //从MediaProvider里查询视频信息
        ContentResolver resolver = getActivity().getContentResolver();
//        Cursor cursor = resolver.query(Media.EXTERNAL_CONTENT_URI, new String[]{Media._ID,Media.DATA, Media.TITLE,Media.SIZE, Media.DURATION}, null, null, null);
//        CursorUtils.printCursor(cursor);
//        mAdapter.swapCursor(cursor);
        //使用子线程执行查询操作
        //_ID:1视频id;DATA:2存储路径;TITLE:3视频名称;SIZE:4视频大小;DURATION:5视频时长
        AsyncQueryHandler asyncQueryHandler = new MobileAsyncQueryHandler(resolver);
        asyncQueryHandler.startQuery(0, mAdapter, Media.EXTERNAL_CONTENT_URI, new String[]{Media._ID, Media.DATA, Media.TITLE, Media.SIZE, Media.DURATION}, null, null, null);
    }

    @Override
    public void processClick(View v) {

    }

    private class OnVideoItemClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            //获取被点击数据
            Cursor cursor = (Cursor) parent.getItemAtPosition(position);
//            VideoItem videoItem = VideoItem.instanceFromCursor(cursor);
            ArrayList<VideoItem> videoItems = VideoItem.instanceListFromCursor(cursor);
            //跳转到播放界面
//            Intent intent = new Intent(getActivity(), VideoPlayerActivity.class);
            Intent intent = new Intent(getActivity(), VitamioPlayerActivity.class);

//            intent.putExtra("videoItem",videoItem);
            intent.putExtra("videoItems", videoItems);
            intent.putExtra("position", position);
            startActivity(intent);
        }
    }


}
