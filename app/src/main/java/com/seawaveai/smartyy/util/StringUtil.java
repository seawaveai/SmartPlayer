package com.seawaveai.smartyy.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 创建者     laoyoutiao
 * 创建时间   2016/11/23 17:44
 * 描述	      ${TODO}
 * <p/>
 * 更新者     $Author$
 * 更新时间   $Date$
 * 更新描述   ${TODO}
 */
public class StringUtil {

    private static final int HOUR = 60 * 60 * 1000;
    private static final int MIN = 60 * 1000;
    private static final int SEC = 1000;

    /**
     * 格式化从数据库回来的时间
     */
    public static String parseDuration(int duration) {
        int hour = duration / HOUR;
        int min = duration % HOUR / MIN;
        int sec = duration % HOUR % MIN / SEC;
        if (hour == 0) {
            return String.format("%02d:%02d", min, sec);
        } else {
            return String.format("%02d:%02d:%02d", hour, min, sec);
        }
    }

    /**
     * 格式化系统时间
     */
    public static String parseSystemTime() {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
        return format.format(new Date());//格式化当前时间
    }

}
