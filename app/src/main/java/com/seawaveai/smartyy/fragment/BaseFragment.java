package com.seawaveai.smartyy.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.seawaveai.smartyy.R;
import com.seawaveai.smartyy.util.LogUtil;

/**
 * 创建者     laoyoutiao
 * 创建时间   2016/11/22 9:19
 * 描述	     自定义findViewById?
 * <p/>
 * 更新者     $Author$
 * 更新时间   $Date$
 * 更新描述   ${TODO}
 */
public abstract class BaseFragment extends Fragment implements View.OnClickListener {

    private View mView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(getLayoutId(), null);
        initView();
        initData();
        initEvent();
        regCommBtn();
        return mView;
    }

    private void regCommBtn() {
        View back = mView.findViewById(R.id.back);
        if (back != null) {
            back.setOnClickListener(this);
        }
    }

    /**
     * 自定findViewById
     */
    public View findViewById(int id) { /* 不要提供view给子类，子类只要findViewById就可以 */
        return mView.findViewById(id);
        //返回Id引用的view，findViewById 需要通过view 来进行
    }

    /**
     * 获取布局id
     */
    protected abstract int getLayoutId();

    /**
     * 初始化view
     */
    protected abstract void initView();

    /**
     * 初始化data
     */
    protected abstract void initData();

    /**
     * 初始化事件
     */
    protected abstract void initEvent();

    /**
     * 处理点击事件
     */
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                 /*处理back事件*/
                getFragmentManager().popBackStack();
                break;
            default:
                 /*处理其他的事件*/
                processBtn(view);
                break;
        }

    }

    /**
     * 处理其他的点击事件
     */
    protected abstract void processBtn(View view);

    /**
     * 打印log
     */
    public void logE(String msg) {
        LogUtil.logE(getClass().getName(), msg);
    }

    /**
     * 用来显示内容为 msg 的吐司
     */
    protected void toast(String msg) {
        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
    }

    /**
     * 用来显示内容为 msgId 所引用的 String 的吐司
     */
    protected void toast(int msgId) {
        Toast.makeText(getActivity(), msgId, Toast.LENGTH_SHORT).show();
    }
}
