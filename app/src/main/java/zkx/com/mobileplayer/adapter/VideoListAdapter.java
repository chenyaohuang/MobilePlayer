package zkx.com.mobileplayer.adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import android.text.format.Formatter;

import zkx.com.mobileplayer.R;
import zkx.com.mobileplayer.bean.VideoItem;
import zkx.com.mobileplayer.util.StringUtils;

/**
 * Created by zhang on 2016/7/31.
 */
public class VideoListAdapter extends CursorAdapter {


    public VideoListAdapter(Context context, Cursor c, boolean autoRequery) {
        super(context, c, autoRequery);
    }

    public VideoListAdapter(Context context, Cursor c) {
        super(context, c);
    }

    public VideoListAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        //生成新的view
        View view = View.inflate(context, R.layout.main_video_list_item, null);
        ViewHolder holder = new ViewHolder(view);

        view.setTag(holder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        //填充view
        ViewHolder holder = (ViewHolder) view.getTag();

        VideoItem videoItem = VideoItem.instanceFromCursor(cursor);

        holder.tv_title.setText(videoItem.getTitle());
        holder.tv_duration.setText(StringUtils.formatDuration(videoItem.getDuration()));
        holder.tv_size.setText(Formatter.formatFileSize(context, videoItem.getSize()));
    }

    private class ViewHolder {
        TextView tv_title, tv_duration, tv_size;

        public ViewHolder(View root) {
            tv_title = (TextView) root.findViewById(R.id.main_video_item_tv_title);
            tv_duration = (TextView) root.findViewById(R.id.main_video_item_tv_duration);
            tv_size = (TextView) root.findViewById(R.id.main_video_item_tv_size);
        }
    }
}
