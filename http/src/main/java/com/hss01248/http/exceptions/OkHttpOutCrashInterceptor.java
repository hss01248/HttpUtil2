package com.hss01248.http.exceptions;

import com.hss01248.http.Tool;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Response;

/**
 * Created by huangshuisheng on 2018/10/23.
 *
 * https://github.com/square/okhttp/issues/3477
 *
 * 防止okhttp调用系统api时引起的crash,常见于dns解析失败,java底层抛出crash
 */

public class OkHttpOutCrashInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        try {
            return chain.proceed(chain.request());
        } catch (Throwable e) {
            if (e instanceof IOException) {
                throw e;
            } else {
                Tool.logw(e.getMessage());
                throw new IOException(e);
            }
        }
    }
}
