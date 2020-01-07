package com.hss01248.ebusticket;

import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.xdandroid.hellodaemon.AbsWorkService;

/**
 * Created by hss on 2018/9/12.
 */

public class EbusService extends AbsWorkService {

    private boolean hasStarted;
    @Override
    public Boolean shouldStopService(Intent intent, int flags, int startId) {
        return false;
    }

    @Override
    public void startWork(Intent intent, int flags, int startId) {
        Log.i("dd","startWork:"+intent);
        if(hasStarted){
            return;
        }
        hasStarted = true;
        TicketUtil.request619();
    }

    @Override
    public void stopWork(Intent intent, int flags, int startId) {

    }

    @Override
    public Boolean isWorkRunning(Intent intent, int flags, int startId) {
        return null;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent, Void alwaysNull) {
        return null;
    }

    @Override
    public void onServiceKilled(Intent rootIntent) {
        Log.e("dd","onServiceKilled:"+rootIntent);

    }
}
