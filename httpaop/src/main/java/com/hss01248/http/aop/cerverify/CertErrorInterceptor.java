package com.hss01248.http.aop.cerverify;

import android.util.Log;


import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;

import javax.net.ssl.SSLPeerUnverifiedException;

import okhttp3.Interceptor;
import okhttp3.Response;
@Deprecated
public class CertErrorInterceptor implements Interceptor {
   //static ReentrantLock lock = new ReentrantLock();
   static AtomicBoolean requested = new AtomicBoolean(false);
   static String TAG = "CertEr";

    @Override
    public Response intercept(Chain chain) throws IOException {
        try {
            return chain.proceed(chain.request());
        } catch (Throwable throwable) {
            if (throwable instanceof SSLPeerUnverifiedException
                    && throwable.getMessage().contains("Hostname")
                    && throwable.getMessage().contains("not verified")) {
                //证书校验抛出的
                //Log.w(TAG, throwable.getMessage());
                if(HostNameCerChecker.configRequest == null){
                    throw throwable;
                }
                if(requested.get()){
                    Log.w(TAG, Thread.currentThread().getName()+"已经拉过---了配置,不再进锁-"+chain.request().url());
                    throw throwable;
                }
                synchronized (CertErrorInterceptor.class){
                    if(requested.get()){
                        Log.w(TAG, Thread.currentThread().getName()+"已经拉到了配置,不再请求-"+chain.request().url());
                        throw throwable;
                    }
                    Log.w(TAG, Thread.currentThread().getName()+"准备拉配置-"+chain.request().url());
                    Map<String, List<String>> piners0 = HostNameCerChecker.configRequest.requestConfig();
                    Log.w(TAG, Thread.currentThread().getName()+"piners0-"+piners0);
                    if (piners0 != null) {
                        HostNameCerChecker.piners.putAll(piners0);
                        //lock.unlock();
                        //lock.notifyAll();
                        //无法重新校验
                        // return chain.proceed(chain.request());
                    }
                    Log.w(TAG, Thread.currentThread().getName()+"已经拉到了配置-切换开关");
                    requested.getAndSet(true);
                }
            }
            throw throwable;
        }
    }

    public static void request(){

    }
}
