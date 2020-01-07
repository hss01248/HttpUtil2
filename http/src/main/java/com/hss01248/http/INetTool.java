package com.hss01248.http;

import android.app.Activity;
import android.app.Application;

import java.util.List;

import okhttp3.OkHttpClient;

/**
 * Created by huangshuisheng on 2017/12/25.
 */

public interface INetTool {

     String toJsonStr(Object obj);

     <T> T parseObject(String str, Class<T> clazz);

   /* public static <T> T parse(String str, Class<T> clazz) {
        // return new Gson().fromJson(str,clazz);
        return JSON.parseObject(str, clazz);
    }*/

     <E> List<E> parseArray(String str, Class<E> clazz);


     void logi(String str);

    void logd(String str);

    void logw(String str);

    void logdJson(String json);

    void initialStetho(Application application);

    void addChuckInterceptor(OkHttpClient.Builder builder);

    void addStethoInterceptor(OkHttpClient.Builder builder);

    void addHttpLogInterceptor(OkHttpClient.Builder builder);

    Activity getTopActivity();

    void logObj(Object t);

    void reportError(String code,String msg,String url);
}
