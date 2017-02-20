package zkx.com.mobileplayer.adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import zkx.com.mobileplayer.R;
import zkx.com.mobileplayer.bean.AudioItem;

/**
 * Created by zhang on 2016/7/31.
 */
public class AudioListAdapter extends CursorAdapter {

    private Context context;
    /**
     * 音频链接的数据
     */
    private String[] audioUrls;

    public AudioListAdapter(Context context, Cursor c) {
        super(context, c);
    }

    public AudioListAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    public AudioListAdapter(Context context, Cursor c, boolean autoRequery) {
        super(context, c, autoRequery);
    }


    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        //生成新的view
        View view = View.inflate(context, R.layout.main_audio_list_item, null);
        ViewHolder holder = new ViewHolder(view);
        view.setTag(holder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder holder = (ViewHolder) view.getTag();
        AudioItem audioItem = AudioItem.instanceFromCursor(cursor);

        holder.tv_title.setText(audioItem.getTitle());
        holder.tv_artist.setText(audioItem.getArtist());
    }

    private class ViewHolder {
        TextView tv_title, tv_artist;
        ImageView iv_icon_audio;

        public ViewHolder(View root) {
            tv_title = (TextView) root.findViewById(R.id.main_audio_item_tv_title);
            tv_artist = (TextView) root.findViewById(R.id.main_audio_item_tv_artist);
            iv_icon_audio = (ImageView) root.findViewById(R.id.iv_icon_audio);
        }
    }


}
