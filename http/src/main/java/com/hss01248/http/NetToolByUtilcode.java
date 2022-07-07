package com.hss01248.http;

import android.app.Activity;
import android.app.Application;

import com.blankj.utilcode.util.ActivityUtils;


/**
 * @Despciption todo
 * @Author hss
 * @Date 07/07/2022 20:24
 * @Version 1.0
 */
public class NetToolByUtilcode implements INetTool{


    @Override
    public Activity getTopActivity() {
        return ActivityUtils.getTopActivity();
    }

    @Override
    public void reportError(String code, String msg, String url) {

    }
}
