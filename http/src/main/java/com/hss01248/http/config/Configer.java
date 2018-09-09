package com.hss01248.http.config;

import com.hss01248.http.ConfigInfo;
import com.hss01248.http.GlobalConfig;
import com.hss01248.http.cache.CacheMode;
import com.hss01248.http.cookie.DiskCookieJar;
import com.hss01248.http.cookie.MemoryCookieJar;
import com.hss01248.http.interceptors.NoCacheInterceptor;
import com.hss01248.http.utils.SslUtil;

import java.util.concurrent.TimeUnit;

import okhttp3.CacheControl;
import okhttp3.CookieJar;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;


/**
 * Created by hss on 2018/7/26.
 */

public class Configer {
    public static void setGloablConfig(OkHttpClient.Builder builder) {
        setCookie(builder, GlobalConfig.get().getCookieMode());
        setHttps(builder,GlobalConfig.get().isIgnoreCertificateVerify());

        setLog(builder,GlobalConfig.get().isOpenLog());

        builder.connectTimeout(GlobalConfig.get().getConnectTimeout(), TimeUnit.MILLISECONDS);
        builder.retryOnConnectionFailure(GlobalConfig.get().isRetryOnConnectionFailure());
        builder.readTimeout(GlobalConfig.get().getReadTimeout(),TimeUnit.MILLISECONDS);
        builder.writeTimeout(GlobalConfig.get().getWriteTimeout(),TimeUnit.MILLISECONDS);
        //todo 拦截器的区分

        for(Interceptor interceptor : GlobalConfig.get().commonInterceptors){
            builder.addInterceptor(interceptor);
        }


        if(GlobalConfig.get().getCacheMode() != CacheMode.DEFAULT){
            builder.addInterceptor(new NoCacheInterceptor());
        }
    }

    static void setCustomConfig(OkHttpClient.Builder builder,ConfigInfo info){
        setCookie(builder,info.getCookieMode());
        setHttps(builder,info.isIgnoreCer());

        setLog(builder,GlobalConfig.get().isOpenLog());

        builder.connectTimeout(info.getConnectTimeout(), TimeUnit.MILLISECONDS);
        builder.retryOnConnectionFailure(info.isRetryOnConnectionFailure());
        builder.readTimeout(GlobalConfig.get().getReadTimeout(),TimeUnit.MILLISECONDS);
        builder.writeTimeout(GlobalConfig.get().getWriteTimeout(),TimeUnit.MILLISECONDS);
        //todo 拦截器的区分

        for(Interceptor interceptor : GlobalConfig.get().commonInterceptors){
            builder.addInterceptor(interceptor);
        }


        if(info.getCacheMode() != CacheMode.DEFAULT){
            builder.addInterceptor(new NoCacheInterceptor());
        }
    }


    //SocketException: sendto failed: EPIPE (Broken pipe) EPIPE happens often as a result of missing permissions)
    private static void setLog(OkHttpClient.Builder builder, boolean openLog) {
        if(openLog){
            //addLog(builder);
            GlobalConfig.get()
                    .getTool()
                    .addHttpLogInterceptor(builder);
            GlobalConfig.get().getTool().addChuckInterceptor(builder);
            GlobalConfig.get().getTool().addStethoInterceptor(builder);
        }
    }

    private static void addLog(OkHttpClient.Builder builder) {
        //builder.addInterceptor(new HttpLoggingInterceptor().setLevel(Level.BODY));
      /*  builder.addInterceptor(new LoggingInterceptor.Builder()
                .loggable(GlobalConfig.get().isOpenLog())
                .setLevel(Level.BASIC)
                .log(Platform.INFO)
                .request("Request")
                .response("Response")
                .addHeader("version", BuildConfig.VERSION_NAME)
                .addQueryParam("query", "0")
                .enableAndroidStudio_v3_LogsHack(true) *//* enable fix for logCat logging issues with pretty format *//*
//              .logger(new Logger() {
//                  @Override
//                  public void log(int level, String tag, String msg) {
//                      Log.w(tag, msg);
//                  }
//              })
//              .executor(Executors.newSingleThreadExecutor())
                .build());*/
    }

    private static void setHttps(OkHttpClient.Builder builder, boolean ignoreCertificateVerify) {
        if(ignoreCertificateVerify){
            SslUtil.setAllCerPass(builder);
        }else {
            SslUtil.setHttps(builder);
        }
    }

    private static <E> void setCacheMode(Request.Builder builder, ConfigInfo<E> info) {
        CacheControl.Builder cacheBuilder = new CacheControl.Builder();
        CacheControl cacheControl;

        switch (info.getCacheMode()){
            case CacheMode.NO_CACHE:{
                //CacheControl
                cacheBuilder = new CacheControl.Builder();
                cacheBuilder.noCache();
            }
            break;
            case CacheMode.DEFAULT:{


            }
            break;
            case CacheMode.REQUEST_FAILED_READ_CACHE:{
                cacheControl = CacheControl.FORCE_NETWORK;


            }
            break;
            case CacheMode.IF_NONE_CACHE_REQUEST:{
                cacheControl = CacheControl.FORCE_NETWORK;

            }
            break;
            case CacheMode.FIRST_CACHE_THEN_REQUEST:{
                cacheControl = CacheControl.FORCE_NETWORK;

            }
            break;
            default:
                break;
        }

        cacheControl = cacheBuilder.build();
        builder.cacheControl(cacheControl);
    }

    private static void setCookie(OkHttpClient.Builder builder, int cookieMode) {
        CookieJar cookieJar ;
        if(cookieMode == GlobalConfig.COOKIE_MEMORY){
            cookieJar = new MemoryCookieJar();
        }else if (cookieMode == GlobalConfig.COOKIE_DISK){
            cookieJar = new DiskCookieJar();
        }else if(cookieMode == GlobalConfig.COOKIE_NONE){
            cookieJar = CookieJar.NO_COOKIES;
        }else {
            cookieJar = new DiskCookieJar();
        }
        builder.cookieJar(cookieJar);
    }
}
