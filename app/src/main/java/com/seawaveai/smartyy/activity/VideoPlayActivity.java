package com.seawaveai.smartyy.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.nineoldandroids.view.ViewHelper;
import com.nineoldandroids.view.ViewPropertyAnimator;
import com.seawaveai.smartyy.R;
import com.seawaveai.smartyy.bean.VideoItem;
import com.seawaveai.smartyy.util.StringUtil;

import java.lang.ref.SoftReference;
import java.util.ArrayList;

/**
 * 创建者     laoyoutiao
 * 创建时间   2016/11/23 20:21
 * 描述	     在VideoFragment界面点击item,进行视频播放的界面.
 * <p/>
 * 1.怎么知道是level字段？
 * 2.防止内存泄漏：handler.removeCallbacksAndMessages(null);
 * 为什么说在Application里面，可能长时间后台运行，再次重启会崩溃。
 * <p/>
 * 定时隐藏,如果在onTouch里处理会有问题,要分别处理.||开始进来不操作,就会一直显示
 */
public class VideoPlayActivity extends BaseActivity {

    private VideoView mVideo_videoview;
    private ImageView mVideo_player_playstate;
    private TextView mVideo_player_name;
    private ImageView mVideo_player_battery;
    private TextView mVideo_player_time;
    private batteryIconReceiver mReceiver;
    private SeekBar mVideo_player_volume_sk, mVideo_player_progress_sk;
    private AudioManager mAudioManager;
    private ImageView mVideo_player_mute;
    private int mMarkVolume;

