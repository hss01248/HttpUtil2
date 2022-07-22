package com.hss01248.serverclock;

import android.os.SystemClock;

/**
 * @Despciption todo
 * @Author hss
 * @Date 21/07/2022 16:21
 * @Version 1.0
 */
public class ClockSynchronizer {


    private volatile static long serverTime;
    private volatile static long serverTimeGMT;

    private volatile static long serverSetTime;

    /**
     * @param serverTime 服务端真实时间-当前时区的时间。 考虑传输延迟，可自行加上延时时间。一般是50-500ms。
     */
    static void setServerTime(long serverTime, long serverTimeGMT) {
        if (serverTime <= 0) {
            return;
        }
        ClockSynchronizer.serverTime = serverTime;
        ClockSynchronizer.serverTimeGMT = serverTimeGMT;
        serverSetTime = SystemClock.elapsedRealtime();
    }

    /**
     * 基于http 响应头的的date UMT格式时间,
     * 获取经服务端校准后的本地真实时间，
     * 替代System.currentTimeMillis(),免受用户调整手机本地时间的影响.
     * 绝对时间精确到秒
     * 计算时间间隔则精度和SystemClock.elapsedRealtime()以及System.currentTimeMillis()一致
     */
    public static long currentTimeMillis() {
        if (serverSetTime == 0) {
            return System.currentTimeMillis();
        }
        return serverTime + (SystemClock.elapsedRealtime() - serverSetTime);
    }

    /**
     * https://zhuanlan.zhihu.com/p/135951778
     *GMT是前世界标准时，UTC是现世界标准时。
     * UTC 比 GMT更精准，以原子时计时，适应现代社会的精确计时。
     * 但在不需要精确到秒的情况下，二者可以视为等同。
     * 每年格林尼治天文台会发调时信息，基于UTC。
     * @return
     */
    public static long currentTimeMillisGMT() {
        if (serverTimeGMT == 0) {
            //todo 减去时区
            long diff = ClockSynchronizerInterceptor.calDiff();
            return System.currentTimeMillis() - diff;
        }
        return serverTimeGMT + (SystemClock.elapsedRealtime() - serverSetTime);
    }


}
