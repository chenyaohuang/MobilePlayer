package zkx.com.mobileplayer.ui.fragment;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.provider.MediaStore.Video.Media;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import zkx.com.mobileplayer.R;
import zkx.com.mobileplayer.adapter.VideoListAdapter;
import zkx.com.mobileplayer.bean.VideoItem;
import zkx.com.mobileplayer.db.MobileAsyncQueryHandler;
import zkx.com.mobileplayer.ui.activity.VideoPlayerActivity;

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
            VideoItem videoItem = VideoItem.instanceFromCursor(cursor);
            //跳转到播放界面
            Intent intent = new Intent(getActivity(), VideoPlayerActivity.class);
            intent.putExtra("videoItem",videoItem);
            startActivity(intent);
        }
    }
}
