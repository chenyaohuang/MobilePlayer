package zkx.com.mobileplayer.ui.activity;

import android.content.Intent;
import android.os.Handler;
import android.view.View;

import zkx.com.mobileplayer.R;

/**
 * Created by zhang on 2016/7/29.
 */
public class SplashActivity extends BaseActivity {
    @Override
    public int getLayoutId() {
        return R.layout.splash;
    }

    @Override
    public void initView() {

    }

    @Override
    public void initListener() {

    }

    @Override
    public void initData() {

    }

    @Override
    public void processClick(View v) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        //延迟调转到主界面
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashActivity.this,MainActivity.class);
                startActivity(intent);

                finish();
            }
        },2000);
    }
}
