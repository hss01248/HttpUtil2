package com.hss01248.http.cache;

/**
 * Created by Administrator on 2017/3/9 0009.
 */

public class CacheMode {

    //缓存策略,分类参考:https://github.com/jeasonlzy/okhttp-OkGo
    public static final int NO_CACHE = 1;//不使用缓存,该模式下,cacheKey,cacheMaxAge 参数均无效
    public static final int DEFAULT = 2;//完全按照HTTP协议的默认缓存规则，例如有304响应头时缓存。
    public static final int REQUEST_FAILED_READ_CACHE = 3;//先请求网络，如果请求网络失败，则读取缓存，如果读取缓存失败，本次请求失败。成功或失败的回调只有一次
    public static final int IF_NONE_CACHE_REQUEST = 4;//优先使用缓存,如果缓存不存在才请求网络,成功或失败的回调只有一次
    public static final int FIRST_CACHE_THEN_REQUEST = 5;//先使用缓存，不管是否存在，仍然请求网络,可能导致两次成功的回调或一次失败的回调.
    public static final int ONLY_CACHE = 6;//只读取缓存,不请求网络
    /*@IntDef({NO_CACHE, DEFAULT,REQUEST_FAILED_READ_CACHE,IF_NONE_CACHE_REQUEST,FIRST_CACHE_THEN_REQUEST})
     @Retention(RetentionPolicy.SOURCE)
    public @interface Mode {
    }*/


}
