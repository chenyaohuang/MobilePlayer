package zkx.com.mobileplayer.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import zkx.com.mobileplayer.R;
import zkx.com.mobileplayer.ui.UIInterface;
import zkx.com.mobileplayer.util.LogUtils;

/**
 * Created by zhang on 2016/7/29.
 */
public abstract class BaseFragment extends Fragment implements View.OnClickListener,UIInterface{
    private View view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = View.inflate(getActivity(),getLayoutId(),null);
        initView();
        initListener();
        initData();

        regCommBtn();
        return view;
    }
    /** 返回viewId引用的view*/
    protected View findViewById(int viewId) {
        return view.findViewById(viewId);
    }

    /**
     * 在多个界面间都存在的按钮,点击事件已经由Base处理，那么将点击事件注册也统一处理掉
     */
    private void regCommBtn() {
        View view = findViewById(R.id.back);
        if(view != null){
            view.setOnClickListener(this);
        }
    }



    @Override
    public void onClick(View v) {
        //把多个界面间处理都存在的点击,统一处理掉
        switch (v.getId()){
            case R.id.back:
                getFragmentManager().popBackStack();
                break;
            default:
                processClick(v);
                break;
        }
    }

    /**
     * 显示一个内容为msg的吐司
     * @param msg
     */
    protected void toast(String msg){
        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
    }

    /**
     * 显示一个内容为msgId引用的String的吐司
     * @param msgId
     */
    protected void toast(int msgId){
        Toast.makeText(getActivity(),msgId,Toast.LENGTH_SHORT).show();
    }

    protected void logE(String msg){
        LogUtils.e(getClass(), msg);
    }

}
