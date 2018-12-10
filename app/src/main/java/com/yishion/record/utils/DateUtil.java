package com.yishion.record.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class DateUtil {

    public static String format(long time) {
        Date d = new Date(time);
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd yyyy - HH:mm a", Locale.getDefault());
        return sdf.format(d);
    }

    public static String format(long time, String format) {
        Date d = new Date(time);
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.getDefault());
        return sdf.format(d);
    }

    private static final StringBuilder sb = new StringBuilder();

    //格式化过了多长时间
    public static String formatElapsedTime(long time) {

        long s = TimeUnit.MILLISECONDS.toSeconds(time);// 秒
        long sss = time - s * 1000;// 毫秒****

        long m = TimeUnit.SECONDS.toMinutes(s);// 分
        long ss = s - m * 60;// 秒****

        long h = TimeUnit.MINUTES.toHours(m);// 时
        long mm = m - h * 60;// 分钟****

        long d = TimeUnit.HOURS.toDays(h);// 天
        long hh = h - d * 24;// 小时****

        if (time >= 24 * 60 * 60 * 1000) {
            return String.format(Locale.getDefault(), "%1$d天 %2$02d:%3$02d:%4$02d", d, hh, mm, ss);
        }
        else if (time >= 60 * 60 * 1000) {//超过一小时
            return String.format(Locale.getDefault(), "%1$02d:%2$02d:%3$02d", hh, mm, ss);
        }
        else {//不到60分钟
            return String.format(Locale.getDefault(), "%1$02d:%2$02d", mm, ss);
        }

    }

}
