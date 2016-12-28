package com.seawaveai.smartyy.activity;

import android.content.Intent;
import android.os.Handler;
import android.view.View;

import com.seawaveai.smartyy.R;

/**
 * 创建者     laoyoutiao
 * 创建时间   2016/11/21 18:38
 * 描述	      欢迎界面,停留2秒后进入主界面.
 * <p/>
 * 更新者     $Author$
 * 更新时间   $Date$
 * 更新描述   ${TODO}
 */
public class SplashActivity extends BaseActivity {

    @Override
    protected int getLayoutId() {
        return R.layout.activity_splash;
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initListener() {

    }

    @Override
    protected void processClick(View v) {

    }


    @Override
    protected void onResume() {
        super.onResume();
        //延时
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        }, 2000);

        // handler.sendEmptyMessageDelayed(0,2000);

    }

}
