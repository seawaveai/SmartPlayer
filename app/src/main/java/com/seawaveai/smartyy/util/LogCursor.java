package com.seawaveai.smartyy.util;

import android.database.Cursor;

/**
 * 创建者     laoyoutiao
 * 创建时间   2016/11/22 13:32
 * 描述	      ${TODO}
 * <p/>
 * 更新者     $Author$
 * 更新时间   $Date$
 * 更新描述   ${TODO}
 */
public class LogCursor {

    public static void cursorLog(Cursor cursor){

        //判断cursor是否为空
        if (cursor==null||cursor.getCount()==0) return;
        //循环打印
        while (cursor.moveToNext()){
            for (int i = 0; i < cursor.getColumnCount(); i++) {
                LogUtil.logE("cursor:","key:"+ cursor.getColumnName(i)+",value:"+cursor.getString(i));
            }
        }
    }


}
