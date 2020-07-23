package com.hss01248.http;

import android.app.Application;
import android.content.Context;

import androidx.lifecycle.LiveData;

import com.akaita.java.rxjava2debug.RxJava2Debug;
import com.hss01248.beanvalidator.BeanValidator;
import com.hss01248.friendlymsg.ExceptionFriendlyMsg;
import com.hss01248.http.config.FileDownlodConfig;
import com.hss01248.http.netstate.NetStateChangeReceiver;
import com.zchu.rxcache.RxCache;
import com.zchu.rxcache.diskconverter.GsonDiskConverter;
import com.zchu.rxcache.utils.LogUtils;

import java.io.File;
import java.util.HashMap;
import java.util.List;


/**
 * Created by huangshuisheng on 2017/11/15.
 */

public class HttpUtil {


    public static Context context;

    public static RxCache getRxCache() {
        if(rxCache == null){
            rxCache = new RxCache.Builder()
                    .appVersion(1)//当版本号改变,缓存路径下存储的所有数据都会被清除掉
                    .diskDir(new File(HttpUtil.context.getCacheDir().getPath() + File.separator + "okhttp-cache"))
                    .diskConverter(new GsonDiskConverter())//支持Serializable、Json(GsonDiskConverter)
                    .memoryMax(6*1024*1024)
                    .diskMax(50*1024*1024)
                    .build();
        }
        return rxCache;
    }

    private static RxCache rxCache;

    public static GlobalConfig init(Application context0, boolean openlog, String baseUrl, INetTool tool) {
        context = context0;
        LogUtils.DEBUG = openlog;
        BeanValidator.init(context0);
        NetStateChangeReceiver.registerReceiver(context0);
        ExceptionFriendlyMsg.init(context0,null);
        RxJava2Debug.enableRxJava2AssemblyTracking(new String[]{"com.hss01248.http"});
        //初始化uploader

        // initDownloader(context0);
        return GlobalConfig.get()
                .setContext(context0)
                .openLog(openlog)
                .setBaseUrl(baseUrl)
                .setTool(tool);
    }



    public static <T> ConfigInfo<T> request(String url, Class<T> clazz) {
        ConfigInfo<T> configInfo = new ConfigInfo<>();
        configInfo.clazz = clazz;
        configInfo.setUrl(url);
        return configInfo;
    }

    public static ConfigInfo<String> requestString(String url) {
        ConfigInfo<String> configInfo = new ConfigInfo<>();
        configInfo.setUrl(url);
         configInfo.clazz = String.class;
        configInfo.responseAsString = true;
        return configInfo;
    }

    public static <T> ConfigInfo<List<T>> requestAsJsonArray(String url, Class<T> clazz) {
        ConfigInfo<List<T>> configInfo = new ConfigInfo<List<T>>();
        configInfo.setResponseAsJsonArray(true);
        configInfo.setUrl(url);
        configInfo.clazz = clazz;
        //configInfo.clazz = type.getClass().getGenericSuperclass()
        // Type typeOfListOfFoo = new TypeToken<List<T>>(){}.getRawType();
        //new TypeToken<List<T>>(){}.getType().getClass();

        return configInfo;
    }

    public static ConfigInfo<FileDownlodConfig> download(String url) {
        ConfigInfo<FileDownlodConfig> configInfo = new ConfigInfo<>();
        configInfo.download();
        configInfo.setUrl(url);
        return configInfo;
    }

    public static void cancelByTag(Object obj){
        Tool.cancelByTag(obj,null,true);
    }








    /*public static <T> ConfigWrapper<T> getWrapper(ConfigInfo<T> configInfo){
        ConfigWrapper<T> wrapper = new ConfigWrapper<>();
        wrapper.configInfo = configInfo;
        return wrapper;
    }

    public static <T> ConfigInfo< List<T>> requestAsJsonArrayWrapper(ConfigInfo<T> configInfo){
        ConfigWrapper<List<T>> wrapper = new ConfigWrapper<>();

        //configInfo.clazz = type.getClass().getGenericSuperclass()
        // Type typeOfListOfFoo = new TypeToken<List<T>>(){}.getRawType();
        //new TypeToken<List<T>>(){}.getType().getClass();

        return configInfo;
    }*/


    private static HashMap<Object, List<retrofit2.Call>> callMap = new HashMap<>();

   /* *//**
     * risk服务器的get请求,不支持下载,下方app同理
     * @param url   url尾. 主机部分自动拼接
     * @param clazz data对应的javabean
     * @param <T>  data对应的javabean
     * @return
     *//*
    public static <T> RequestConfig.Builder<T> getFromRisk(String url, Class<T> clazz){
       return RequestConfig.newBuilder()
            .url(url)
            .clazz(clazz)
            .isRiskHost(true)
            .method(HttpMethod.GET);
    }


    *//**
     * riks服务器的post请求,不支持上传.下方app同理
     * @param url
     * @param clazz data对应的javabean
     * @param <T> data对应的javabean
     * @return
     *//*
    public static <T> RequestConfig.Builder<T> postToRisk(String url, Class<T> clazz){
        return RequestConfig.newBuilder()
            .url(url)
            .clazz(clazz)
            .isRiskHost(true)
            .method(HttpMethod.POST);
    }

    public static <T> RequestConfig.Builder<T> getFromApp(String url, Class<T> clazz){
        return RequestConfig.newBuilder()
            .url(url)
            .clazz(clazz)
            .isRiskHost(false)
            .method(HttpMethod.GET);
    }

    public static <T> RequestConfig.Builder<T> postToApp(String url, Class<T> clazz){
        return RequestConfig.newBuilder()
            .url(url)
            .clazz(clazz)
            .isRiskHost(false)
            .method(HttpMethod.POST);
    }


    //TODO  调试中,请勿使用

    public static <T> RequestConfig.Builder<T> download(String url){
        return RequestConfig.newBuilder()
            .url(url)
            .setType(RequestConfig.TYPE_DOWNLOAD)
            .method(HttpMethod.GET);
    }

    *//**
     * 通用性方法
     * @param url
     * @param key
     * @param filepath
     * @param <T>
     * @return
     *//*
    public static <T> RequestConfig.Builder<T> uploadByPost(String url, String key, String filepath){
        return RequestConfig.newBuilder()
            .url(url)
            .addFile(key,filepath)
            .setType(RequestConfig.TYPE_UPLOAD)
            .method(HttpMethod.POST);
    }

    public static <T> RequestConfig.Builder<T> uploadByPut(String url, String key, String filepath){
        return RequestConfig.newBuilder()
            .url(url)
            .addFile(key,filepath)
            .setType(RequestConfig.TYPE_UPLOAD)
            .isTreatEmptyDataAsSuccess(true)
            .method(HttpMethod.PUT);
    }*/


}
