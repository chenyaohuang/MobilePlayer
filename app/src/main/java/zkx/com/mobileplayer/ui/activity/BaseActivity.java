package zkx.com.mobileplayer.ui.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Toast;

import zkx.com.mobileplayer.R;
import zkx.com.mobileplayer.ui.UIInterface;
import zkx.com.mobileplayer.util.LogUtils;

/**
 * 1,规范代码结构
 * 2,提供公用方法
 */
public abstract class BaseActivity extends FragmentActivity implements View.OnClickListener , UIInterface{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());

        initView();
        initListener();
        initData();

        regCommonBtn();
    }

    /** 在多个界面间都存在的按钮，点击事件已经由Base处理，那么将点击事件注册也统一处理掉 */
    private void regCommonBtn() {
        View view = findViewById(R.id.back);
        if (view != null) {
            view.setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View v) {
        // 把在多个界面间都存在的点击，统一处理掉
        switch (v.getId()){
            case R.id.back:
                finish();
                break;
            default:
                processClick(v);
                break;
        }
    }

    /** 显示一个内容为msg的吐司 */
    protected void toast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    /** 显示一个内容为msgid引用的string的吐司 */
    protected void toast(int msgId) {
        Toast.makeText(this, msgId, Toast.LENGTH_SHORT).show();
    }

    /** 打印一个子类的名称为tag的log */
    protected void logE(String msg) {
        LogUtils.e(getClass(), msg);
    }

}
