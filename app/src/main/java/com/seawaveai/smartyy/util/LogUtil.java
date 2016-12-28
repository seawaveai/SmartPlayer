package com.seawaveai.smartyy.util;

import android.util.Log;

/**
 * 创建者     laoyoutiao
 * 创建时间   2016/11/21 18:21
 * 描述
 * <p/>
 * 更新者     $Author$
 * 更新时间   $Date$
 * 更新描述   ${TODO}
 */
public class LogUtil {

    private static final boolean showLogE = true;

    /** 定义自己的logE */
    public static void logE(String tag, String msg) {
        if (showLogE) {
            Log.e(tag, msg);
        }
    }


}
