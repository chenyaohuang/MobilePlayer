package zkx.com.mobileplayer.adapter;

import android.content.Context;
import android.database.Cursor;

import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import android.text.format.Formatter;
import zkx.com.mobileplayer.R;
import zkx.com.mobileplayer.bean.VideoItem;
import zkx.com.mobileplayer.util.ImageLoader;
import zkx.com.mobileplayer.util.StringUtils;


/**
 * Created by zhang on 2016/7/31.
 */
public class VideoListAdapter extends CursorAdapter {
    private ImageLoader mImageLoader;
    private Context context;
    /**
     * 视频链接的数据
     */
    private String[] videoUrls;

    public VideoListAdapter(Context context, Cursor c) {
        super(context, c);
        mImageLoader = new ImageLoader();

    }

    public VideoListAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
        mImageLoader = new ImageLoader();
    }

    public VideoListAdapter(Context context, Cursor c, boolean autoRequery) {
        super(context, c, autoRequery);
        mImageLoader = new ImageLoader();
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

        ViewHolder holder = (ViewHolder) view.getTag();
        VideoItem videoItem = VideoItem.instanceFromCursor(cursor);

        //填充view
//        mmr = new MediaMetadataRetriever();
//        String path = videoItem.getPath();
////        mmr.setDataSource(path);
//        Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(path, MediaStore.Video.Thumbnails.MICRO_KIND);
////                        bitmap = LoadBitmapUtils.decodeSampledBitmapFromResource(path, 210, 210);
//        //压缩
//        bitmap = ThumbnailUtils.extractThumbnail(bitmap, 100, 100);
//        String path = videoItem.getPath();
//
//        mBitmapUtils.display(view,path);
//        VideoItem videoItem = (VideoItem) list.get(cursor.getPosition());

        holder.iv_icon_video.setImageResource(R.drawable.video_default_icon);
        String url = videoItem.getPath();
        holder.iv_icon_video.setTag(url);
//        new ImageLoader().showImageByThread(holder.iv_icon_video, url);
        mImageLoader.showImageByAsyncTask(holder.iv_icon_video, url);
        holder.tv_title.setText(videoItem.getTitle());
        holder.tv_duration.setText(StringUtils.formatDuration(videoItem.getDuration()));
        holder.tv_size.setText(Formatter.formatFileSize(context, videoItem.getSize()));


    }

    private class ViewHolder {
        TextView tv_title, tv_duration, tv_size;
        ImageView iv_icon_video;

        public ViewHolder(View root) {

            tv_title = (TextView) root.findViewById(R.id.main_video_item_tv_title);
            tv_duration = (TextView) root.findViewById(R.id.main_video_item_tv_duration);
            tv_size = (TextView) root.findViewById(R.id.main_video_item_tv_size);
            iv_icon_video = (ImageView) root.findViewById(R.id.iv_icon_video);
        }
    }


}
