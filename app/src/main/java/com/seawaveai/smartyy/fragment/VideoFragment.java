package com.seawaveai.smartyy.fragment;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.seawaveai.smartyy.R;
import com.seawaveai.smartyy.activity.VideoPlayActivity;
import com.seawaveai.smartyy.adapter.VideoListAdapter;
import com.seawaveai.smartyy.bean.VideoItem;

import java.util.ArrayList;
import java.util.Random;

/**
 * 创建者     laoyoutiao
 * 创建时间   2016/11/21 21:04
 * 描述	      ${TODO}
 * <p/>
 * 更新者     $Author$
 * 更新时间   $Date$
 * 更新描述   ${TODO}
 */

// java.lang.IllegalArgumentException: column '_id' does not exist

public class VideoFragment extends BaseFragment {

    private ListView mVieo_lv;
    private VideoListAdapter mAdapter;
    private int mItoken;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_video;
    }

    @Override
    protected void initView() {
        mVieo_lv = (ListView) findViewById(R.id.video_listview);
    }

    @Override
    protected void initData() {
        //拿到contentResolver
        ContentResolver resolver = getContext().getContentResolver();

      /*  Cursor cursor = resolver.query(MediaStore.Video.Media.INTERNAL_CONTENT_URI,new String[]{
                MediaStore.Video.Media.DATA,
                MediaStore.Video.Media.TITLE,
                MediaStore.Video.Media.DURATION,
                MediaStore.Video.Media.SIZE
        },null,null,null);

        //MediaStore.Audio.Media.INTERNAL_CONTENT_URI;
        //LogCursor.cursorLog(cursor);
      */
        Random random = new Random();
        mItoken = random.nextInt(2); //就是0 1

        AsyncQueryHandler mQueryHandler = new AsyncQueryHandler(resolver) {
            @Override
            protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
                //LogCursor.cursorLog(cursor);
                //mAdapter.notifyDataSetChanged();//这是不可以的，没有设置cursor,也就是数据。
                //Cursor中有数据之后调用swapCursor，用来填充数据并刷新适配器

                // if (token== mItoken)
                if (token == 1)
                    mAdapter.swapCursor(cursor);/*cursor的api*/
            }
        };

        //Media : Audio Video Images
        mQueryHandler.startQuery(1, null, MediaStore.Video.Media.EXTERNAL_CONTENT_URI, new String[]{
                MediaStore.Video.Media._ID,
                MediaStore.Video.Media.DATA,
                MediaStore.Video.Media.TITLE,
                MediaStore.Video.Media.DURATION,
                MediaStore.Video.Media.SIZE
        }, null, null, null);

        mQueryHandler.startQuery(0, null, MediaStore.Video.Media.INTERNAL_CONTENT_URI, new String[]{
                MediaStore.Video.Media._ID,
                MediaStore.Video.Media.DATA,
                MediaStore.Video.Media.TITLE,
                MediaStore.Video.Media.DURATION,
                MediaStore.Video.Media.SIZE
        }, null, null, null);

    }

    @Override
    protected void initEvent() {
        mAdapter = new VideoListAdapter(getContext(), null);/*为什么可以传null?*/
        mVieo_lv.setAdapter(mAdapter);

        mVieo_lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //获取到数据.. 点击跳转去播放视频
                ArrayList<VideoItem> videoItems = VideoItem.getVideoItems((Cursor) adapterView.getItemAtPosition(i));
                int position = i;
                Intent intent = new Intent(getContext(), VideoPlayActivity.class);
                intent.putExtra("VideoItems", videoItems);
                intent.putExtra("Position", position);
                startActivity(intent);
            }
        });

    }

    @Override
    protected void processBtn(View view) {

    }


}
