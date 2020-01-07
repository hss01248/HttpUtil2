package com.hss01248.http.interceptors;

import com.hss01248.http.GlobalConfig;
import com.hss01248.http.log.HttpErrorReporter;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * time:2019/8/13
 * author:hss
 * desription:
 * * <p>
 *  * https://github.com/square/okhttp/issues/3477
 *  * <p>
 *  * <p>
 *  * 添加到okhttp的interceptor层，不是networkinterceor层
 *  * <p>
 *  * 功能有：
 *  * 1.catch住异常，全部转化成IOException，不让框架崩溃
 *  * 2 网络层错误上报
 */
public class OkHttpExceptionInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        String url = "";
        try {
            Request request = chain.request();
            url = request.url().toString();
            if (GlobalConfig.get().isDebug()) {
                request = request.newBuilder().addHeader("OkHttpExceptionInterceptor", "yes").build();
            }
            Response response = chain.proceed(request);
            if (response.isSuccessful()) {
                return response;
            } else {
                int code = response.code();
                String message = response.message();
                //上报http错误
                HttpErrorReporter.reportHttpError(url, code, message,true);
                return response;
            }
        } catch (Throwable e) {
            //上报网络层的错误
            HttpErrorReporter.reportNetworkException(url, e,true);
            if (e instanceof IOException) {
                throw e;
            } else {
                throw e;
            }
        }
    }
}
