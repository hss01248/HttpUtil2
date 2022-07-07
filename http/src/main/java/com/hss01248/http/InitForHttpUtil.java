package com.hss01248.http;


import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.startup.Initializer;

import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * @Despciption todo
 * @Author hss
 * @Date 30/06/2022 10:04
 * @Version 1.0
 */
public class InitForHttpUtil implements  Initializer<String> {




    @Override
    public String create(Context context) {
        Log.d("init","InitForHttp.init start");
       HttpUtil.init((Application) context, AppUtils.isAppDebug(),"https://www.baidu.com/",null);
        return "InitForHttp";
    }

    @Override
    public List<Class<? extends Initializer<?>>> dependencies() {

        return new ArrayList<>();
    }
}
