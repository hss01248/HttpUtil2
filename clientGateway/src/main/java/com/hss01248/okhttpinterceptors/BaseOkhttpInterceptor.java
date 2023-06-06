package com.hss01248.okhttpinterceptors;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import okhttp3.Interceptor;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.Buffer;

/**
 * shouldIntercept方法优先级最高,然后才是白名单,黑名单
 * 白名单,黑名单
 * <p>
 * 优先白名单,只有在白名单里的,才拦截
 * 有白名单,但不在白名单里,就不拦截
 * <p>
 * 有黑名单,在黑名单里的,就不拦截
 * 不在黑名单里,就拦截
 * <p>
 * 都不加 == 所有请求都不拦截
 */
public abstract class BaseOkhttpInterceptor implements Interceptor {

    protected Set<String> whiteListUrlPattern;
    protected Set<String> blackListUrlPattern;

    public BaseOkhttpInterceptor setDebug(boolean debug) {
        isDebug = debug;
        return this;
    }

    protected boolean isDebug;
    protected boolean interceptorAll;
    protected boolean ignoreAll;

    public BaseOkhttpInterceptor setConfig(IConfig config) {
        this.config = config;
        return this;
    }

    protected IConfig config;

    /**
     * 忽略所有,相当于此不走此拦截器逻辑
     * @param ignoreAll
     * @return
     */
    public BaseOkhttpInterceptor ignoreAll(boolean ignoreAll){
        this.ignoreAll = ignoreAll;
        return this;
    }

    /**
     * 强制拦截所有,所有都走拦截器逻辑
     * 优先级低于ignoreAll
     * @param interceptorAll
     * @return
     */
    public BaseOkhttpInterceptor interceptorAll(boolean interceptorAll){
        this.interceptorAll = interceptorAll;
        return this;
    }

    public BaseOkhttpInterceptor addWhiteList(String pattern) {
        if (whiteListUrlPattern == null) {
            whiteListUrlPattern = new HashSet<>();
        }
        whiteListUrlPattern.add(pattern);
        return this;
    }

    public BaseOkhttpInterceptor addBlackList(String pattern) {
        if (blackListUrlPattern == null) {
            blackListUrlPattern = new HashSet<>();
        }
        blackListUrlPattern.add(pattern);
        return this;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        if(config !=null){
            if(config.ignoreAll()){
                return chain.proceed(chain.request());
            }
            if(config.interceptorAll()){
                return interceptReally(chain);
            }
        }else {
            if(ignoreAll){
                return chain.proceed(chain.request());
            }
            if(interceptorAll){
                return interceptReally(chain);
            }
        }


        if(shouldInterceptBeforeUrlWhiteBlackList(chain)){
            return interceptReally(chain);
        }
        String url = chain.request().url().toString();

        if (whiteListUrlPattern != null && !whiteListUrlPattern.isEmpty()) {
            //优先白名单,只有在白名单里的,才拦截
            for (String pattern : whiteListUrlPattern) {
                if (url.contains(pattern)) {
                    if (blackListUrlPattern != null && !blackListUrlPattern.isEmpty()) {
                        //有黑名单,在黑名单里的,就不拦截
                        for (String pattern2 : blackListUrlPattern) {
                            if (url.contains(pattern2)) {
                                return chain.proceed(chain.request());
                            }
                        }
                    }
                    //在白名单,且不在黑名单里,才真正拦截
                    return interceptReally(chain);
                }
            }
            //有白名单,但不在白名单里,就不拦截
            return chain.proceed(chain.request());
        }else if (blackListUrlPattern != null && !blackListUrlPattern.isEmpty()) {
            //有黑名单,在黑名单里的,就不拦截
            for (String pattern : blackListUrlPattern) {
                if (url.contains(pattern)) {
                    return chain.proceed(chain.request());
                }
            }
            //不在黑名单里,就拦截
            return interceptReally(chain);
        }
        return chain.proceed(chain.request());

    }

    public static String getRequetBodyAsString(RequestBody body){
        try {
            if(body == null){
                return null;
            }
            final Buffer buffer = new Buffer();
            body.writeTo(buffer);

            final long size = buffer.size();

            final byte[] bytes = new byte[(int) size];
            buffer.readFully(bytes);
            return new String(bytes);
        }catch (Throwable throwable){
            throwable.printStackTrace();
            return "";
        }

    }

    protected boolean shouldInterceptBeforeUrlWhiteBlackList(Chain chain) {
        return false;
    }

    protected abstract Response interceptReally(Chain chain) throws IOException;

    public interface IConfig{
        default   boolean ignoreAll(){
            return false;
        }
        default   boolean interceptorAll(){
            return false;
        }


    }
}