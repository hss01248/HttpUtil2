package com.hss01248.http.netstate;

/**
 * time:2019/8/13
 * author:hss
 * desription:
 */
public interface NetStateChangeObserver {
    void onNetDisconnected();

    void onNetConnected(NetworkType networkType);

}
