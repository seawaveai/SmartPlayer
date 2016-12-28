package com.seawaveai.smartyy.activity;

import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.TextView;

import com.nineoldandroids.view.ViewHelper;
import com.nineoldandroids.view.ViewPropertyAnimator;
import com.seawaveai.smartyy.R;
import com.seawaveai.smartyy.adapter.MainAdapter;
import com.seawaveai.smartyy.fragment.AudioFragment;
import com.seawaveai.smartyy.fragment.VideoFragment;

import java.util.ArrayList;

/**
 * 创建者     laoyoutiao
 * 创建时间   2016/11/21 18:03
 * 描述	     主界面
 * <p/>
 * 更新者     $Author$
 * 更新时间   $Date$
 * 更新描述   ${TODO}
 */
public class MainActivity extends BaseActivity {

    private ViewPager mViewPager;
    private TextView mTv_video;
    private TextView mTv_audio;
    private View mView_bar;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView() {
        mTv_video = (TextView) findViewById(R.id.main_tv_video);//video标题
        mTv_audio = (TextView) findViewById(R.id.main_tv_audio);//audio标题
        mView_bar = findViewById(R.id.main_bar);//指示器
        mViewPager = (ViewPager) findViewById(R.id.main_viewpager);
    }

    @Override
    protected void initData() {
        //开始的时候，字体并没有表，
        updataTitle(0);

        //指示器宽度的初始化
        int screenWidth = getWindowManager().getDefaultDisplay().getWidth();
        mView_bar.getLayoutParams().width = screenWidth / 2; //布局中的参数
        mView_bar.requestLayout(); //invalidate(); postInvalidate();
    }

    @Override
    protected void initListener() {
        // ViewPager设置adapter
        ArrayList<Fragment> fragments = new ArrayList<Fragment>();
        fragments.add(new VideoFragment());
        fragments.add(new AudioFragment());
        //在这里需要获取到getSupportFragmentManager所以在 BaseActivity中需要继承FragmentActivity
        MainAdapter adapter = new MainAdapter(getSupportFragmentManager(), fragments);
        mViewPager.setAdapter(adapter);

        //监听viewPger来改变上面的指示器
        //mViewPager.addOnPageChangeListener(mOnPageChangeListener);
        mViewPager.setOnPageChangeListener(mOnPageChangeListener);

        //点击视频、音乐标签后fragment及文字会变
        mTv_video.setOnClickListener(this);
        mTv_audio.setOnClickListener(this);
    }

    @Override
    protected void processClick(View v) {
        switch (v.getId()) {
            case R.id.main_tv_video:
                mViewPager.setCurrentItem(0);
                break;
            case R.id.main_tv_audio:
                mViewPager.setCurrentItem(1);
                break;
            default:
                break;
        }
    }

    private ViewPager.OnPageChangeListener mOnPageChangeListener = new ViewPager.OnPageChangeListener() {
        /* 所在的位置  偏移百分比   偏移像素 */
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            /*  指示器最终的位置 = 初始位置 + 移动位置  */
            /*  初始位置 = 指示器的宽度*positon   */
            /*  移动位置 = 指示器宽带*滑动百分比   */

            //自己求百分比：手指划过的像素/pager的个数

            float start = mView_bar.getWidth() * position;
            float move = mView_bar.getWidth() * positionOffset;
            float end = start + move;
            //ViewPropertyAnimator.animate(mView_bar).translationX(end);
            ViewHelper.setTranslationX(mView_bar, end);//标签跟着动

           // moveAbs = Math.abs(move) - mView_bar.getWidth() / 2;

        }

        @Override
        public void onPageSelected(int position) {/*选中状态的改变*/
            updataTitle(position);
        }

        @Override
        public void onPageScrollStateChanged(int state) {/*滑动状态改变*/
            //SCROLL_STATE_DRAGGING = 1;
            //SCROLL_STATE_SETTLING = 2;
            // SCROLL_STATE_IDLE = 0;
           // System.out.println(state+";;;;");

        }
    };

    private void updataTitle(int position) {/*改变标题*/

        int green = getResources().getColor(R.color.green);
        int white = getResources().getColor(R.color.halfwhite);
        //1.实现文字颜色的改变
        //2.实现文字大小的改变，并且有动画
        //nineoldandroids-2.4.0.jar属性动画的第三方包。add as libs.
        if (position == 0) { /*假如有很多个这样写不是累死人*/
            mTv_video.setTextColor(green);
            mTv_audio.setTextColor(white);

            ViewPropertyAnimator.animate(mTv_video).scaleX(1.5f).scaleY(1.5f);
            ViewPropertyAnimator.animate(mTv_audio).scaleX(1.0f).scaleY(1.0f);

        } else if (position == 1) {
            mTv_video.setTextColor(white);
            mTv_audio.setTextColor(green);

            ViewPropertyAnimator.animate(mTv_video).scaleX(1.0f).scaleY(1.0f);
            ViewPropertyAnimator.animate(mTv_audio).scaleX(1.5f).scaleY(1.5f);
        }

    }


}
