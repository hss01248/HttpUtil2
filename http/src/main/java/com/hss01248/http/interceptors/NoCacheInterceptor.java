package com.hss01248.http.interceptors;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Administrator on 2016/9/22 0022.
 * 不让okhttp处理,完全由ACache+内置逻辑来处理缓存
 */
public class NoCacheInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {

        Request request = chain.request();
        request = request.newBuilder().header("Cache-Control", "no-cache").header("Pragma", "no-cache").build();
        Response originalResponse = chain.proceed(request);
        originalResponse = originalResponse.newBuilder().header("Cache-Control", "no-cache").header("Pragma", "no-cache").build();
        return originalResponse;

        //获取retrofit @headers里面的参数，参数可以自己定义，在本例我自己定义的是cache，跟@headers里面对应就可以了
       /* String cache = chain.request().header("cache");
        okhttp3.Response originalResponse = chain.proceed(chain.request());
        String cacheControl = originalResponse.header("Cache-Control");
        //如果cacheControl为空，就让他TIMEOUT_CONNECT秒的缓存，本例是5秒，方便观察。注意这里的cacheControl是服务器返回的
        if (cacheControl == null) {
            //如果cache没值，缓存时间为TIMEOUT_CONNECT，有的话就为cache的值
            if (cache == null || "".equals(cache)) {
                cache = TIMEOUT_CONNECT + "";
            }
            originalResponse = originalResponse.newBuilder()
                    .header("Cache-Control", "public, max-age=" + cache)
                    .build();
            return originalResponse;
        } else {
            return originalResponse;
        }*/

        //获取retrofit @headers里面的参数，参数可以自己定义，在本例我自己定义的是cache，跟@headers里面对应就可以了

    }


}



