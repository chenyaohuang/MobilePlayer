package zkx.com.mobileplayer.ui.activity;

import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.TextView;

import com.nineoldandroids.view.ViewHelper;
import com.nineoldandroids.view.ViewPropertyAnimator;

import java.util.ArrayList;
import java.util.List;

import zkx.com.mobileplayer.R;
import zkx.com.mobileplayer.adapter.MainPagerAdapter;
import zkx.com.mobileplayer.ui.fragment.AudioListFragment;
import zkx.com.mobileplayer.ui.fragment.VideoListFragment;

public class MainActivity extends BaseActivity {

    private ViewPager viewPager;
    private List<Fragment> fragments;
    private MainPagerAdapter mAdapter;
    private TextView tv_video;
    private TextView tv_audio;
    private View indicate_line;

    @Override
    public int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    public void initView() {
        viewPager = (ViewPager) findViewById(R.id.main_viewpager);
        tv_video = (TextView) findViewById(R.id.main_tv_video);
        tv_audio = (TextView) findViewById(R.id.main_tv_audio);

        indicate_line = (View) findViewById(R.id.main_indicate_line);
    }

    @Override
    public void initListener() {
        tv_video.setOnClickListener(this);
        tv_audio.setOnClickListener(this);

        fragments = new ArrayList<Fragment>();
        mAdapter = new MainPagerAdapter(getSupportFragmentManager(), fragments);
        viewPager.setAdapter(mAdapter);

        viewPager.setOnPageChangeListener(new OnMainPageChangeListener());
    }

    @Override
    public void initData() {
        fragments.add(new VideoListFragment());
        fragments.add(new AudioListFragment());

        mAdapter.notifyDataSetChanged();

        // 高亮第一个标签
        updateTabs(0);

        //初始化指示器宽度(1,拿到屏幕宽度)
        int screenW = getWindowManager().getDefaultDisplay().getWidth();
        indicate_line.getLayoutParams().width = screenW / fragments.size();
        indicate_line.requestLayout();//重新计算界面大小，并刷新界面
        // (invalidate,不会重新计算界面大小,只会刷新界面)

    }

    @Override
    public void processClick(View v) {

        switch (v.getId()){
            case R.id.main_tv_video:
                viewPager.setCurrentItem(0);
                break;
            case R.id.main_tv_audio:
                viewPager.setCurrentItem(1);
                break;
        }

    }

    private class OnMainPageChangeListener implements ViewPager.OnPageChangeListener {
        @Override
        /** 当touch事件发生时回调此方法 */
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            //偏移位置 = 手指划过屏幕的百分比*指示器宽度
//            int offsetX = (int) (positionOffset * indicate_line.getWidth());
            //偏移位置2 =手指划过屏幕的像素 /pager个数
            int offsetX = positionOffsetPixels / fragments.size();
            //起始位置 = position * 指示器宽度
            int startX = position * indicate_line.getWidth();
            //指示器移动的位置 = 起始位置 + 偏移位置
            int translationX = startX + offsetX;
            ViewHelper.setTranslationX(indicate_line,translationX);
        }

        @Override
        /** 当见面选中状态发生变更时会回调此方法 */
        public void onPageSelected(int position) {
            // 高亮选中页面对应的标签，并将其他的变暗
            updateTabs(position);
        }

        @Override
        /** 当页面的滑动状态发生变更会回调此方法*/
        public void onPageScrollStateChanged(int state) {

        }
    }

    /** 高亮position选中页面对应的标签，并将其他的变暗 */
    private void updateTabs(int position) {
        updateTab(position, 0, tv_video);
        updateTab(position, 1, tv_audio);
    }

    /** 判断当前要处理的 tabPosition 是否是选中的 position，并修改tab的高亮状态 */
    private void updateTab(int position, int tabPosition, TextView tab) {
        int green = getResources().getColor(R.color.green);
        int halfWhite = getResources().getColor(R.color.halfwhite);

        if (position == tabPosition){
            // tab对应的页面被选中
            tab.setTextColor(green);
            ViewPropertyAnimator.animate(tab).scaleX(1.2f).scaleY(1.2f);
        }else {
            // tab对应的页面没有被选中
            tab.setTextColor(halfWhite);
            ViewPropertyAnimator.animate(tab).scaleX(1.0f).scaleY(1.0f);
        }
    }
}
