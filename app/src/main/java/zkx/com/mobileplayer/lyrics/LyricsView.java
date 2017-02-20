package zkx.com.mobileplayer.lyrics;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.TextView;


import java.io.File;
import java.util.ArrayList;
import java.util.List;

import zkx.com.mobileplayer.R;

/**
 * Created by zhang on 2016/11/1.
 */
public class LyricsView extends TextView {
    private Paint mPaint;
    private float mHighLightSize;
    private float mNormalSize;
    private int mHightLightColor;
    private int mNormalColor;
    private int mHalfViewW;
    private int mHalfViewH;
    private List<Lyrics> mLyricsList;
    private int mCurrentLine;
    private int mLineHight;
    private int mDuration;
    private int mPosition;

    public LyricsView(Context context) {
        super(context);
        initView();

    }

    public LyricsView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public LyricsView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        //getDimension()更安全，可以在不同的屏幕上进行适配
        mHighLightSize = getResources().getDimension(R.dimen.lyric_highlight_size);//直接给他一个确定的值,不同的屏幕适配不好
        mNormalSize = getResources().getDimension(R.dimen.lyric_normal_size);//直接给他一个确定的值,不同的屏幕适配不好
        mLineHight = getResources().getDimensionPixelSize(R.dimen.lyric_line_height);
        mHightLightColor = Color.WHITE;
        mNormalColor = Color.GRAY;

        mPaint = new Paint();
        mPaint.setAntiAlias(true);//抗锯齿(平滑)
        mPaint.setTextSize(mHighLightSize);//文字大小
        mPaint.setColor(mHightLightColor);//文字颜色

//        //模拟初始化数据
//        mLyricsList = new ArrayList<>();
//        for (int i = 0; i < 30; i++) {//循环一次添加一行歌词
//            mLyricsList.add(new Lyrics(i * 2000, "当前歌词行数是:" + i));
//        }
//
//        //高亮行
//        mCurrentLine = 15;
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mHalfViewW = w / 2;
        mHalfViewH = h / 2;
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mLyricsList == null || mLyricsList.size() == 0) {
            drawSingleText(canvas);//绘制单行文本
        } else {
            drawMulitText(canvas);//绘制多行文本
        }

    }

    /**
     * 根据当前歌词数据列表和高亮行，绘制多行歌词文本
     */
    private void drawMulitText(Canvas canvas) {
        Lyrics lyrics = mLyricsList.get(mCurrentLine);
        int endPoint;
        if (mCurrentLine == mLyricsList.size() - 1) {
            //最后一行
            endPoint = mDuration;
        } else {
            Lyrics nextLyric = mLyricsList.get(mCurrentLine + 1);
            endPoint = nextLyric.getStartPoint();
        }

        int lineTime = endPoint - lyrics.getStartPoint();
        int pastTime = mPosition - lyrics.getStartPoint();
        float pastPercent = pastTime / (float) lineTime;
        int offsetY = (int) (pastPercent * mLineHight);
//        canvas.translate(0,-offsetY);
        //获取高亮行的Y位置
        Rect bounds = new Rect();
        mPaint.getTextBounds(lyrics.getContent(), 0, lyrics.getContent().length(), bounds);
        int halfTextH = bounds.height() / 2;
        int centerY = mHalfViewH + halfTextH - offsetY;//高亮行的y位置
        //按行绘制文本
        for (int i = 0; i < mLyricsList.size(); i++) {
            if (i == mCurrentLine) {//如果当前行是高亮行
                mPaint.setTextSize(mHighLightSize);
                mPaint.setColor(mHightLightColor);
            } else {//否则当前行是普通行
                mPaint.setTextSize(mNormalSize);
                mPaint.setColor(mNormalColor);
            }
            //x=水平居中行的x
            //y=高亮行的y位置+(绘制行的行数-高亮行的行数)*行高
            int drawY = centerY + (i - mCurrentLine) * mLineHight;
            drawHorizontalText(canvas, mLyricsList.get(i).getContent(), drawY);
        }


    }


    /**
     * 在View中间绘制一行文本
     */
    private void drawSingleText(Canvas canvas) {
        String text = "正在加载歌词...";
        //计算文本的一半宽高
        Rect bounds = new Rect();
        mPaint.getTextBounds(text, 0, text.length(), bounds);
        int halfTextH = bounds.height() / 2;
        int drawY = mHalfViewH + halfTextH;

        drawHorizontalText(canvas, text, drawY);
    }

    /**
     * 水平绘制一行文本
     */
    private void drawHorizontalText(Canvas canvas, String text, int drawY) {
        //        int halfTextW = bounds.width()/2;
        int halfTextW = (int) (mPaint.measureText(text) / 2);

        /*x=View的一半宽度-Text文字的一半的宽度
        * y=View的一半高度+Text文字的一半的高度*/
        int drawX = mHalfViewW - halfTextW;
        canvas.drawText(text, drawX, drawY, mPaint);
    }

    /**
     * 根据当前播放时间,选择高亮行
     * position:当前时间(当前已播放时间)
     * duration:歌曲时长
     */
    public void roll(int position, int duration) {
        mDuration = duration;
        mPosition = position;
        //满足以下2个条件的为当前播放行(即高亮行)
        //起始时间<=播放时间
        //播放时间<下一行起始时间
        for (int i = 0; i < mLyricsList.size(); i++) {
            Lyrics lyrics = mLyricsList.get(i);
            int endPoint;//这一行的结束时间(即下一行的开始时间)
            if (i == mLyricsList.size() - 1) {
                //当前一行为最后一行
                endPoint = duration;
            } else {
                Lyrics nextLyrics = mLyricsList.get(i + 1);
                endPoint = nextLyrics.getStartPoint();
            }
            if (lyrics.getStartPoint() <= position && endPoint > position) {
                mCurrentLine = i;
            }
        }

        invalidate();
    }

    public void setLyricFile(File lyricFile) {
        mLyricsList = LyricsParser.parserFromFile(lyricFile);
        mCurrentLine = 0;
    }
}
