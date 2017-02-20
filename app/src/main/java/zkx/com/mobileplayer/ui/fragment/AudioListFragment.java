package zkx.com.mobileplayer.ui.fragment;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.provider.MediaStore.Audio.Media;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

import zkx.com.mobileplayer.R;
import zkx.com.mobileplayer.adapter.AudioListAdapter;
import zkx.com.mobileplayer.bean.AudioItem;
import zkx.com.mobileplayer.db.MobileAsyncQueryHandler;
import zkx.com.mobileplayer.ui.activity.AudioPlayerActivity;


/**
 * Created by zhang on 2016/7/29.
 */
public class AudioListFragment extends BaseFragment {

    private ListView listView;
    private AudioListAdapter mAdapter;

    @Override
    public int getLayoutId() {
        return R.layout.main_audio_list;
    }

    @Override
    public void initView() {
        listView = (ListView) findViewById(R.id.simple_listview);
    }

    @Override
    public void initListener() {
        //初始化listener的时候还没有数据
        mAdapter = new AudioListAdapter(getActivity(), null);
        listView.setAdapter(mAdapter);
        listView.setOnItemClickListener(new OnAudioItemClickListener());
    }

    @Override
    public void initData() {
        //从MediaProvider查询数据
        ContentResolver resolver = getActivity().getContentResolver();

     /*  主线程查询不太合适,换子线程查询
      //系统封装了Api(_ID:1歌曲id;DATA:2歌曲存储路径;DIAPLAY_NAME:3歌曲展示名;ARTIST:4艺术家)
        Cursor cursor = resolver.query(Media.EXTERNAL_CONTENT_URI, new String[]{Media._ID,Media.DATA,Media.DISPLAY_NAME,Media.ARTIST},null,null,null);
//        CursorUtils.printCursor(cursor);
        //查到cursor，切换cursor,把最新查到的adapter放进去,cursor更新,数据更新
        mAdapter.swapCursor(cursor);//swap:交换*/

        AsyncQueryHandler asyncQueryHandler = new MobileAsyncQueryHandler(resolver);

        /*token:用来区分不同查询次数,随便放一个,
        cookie要接收一个cursoradpter,对应MobileAsyncQueryHandler里面
        的onQueryComplete方法的cookie*/
        //startQuery会开启子线程去查询,
        /*注:Message里面的what用来区分不同的消息,那么token也可以用来区分不同的查询,
        *cookie跟Message的obj差不多,Message的obj是用来传递一个对象的*/
        asyncQueryHandler.startQuery(1, mAdapter, Media.EXTERNAL_CONTENT_URI, new String[]{Media._ID, Media.DATA, Media.DISPLAY_NAME, Media.ARTIST}, null, null, null);

    }

    @Override
    public void processClick(View v) {

    }

    private class OnAudioItemClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            //获取被点击的数据(CursorAdapter点击后返回的是Cursor)
            Cursor cursor = (Cursor) parent.getItemAtPosition(position);
            ArrayList<AudioItem> audioItems = AudioItem.instanceListFromCursor(cursor);

            //获取到数据后跳转到播放界面
            Intent intent = new Intent(getActivity(), AudioPlayerActivity.class);
            intent.putExtra("audioItems", audioItems);
            intent.putExtra("position", position);
            startActivity(intent);
        }
    }
}