    private static final int UPDATA_SYSTEM_TIME = 0;//更新系统时间
    private static final int UPDATA_PROGRESS = 1;//更新视频播放的进度
    private static final int MEG_Show_Hide = 2;//隐藏上下栏

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UPDATA_SYSTEM_TIME:
                    startUpdateTime();
                    break;
                case UPDATA_PROGRESS:
                    startUpdateProgress();
                    break;
                case MEG_Show_Hide:
                    hide();
                    break;
            }
        }
    };
    private ArrayList<VideoItem> mVideoItems;
    private int mPosition;
    private ImageView mVideo_player_pre;
    private ImageView mVideo_player_next;
    private ImageView mVideo_player_screenstate;
    private int mDefaultWidth;
    private int mDefaultHeight;
    private boolean isFullScreen;


    static class myHandler extends Handler { //消除Handler的警号线。
        private SoftReference<Context> srf;

        public myHandler(Context context) {
            srf = new SoftReference<Context>(context);
        }

        @Override
        public void handleMessage(Message msg) {
            Toast.makeText(srf.get(), "消除警号线", Toast.LENGTH_SHORT).show();
            super.handleMessage(msg);
        }
    }

    private float downY;
    private int mScreenHeight;
    private int mStartVolue;
    private View mVideo_player_cover;
    private int mScreenWidth;
    private TextView mVideo_player_duration;
    private TextView mVideo_player_progress;
    private LinearLayout mVideo_player_linear_top, mVideo_player_linear_bottom;
    private GestureDetector mGesture;
    private boolean showing;
    private int mTopHeight;
    private int mBotHeight;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_video_play;
    }

    @Override
    protected void initView() {
        mVideo_videoview = (VideoView) findViewById(R.id.video_videoview);
        mVideo_player_playstate = (ImageView) findViewById(R.id.video_player_playstate);

        mVideo_player_name = (TextView) findViewById(R.id.video_player_name);
        mVideo_player_battery = (ImageView) findViewById(R.id.video_player_battery);
        mVideo_player_time = (TextView) findViewById(R.id.video_player_time);
        mVideo_player_volume_sk = (SeekBar) findViewById(R.id.video_player_volume_sk);
        mVideo_player_mute = (ImageView) findViewById(R.id.video_player_mute);
        mVideo_player_cover = findViewById(R.id.video_player_cover);

        mVideo_player_progress_sk = (SeekBar) findViewById(R.id.video_player_progress_sk);
        mVideo_player_duration = (TextView) findViewById(R.id.video_player_duration);
        mVideo_player_progress = (TextView) findViewById(R.id.video_player_progress);

        mVideo_player_linear_top = (LinearLayout) findViewById(R.id.video_player_linear_top);
        mVideo_player_linear_bottom = (LinearLayout) findViewById(R.id.video_player_linear_bottom);

        mVideo_player_pre = (ImageView) findViewById(R.id.video_player_pre);
        mVideo_player_next = (ImageView) findViewById(R.id.video_player_next);

        mVideo_player_screenstate = (ImageView) findViewById(R.id.video_player_screenstate);

    }
    @Override
    protected void initData() {
        //VideoItem videoItem = (VideoItem) getIntent().getSerializableExtra("VideoItem");


        Uri uri = getIntent().getData();
        if (uri!=null){ //响应点击视频文件,从而播放
            mVideo_videoview.setVideoPath(uri.getPath());
            mVideo_player_name.setText(uri.getPath());
            updatePreAndNextBtn();
        }else{
            mPosition = getIntent().getIntExtra("Position", -1);
            mVideoItems = (ArrayList<VideoItem>) getIntent().getSerializableExtra("VideoItems");
            playItem();
        }

        mScreenHeight = getWindowManager().getDefaultDisplay().getHeight();
        mScreenWidth = getWindowManager().getDefaultDisplay().getWidth();
        mGesture = new GestureDetector(new mySimpleOnGestureListener()); //监听点击的次数.


        //mVideo_videoview.start();//查看生命周期知道，如果文件比较大时，有可能还没加载到内存就开始播放
        mVideo_videoview.setOnPreparedListener(new mOnPreparedListener());//准备好了的监听.


        // 注册广播来获取电量的改变
        mReceiver = new batteryIconReceiver();
        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);//Intent.ACTION_BATTERY_OKAY
        registerReceiver(mReceiver, intentFilter);

        startUpdateTime();/*更新系统时间*/

        //获取最大音量值 和 当前值赋值给SeekBar
        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        int maxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        mVideo_player_volume_sk.setMax(maxVolume);//获取系统的最大值，赋值给sk
        int currentVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        updateVolumeSk(currentVolume);


        ViewHelper.setAlpha(mVideo_player_cover, 0.2f);/*设置默认亮度值*/

        //获取上下栏的高度;
        //获取真实的高度（1）
        mVideo_player_linear_top.measure(0, 0);
        mTopHeight = mVideo_player_linear_top.getMeasuredHeight();
        //获取真实的高度（2）
        mVideo_player_linear_bottom.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mBotHeight = mVideo_player_linear_bottom.getHeight();
                mVideo_player_linear_top.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                //SurfaceView极其子类除外.
            }
        });

    }

    private void playItem() {

        updatePreAndNextBtn();//上一曲和下一曲按钮的处理

        if (mPosition == -1 || mVideoItems == null || mVideoItems.size() == 0)
            return;

        mVideo_videoview.setVideoPath(mVideoItems.get(mPosition).getPath());
        mVideo_player_name.setText(mVideoItems.get(mPosition).getName());
    }

    private void updatePreAndNextBtn() {
        //可以简写.
        if (mPosition == -1 || mVideoItems == null || mVideoItems.size() == 0 || mPosition == 0) {
            mVideo_player_pre.setEnabled(false);
        } else {
            mVideo_player_pre.setEnabled(true);
        }

        if (mPosition == -1 || mVideoItems == null || mVideoItems.size() == 0 || mPosition == mVideoItems.size() - 1) {
            mVideo_player_next.setEnabled(false);
        } else {
            mVideo_player_next.setEnabled(true);
        }

    }

    private void updateVolumeSk(int currentVolume) {
        mVideo_player_volume_sk.setProgress(currentVolume);
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, currentVolume, 0);
    }

    private void startUpdateTime() {
        mVideo_player_time.setText(StringUtil.parseSystemTime());
        handler.sendEmptyMessageDelayed(UPDATA_SYSTEM_TIME, 500);
    }

    /*private void startUpdateTime() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mVideo_player_time.setText(StringUtil.parseSystemTime());
                mVideo_player_time.requestLayout();
            }
        },500);
    }*/

    @Override
    protected void initListener() {
        mVideo_player_playstate.setOnClickListener(this);// 播放/暂停按钮

        mVideo_videoview.setOnCompletionListener(new mOnCompletionListener());//视频播放完了的监听

        mVideo_player_volume_sk.setOnSeekBarChangeListener(mOnSeekBarChangeListener);//音量SeekBak改变的监听

        mVideo_player_mute.setOnClickListener(this);//静音
        mVideo_player_pre.setOnClickListener(this); //上
        mVideo_player_next.setOnClickListener(this);//下

        mVideo_player_progress_sk.setOnSeekBarChangeListener(mOnSeekBarChangeListener);//播放进度SeekBar改变的监听

        mVideo_player_screenstate.setOnClickListener(this);
    }

    @Override
    protected void processClick(View v) {
        switch (v.getId()) {
            case R.id.video_player_playstate:
                updatePlayState();//更新播放状态
                break;
            case R.id.video_player_mute:
                if (mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC) != 0) {
                    //非0   记录当前值   设为0
                    mMarkVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                    updateVolumeSk(0);
                } else {
                    //是0   设为记录的值
                    updateVolumeSk(mMarkVolume);
                }
                break;
            case R.id.video_player_pre:
                playPre();
                break;
            case R.id.video_player_next:
                playNext();
                break;
            case R.id.video_player_screenstate:
               // mVideo_videoview.switchScreen();
              //  updateScreenImgBtn();

                switchScreen();
                break;
        }
    }

    //全屏的切换,
    private void switchScreen() {

        if (isFullScreen) {
            mVideo_videoview.getLayoutParams().width = mDefaultWidth;
            mVideo_videoview.getLayoutParams().height = mDefaultHeight;

        } else {
            mDefaultWidth = mVideo_videoview.getWidth();
            mDefaultHeight = mVideo_videoview.getHeight();

            mVideo_videoview.getLayoutParams().width = mScreenHeight;
            mVideo_videoview.getLayoutParams().height = mScreenWidth;
        }

        isFullScreen = !isFullScreen;
        mVideo_videoview.requestLayout();

        updateScreenImgBtn();
    }

    private void updateScreenImgBtn() {
        if (isFullScreen) {
            mVideo_player_screenstate.setImageResource(R.drawable.video_player_fullscreen_selector);
        } else {
            mVideo_player_screenstate.setImageResource(R.drawable.video_player_defaultscreen_selector);
        }
    }

    private void playNext() {
        //判断是否是最后一个
        //mPosition = (mPosition + 1) % mVideoItems.size();

        if (mPosition == mVideoItems.size() - 1)
            return;
        mPosition++;

        playItem();//不用再次设置path.
    }

    private void playPre() {
        //判断是否是第一项
        if (mPosition == 0)
            return;
        mPosition--;

        playItem();
    }

    /**
     * 更新播放状态
     */
    private void updatePlayState() {
        //1功能性
        if (mVideo_videoview.isPlaying()) {
            //播放
            mVideo_videoview.pause();
        } else {
            //暂停
            mVideo_videoview.start();
        }
        //2界面性
        updatePlayStateBtn(); //更新播放按钮

    }

    /**
     * 更新播放按钮
     */
    private void updatePlayStateBtn() {
        if (mVideo_videoview.isPlaying()) {
            handler.sendEmptyMessageDelayed(UPDATA_PROGRESS, 500);//播放完后，点击再次播放，要添加更新
            mVideo_player_playstate.setImageResource(R.drawable.video_player_playstate_play_selector);
        } else {
            mVideo_player_playstate.setImageResource(R.drawable.video_player_playstate_pause_selector);
        }

    }

    /**
     * 设置监听,确保视频加载到内存了,再去播放
     */
    class mOnPreparedListener implements MediaPlayer.OnPreparedListener {
        @Override
        public void onPrepared(MediaPlayer mediaPlayer) {
            /*开始播放*/
            mVideo_videoview.start();
            /*初始化播放按钮*/
            updatePlayStateBtn();
            //设置视频的总时长
            mVideo_player_duration.setText(StringUtil.parseDuration(mVideo_videoview.getDuration()));
            /*视频进度的更新*/
            startUpdateProgress();
        }

    }

    /* 视频进度（时间）的更新 */
    private void startUpdateProgress() {
        //获取当前进度
        int progress = mVideo_videoview.getCurrentPosition();

        //设置进度值， 进度条值
        mVideo_player_progress_sk.setMax(mVideo_videoview.getDuration());/*设置最大值*/

        updateProgress(progress);

        //定时获取进度，也就是不断的循环
        handler.sendEmptyMessageDelayed(UPDATA_PROGRESS, 500);
    }

    private void updateProgress(int progress) {
        //更新进度数值
        mVideo_player_progress.setText(StringUtil.parseDuration(progress));
        //更新进度条
        mVideo_player_progress_sk.setProgress(progress);
    }

    protected class batteryIconReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            int level = intent.getIntExtra("level", 0);//你怎么知道电池变化后发的 字段是level呢？
            //Bundle extras = intent.getExtras();
            //Toast.makeText(getApplicationContext(),"::"+extras, Toast.LENGTH_SHORT).show();
            updateBatteryImage(level);//根据level来更新图片
        }
    }

    private void updateBatteryImage(int level) {
        if (level < 10) {
            mVideo_player_battery.setImageResource(R.drawable.ic_battery_0);
        } else if (level < 20) {
            mVideo_player_battery.setImageResource(R.drawable.ic_battery_10);
        } else if (level < 40) {
            mVideo_player_battery.setImageResource(R.drawable.ic_battery_20);
        } else if (level < 60) {
            mVideo_player_battery.setImageResource(R.drawable.ic_battery_40);
        } else if (level < 80) {
            mVideo_player_battery.setImageResource(R.drawable.ic_battery_60);
        } else if (level < 100) {
            mVideo_player_battery.setImageResource(R.drawable.ic_battery_80);
        } else if (level == 100) {
            mVideo_player_battery.setImageResource(R.drawable.ic_battery_100);
        }
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(mReceiver);//电量改变的广播。
        handler.removeCallbacksAndMessages(null);//防止handler泄漏。
        super.onDestroy();
    }

    private SeekBar.OnSeekBarChangeListener mOnSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (!fromUser)
                return;//判断是否是用户操作

            if (seekBar.getId() == R.id.video_player_volume_sk) {
                updateVolumeSk(progress);
            } else if (seekBar.getId() == R.id.video_player_progress_sk) {
                //调整视频播放的位置..
                mVideo_videoview.seekTo(progress);
                //改变数值
                updateProgress(progress);
            }
        }

        /**
         *(处理定时隐藏上下栏) 手指操作是,移除 ; 手指离开后,添加
         */
        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            handler.removeMessages(MEG_Show_Hide);
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            handler.sendEmptyMessageDelayed(MEG_Show_Hide, 4000);
        }
    };

    /**
     * 处理触摸事件
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //给手势事件传入event
        mGesture.onTouchEvent(event);

        //变化的音量 = 最大值*百分比
        //滑动的距离 = 开始位置-停止位置
        //滑动百分比 = 滑动的距离/屏幕的高度
        //最终音量 = 开始音量+变化的音量
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mStartVolue = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                downY = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                float offset = downY - event.getY();
                float offsetPercnet = offset / mScreenHeight;

                //手指在左边就处理音量   在右边就处理亮度
                if (event.getX() < mScreenWidth / 2) {
                    float finalVolume = (mStartVolue + offsetPercnet * mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
                    updateVolumeSk((int) finalVolume);
                } else {
                    updateCover(offsetPercnet);/*处理亮度*/
                }
                break;
            default:
                break;
        }
        return super.onTouchEvent(event);
    }

    private void updateCover(float offsetPercnet) { /*处理亮度*/
        //当前亮度 + 变化亮度
        float finalAlpha = mVideo_player_cover.getAlpha() - offsetPercnet;
        if (finalAlpha > 1) {
            finalAlpha = 1;
        } else if (finalAlpha < 0) {
            finalAlpha = 0;
        }
        ViewHelper.setAlpha(mVideo_player_cover, finalAlpha);
    }

    /**
     * 上下栏的显示和隐藏.
     */
    private void hide() {
        ViewPropertyAnimator.animate(mVideo_player_linear_top).translationY(-mTopHeight);
        ViewPropertyAnimator.animate(mVideo_player_linear_bottom).translationY(mBotHeight);
        showing = false;
    }

    private void show() {
        ViewPropertyAnimator.animate(mVideo_player_linear_top).translationY(0);
        ViewPropertyAnimator.animate(mVideo_player_linear_bottom).translationY(0);
        showing = true;
        //正在显示的时候,定时隐藏.
        handler.sendEmptyMessageDelayed(MEG_Show_Hide, 4000);

    }

    private class mySimpleOnGestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            if (showing) {
                //正在显示，就隐藏
                hide();
            } else {
                //隐藏，就显示
                show();
            }
            return super.onSingleTapConfirmed(e);
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            //System.out.println("onSingleTapUp");
            return super.onSingleTapUp(e);
        }


        @Override
        public boolean onDoubleTap(MotionEvent e) {
            switchScreen();
            return super.onDoubleTap(e);
        }

        @Override
        public boolean onDoubleTapEvent(MotionEvent e) {
            return super.onDoubleTapEvent(e);
        }

        @Override
        public void onLongPress(MotionEvent e) {
            updatePlayState();//长按控制播放的状态
            super.onLongPress(e);
        }
    }

    class mOnCompletionListener implements MediaPlayer.OnCompletionListener {
        @Override
        public void onCompletion(MediaPlayer mp) {
            //handler更新可以能会延迟
            //播放结束，设置最大值
            handler.removeMessages(UPDATA_PROGRESS);//移除更新消息
            updateProgress(mVideo_player_progress_sk.getMax());
            //更新播放按钮Icon
            updatePlayStateBtn();
        }
    }

}
