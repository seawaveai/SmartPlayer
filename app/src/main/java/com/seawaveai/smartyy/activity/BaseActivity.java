package com.seawaveai.smartyy.activity;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;

import com.seawaveai.smartyy.R;
import com.seawaveai.smartyy.util.LogUtil;

/**
 * 创建者     laoyoutiao
 * 创建时间   2016/11/21 17:37
 * 描述	      手机影音
 * <p/>
 * 更新者     $Author$
 * 更新时间   $Date$
 * 更新描述   ${TODO}$
 */
public abstract class BaseActivity extends FragmentActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        initView();
        initData();
        initListener();
        regCommBtn();

    }

    /**
     * 获得布局id
     */
    protected abstract int getLayoutId();

    /**
     * 初始化view
     */
    protected abstract void initView();

    /**
     * 初始化数据
     */
    protected abstract void initData();

    /**
     * 初始化监听
     */
    protected abstract void initListener();

    /**
     * 处理公用的按钮
     */
    private void regCommBtn() {
        View back = findViewById(R.id.back);
        if (back!=null){
           back.setOnClickListener(this);
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                finish();
                break;
            default:
                processClick(v);/* 处理其他的点击事件 */
                break;
        }
    }

    /* */
    /**处理除back按钮之外的点击事件*/
    /*---------------  ---------------*/
    /*===============  ===============*/
    /*###############  ###############*/
    /*#######################################*/

    protected abstract void processClick(View v);

    /**打印log*/
    public void logE(String msg){
        LogUtil.logE(getClass().getName(),msg);
    }

}
