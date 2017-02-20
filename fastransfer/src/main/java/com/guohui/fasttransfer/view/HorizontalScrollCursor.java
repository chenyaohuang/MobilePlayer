package com.guohui.fasttransfer.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.guohui.fasttransfer.aty.SendActivity;

/**
 * Created by nangua on 2016/5/21.
 */
public class HorizontalScrollCursor extends View implements ViewPager.OnPageChangeListener{
    private Context context;
    private ViewPager mViewPager;
    private float lineStartX;
    private int startX;
    private int lastPosition = 0 ;

    private float lineScale = 0 ;
    private float lineEndX = 0  ;

    //在XML中设定的参数
    private int width = 0;//控件总宽度
    private int height = 0;//控件总高度
    private int padingLeft = 0;//设置的padingLeft
    private int padingRight = 0;//设置的padingRight

    //间隔
    private int space = 0 ;//默认为0
    //指定的页面个数
    private int count = 0;//!!!这里后面应该改为set方法得到

    //Cursor的画笔
    private Paint linePaint;

    //测量参数
    private void measureSize(){
        width = getWidth(); //得到宽度
        DEFAULT_CURSOR_WIDTH = width/count;
        strX  = new float[count];
        for (int i = 0;i<count;i++) {
            strX[i] = DEFAULT_CURSOR_WIDTH*i;
        }
        height = getHeight();//得到设定的高度
        padingLeft = getPaddingLeft();   //得到的是40
        padingRight = getPaddingRight();  //得到padingRight
    }

    private int DEFAULT_CURSOR_WIDTH  ;//默认游标宽度
    private float [] strX  ;
    /**
     * 测量下划线的位置
     */
    private void measureLineSize(){
        lineStartX = strX[lastPosition]   + (DEFAULT_CURSOR_WIDTH   )* lineScale + space/2;
        if ((lastPosition + 1) == mViewPager.getAdapter().getCount()) {
            lineEndX = strX[lastPosition] + DEFAULT_CURSOR_WIDTH   - space/2;
        }else {
            lineEndX = strX[lastPosition] + DEFAULT_CURSOR_WIDTH   + (DEFAULT_CURSOR_WIDTH  ) * lineScale - space/2;
        }
    }

    //默认的颜色和宽度，应该改为可设置的
    private  static final int DEFULT_LINE_COLOR = Color.rgb(255,135,47);
    private static final int DEFULT_LINE_STRO = 5;

    /**
     * 初始化画笔
     * @param context
     */
    private void init(Context context){
        this.context = context ;
        linePaint = new Paint();
        linePaint.setColor(DEFULT_LINE_COLOR);
        linePaint.setStyle(Paint.Style.FILL);
        linePaint.setStrokeWidth(80);
    }

    /**
     * 设置游标间距
     * @param space
     */
    public void setspace(int space){
        this.space = space ;
    }


    public HorizontalScrollCursor(Context context) {
        super(context);
        init(context);
    }

    public HorizontalScrollCursor(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public HorizontalScrollCursor(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        measureSize();  //测量数值
        measureLineSize();//测量下划线的位置
        drawLine(canvas);//画下划线

    }

    private void drawLine(Canvas canvas){
        canvas.drawLine(lineStartX, height, lineEndX, height, linePaint);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        this.lastPosition = position ;
        lineScale = positionOffset ;

        invalidate();
    }

    @Override
    public void onPageSelected(int position) {
        callback.CheckPage(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    public void setViewPager(ViewPager mViewPager){
        this.mViewPager = mViewPager;
        if (this.mViewPager != null) {
            this.mViewPager.setOnPageChangeListener(this);
        }
        count = mViewPager.getAdapter().getCount();
    }


    onViewPagerChanggedListner callback;

    public void setcallback(Activity activity) {
        callback = (onViewPagerChanggedListner) activity;
    }

    public interface onViewPagerChanggedListner {
        void CheckPage(int position);
    }
}
