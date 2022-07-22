package com.hss01248.serverclock;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.hss01248.okhttpinterceptors.BaseOkhttpInterceptor;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import okhttp3.Response;

/**
 * @Despciption todo
 * @Author hss
 * @Date 21/07/2022 16:21
 * @Version 1.0
 */
public class ClockSynchronizerInterceptor extends BaseOkhttpInterceptor {

    static volatile long lastTime;

    /**
     * https://www.jianshu.com/p/e0dd536dd3e4
     * Date：Date头域表示消息发送的时间，缓存在评估响应的新鲜度时要用到，时间的描述格式由RFC822定义。例如，Date: Thu, 11 Jul 2015 15:33:24 GMT。
     * Sun, 06 Nov 1994 08:49:37 GMT  ; RFC 822, updated by RFC 1123
     * 所有的HTTP日期/时间戳必须是以格林威治标准时间展示（GMT）
     *
     * @param dataFormater
     * @return
     */
    public ClockSynchronizerInterceptor setDataFormater(SimpleDateFormat dataFormater) {
        this.dataFormater = dataFormater;
        return this;
    }

    /**
     * 默认只更新一次. 避免跳动.
     *
     * @param adjustOneTime
     */
    public void setAdjustOneTime(boolean adjustOneTime) {
        this.adjustOneTime = adjustOneTime;
    }

    boolean adjustOneTime = true;

    SimpleDateFormat dataFormater = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss 'GMT'", Locale.US);

    @Override
    protected Response interceptReally(Chain chain) throws IOException {
        Response response = chain.proceed(chain.request());
        adjustByResponseHeader(response);
        return response;
    }

    boolean hasSuccessOnce = false;

    private void adjustByResponseHeader(Response response) {
        if (lastTime != 0 && (System.currentTimeMillis() - lastTime < 3000)) {
            //3s校准一次
            return;
        }
        if (hasSuccessOnce && adjustOneTime) {
            return;
        }

        String dateStr = response.header("Date");
        if (TextUtils.isEmpty(dateStr)) {
            return;
        }

        //Date: Thu, 21 Jul 2022 08:25:34 GMT
        //Server: Jetty(9.4.z-SNAPSHOT)

        //Server: nginx/1.20.1
        //Date: Thu, 21 Jul 2022 08:26:33 GMT

        //date: Thu, 21 Jul 2022 08:30:52 GMT

        //date
        //Thu, 21 Jul 2022 08:27:48 GMT
        //date
        //Thu, 21 Jul 2022 08:27:57 GMT
        lastTime = System.currentTimeMillis();
        boolean success = parse(dateStr, dataFormater, isDebug);
        if (success) {
            hasSuccessOnce = true;
        }

    }

    static long calDiff() {
        String timeZone = getCurrentTimeZone();
        //GMT+08:00
        long diff = 0;
        String timeStr = timeZone.substring(4);
        Log.v("time", timeZone + "   " + timeStr);
        String[] strings = timeStr.split(":");
        diff = diff + Long.parseLong(strings[0]) * 60 * 60 * 1000L;
        diff = diff + Long.parseLong(strings[1]) * 60 * 1000L;
        if (timeZone.startsWith("GMT+")) {
            return diff;
        } else if (timeZone.startsWith("GMT-")) {
            return -diff;
        }
        return 0;
    }

    public static boolean parse(String dateStr, SimpleDateFormat dataFormater, boolean isDebug) {
        try {
            Date date = dataFormater.parse(dateStr);
            if (date != null) {
                long time = date.getTime();
                long timeUMT = time;
                long diff = calDiff();

                time = time + diff;

                ClockSynchronizer.setServerTime(time, timeUMT);

                if (isDebug) {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Log.v("time", "根据服务端时间校准:" + sdf.format(new Date(time)) + ", 手机系统设置的时间:" + sdf.format(new Date(System.currentTimeMillis())));
                }
                return true;
            } else {
                if (isDebug) {
                    Log.w("time", "时间解析失败:" + dateStr);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 获取当前时区
     * String s = "TimeZone   "+tz.getDisplayName(false, TimeZone.SHORT)+" Timezon id :: " +tz.getID();
     * TimeZone   GMT+08:00 Timezon id :: Asia/Shanghai
     *
     * @return
     */
    static String getCurrentTimeZone() {
        TimeZone tz = TimeZone.getDefault();
        String strTz = tz.getDisplayName(false, TimeZone.SHORT);
        return strTz;

    }


    /**
     * 获取当前系统语言格式
     *
     * @param mContext
     * @return
     */
    public static String getCurrentLanguage(Context mContext) {
        Locale locale = mContext.getResources().getConfiguration().locale;
        String language = locale.getLanguage();
        String country = locale.getCountry();
        String lc = language + "_" + country;
        return lc;
    }

}
