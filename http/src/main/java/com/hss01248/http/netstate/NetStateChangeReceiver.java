package com.hss01248.http.netstate;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

/**
 * time:2019/8/13
 * author:hss
 * desription:
 *
 *
 https://github.com/GHdeng/NetMonitor

 @Override public void onTerminate() { super.onTerminate();
 // 取消BroadcastReceiver注册 NetStateChangeReceiver.unregisterReceiver(this);}

 作者：杨晓是大V
 链接：https://www.jianshu.com/p/9cc17b0a3295

 */
public class NetStateChangeReceiver extends BroadcastReceiver {
    private List<NetStateChangeObserver> mObservers = new ArrayList<>();

    public NetStateChangeReceiver() {
    }

    /*** 注册网络监听 */
    public static void registerReceiver(@NonNull Context context) {
        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        context.registerReceiver(InstanceHolder.INSTANCE, intentFilter);
    }

    /*** 取消网络监听 */
    public static void unregisterReceiver(@NonNull Context context) {
        context.unregisterReceiver(InstanceHolder.INSTANCE);
    }

    /*** 注册网络变化Observer*/
    public static void registerObserver(NetStateChangeObserver observer) {
        if (observer == null) return;
        if (!InstanceHolder.INSTANCE.mObservers.contains(observer)) {
            InstanceHolder.INSTANCE.mObservers.add(observer);
        }
    }

    /*** 取消网络变化Observer的注册 */
    public static void unregisterObserver(NetStateChangeObserver observer) {
        if (observer == null) return;
        if (InstanceHolder.INSTANCE.mObservers == null) return;
        InstanceHolder.INSTANCE.mObservers.remove(observer);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {
           // NetworkType networkType = NetworkUtils.getNetworkType(context);
           // notifyObservers(networkType);
        }
    }

    /*** 通知所有的Observer网络状态变化 */
    private void notifyObservers(NetworkType networkType) {
        if (networkType == NetworkType.NETWORK_NO) {
            for (NetStateChangeObserver observer : mObservers) {
                observer.onNetDisconnected();
            }
        } else {
            for (NetStateChangeObserver observer : mObservers) {
                observer.onNetConnected(networkType);
            }
        }
    }

    private static class InstanceHolder {
        private static final NetStateChangeReceiver INSTANCE = new NetStateChangeReceiver();
    }


}
