package zkx.com.mobileplayer.db;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.database.Cursor;
import android.widget.CursorAdapter;

/**
 * Created by zhang on 2016/7/31.
 */
public class MobileAsyncQueryHandler extends AsyncQueryHandler{
    public MobileAsyncQueryHandler(ContentResolver cr) {
        super(cr);
    }

    @Override
    protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
        super.onQueryComplete(token, cookie, cursor);

        CursorAdapter adapter = (CursorAdapter) cookie;
        adapter.swapCursor(cursor);
    }
}
