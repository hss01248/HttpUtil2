package com.hss01248.http.interceptors;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.util.Log;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.nio.charset.Charset;
import java.util.WeakHashMap;

import okhttp3.Call;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;

public class SameRequestInterceptor implements Interceptor {

    static WeakHashMap<String, ResonseForClone> responseWeakHashMap = new WeakHashMap<>();
    static WeakHashMap<String, WeakReference<Call>> calls = new WeakHashMap<>();

    static Charset UTF_8 = Charset.forName("UTF-8");
    static IConfig config;
    static boolean enableFilter;
    static boolean debug;
    static Handler handler;

    /**
     * @param enableFilter 全局开关
     * @param config       配置信息
     */
    public static void config(boolean debug, boolean enableFilter, IConfig config) {
        SameRequestInterceptor.enableFilter = enableFilter;
        SameRequestInterceptor.config = config;
        SameRequestInterceptor.debug = debug;
    }


    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        if (!enableFilter) {
            return chain.proceed(request);
        }

        if (config == null) {
            return chain.proceed(request);
        }
        if (!config.shouldInterceptSameRequest(request)) {
            return chain.proceed(request);
        }

        String key = generateKey(request);

        return check(chain, request, key);
    }

    private Response check(Chain chain, Request request, String key) throws IOException {
        try {
            //从缓存的call和response中判断要不要等待
            boolean needwait = needwait(key,request.url().toString());

            if (!needwait) {
                if (responseWeakHashMap.containsKey(key)) {
                    return responseWeakHashMap.get(key).getClonedResonse();
                } else {
                    //直接执行请求:
                    //将response缓存起来
                    return realExceute(chain, request, key);
                }
            } else {
                Thread.sleep(2000);
                return check(chain, request, key);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            if (responseWeakHashMap.containsKey(key)) {
                return responseWeakHashMap.get(key).getClonedResonse();
            } else {
                //直接执行请求:
                //将response缓存起来
                return realExceute(chain, request, key);
            }
        }
    }

    @NonNull
    private Response realExceute(Chain chain, Request request, String key) throws IOException {
        calls.put(key, new WeakReference<>(chain.call()));
        try {
            Response response = chain.proceed(request);

            if (canCacheResponse(response)) {
                ResponseBody responseBody = response.body();
                BufferedSource source = responseBody.source();
                source.request(responseBody.contentLength() > 0 ? responseBody.contentLength() : Integer.MAX_VALUE);
                //吓人?
                Buffer buffer = source.buffer();
                Charset charset = UTF_8;
                MediaType contentType = responseBody.contentType();
                if (contentType != null) {
                    charset = contentType.charset(UTF_8);
                }
                String bodyString = buffer.clone().readString(charset);

                ResponseBody cloneBody = ResponseBody.create(response.body().contentType(), bodyString);

                Response responseClone = response.newBuilder()
                        .body(cloneBody)
                        .header("cachedResonse", "yes")
                        .build();
                responseWeakHashMap.put(key, new ResonseForClone(bodyString, responseClone));
                calls.remove(key);
                String url = request.url().toString();
                getMainHandler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //1min后移除缓存的response:
                        responseWeakHashMap.remove(key);
                        logw(config.responseCacheTimeInMills() + "ms 时间到了,清除缓存的response",url);
                    }
                }, config.responseCacheTimeInMills());

            }else {
                calls.remove(key);
            }

            return response;
        }catch (IOException e){
            calls.remove(key);
            //其他非io类型的异常,calls里用软引用自动移除
            throw e;
        }
    }

    private boolean canCacheResponse(Response response) {
        if(response == null){
            return false;
        }
        if(!response.isSuccessful()){
            return false;
        }
        String url = response.request().url().toString();
        if(response.body() == null){
            logw("response 的body为空,不缓存response",url);
            return false;
        }
        if(response.body().contentType() == null){
            logw("response 的contenttype为空,不缓存response",url);
            return false;
        }
        String type = response.body().contentType().type();
        if("text".equals(type) || "application".equals(type)){
            return true;
        }
        logw("response 的contenttype不是text或application类型,而是:"+type+",不缓存response",url);
        return false;
    }

    private static Handler getMainHandler() {
        if (handler == null) {
            handler = new Handler(Looper.getMainLooper());
        }
        return handler;
    }

    private boolean needwait(String key,String url) {
        if (responseWeakHashMap.containsKey(key)) {
            logw("有缓存的response,直接去读缓存,并组装新的response",url);
            return false;
        }
        if (calls.containsKey(key)) {
            WeakReference<Call> callWeakReference = calls.get(key);
            if (callWeakReference == null) {
                logw("不需要等待,直接发请求 call WeakReference not exist:",url);
                return false;
            }
            Call call = callWeakReference.get();
            if (call == null || call.isCanceled()) {
                logw("不需要等待,直接发请求 call not exist or is canceld:" + call,url);
                return false;
            }
            logw("请求可能正在等待或正在执行-needwait call is running:" + call,url);
            //请求可能正在等待或正在执行
            return true;
        }
        logw("任何地方都没有,不需要等,直接执行请求",url);
        //任何地方都没有,不需要等,直接执行请求
        return false;
    }

    private static void logw(String str,String url) {
        if (debug) {
            Log.w("SameRequest", str+"  "+ url);
        }
    }

    /**
     * @param request
     * @return
     */
    private String generateKey(Request request) {

        return config.generateCacheKey(request);
    }

    class ResonseForClone {
        String body;
        Response response;

        public ResonseForClone(String body, Response response) {
            this.body = body;
            this.response = response;
        }

        public Response getClonedResonse() {
            ResponseBody cloneBody = ResponseBody.create(response.body().contentType(), body);
            Response responseClone = response.newBuilder()
                    .body(cloneBody)
                    .build();
            return responseClone;
        }
    }

    public interface IConfig {
        boolean shouldInterceptSameRequest(Request request);

        String generateCacheKey(Request request);

        long responseCacheTimeInMills();
    }
}
