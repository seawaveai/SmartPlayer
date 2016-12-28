package com.seawaveai.smartyy.bean;

import android.database.Cursor;
import android.provider.MediaStore;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 创建者     laoyoutiao
 * 创建时间   2016/11/23 17:05
 * 描述	      ${TODO}
 * <p/>
 * 更新者     $Author$
 * 更新时间   $Date$
 * 更新描述   ${TODO}
 */
public class VideoItem implements Serializable {
    private String path;
    private String name;
    private int duration;
    private int size;

    //出入Cursor,返回解析后的item数据
    public static VideoItem getVieoItem(Cursor cursor){
        VideoItem videoItem = new VideoItem();
        //判断Cursor是否为空
        if (cursor != null && cursor.getCount() != 0) {
            //解析Cursor
            videoItem.setPath(cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA)));
            videoItem.setName(cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.TITLE)));
            videoItem.setDuration(cursor.getInt(cursor.getColumnIndex(MediaStore.Video.Media.DURATION)));
            videoItem.setSize(cursor.getInt(cursor.getColumnIndex(MediaStore.Video.Media.SIZE)));
        }
        //返回item
        return videoItem;
    }

    //获取所有数据
    public static ArrayList<VideoItem> getVideoItems(Cursor cursor) {
        ArrayList<VideoItem> videoItems = new ArrayList<VideoItem>();

        cursor.moveToPosition(-1);

        while (cursor.moveToNext()) {
            VideoItem item = getVieoItem(cursor);
            videoItems.add(item);
        }
        return videoItems;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

}
