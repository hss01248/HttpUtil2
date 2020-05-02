package com.hss01248.http.exceptions;

import com.hss01248.http.ConfigInfo;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Function;

/**
 * Created by hss on 2018/11/26.
 */

public class ExceptionWrapper extends Exception {



    public ConfigInfo info;
    public boolean fromCache;




    public ExceptionWrapper(Throwable cause, ConfigInfo info, boolean fromCache) {
        super( cause);
        this.info = info;
        this.fromCache = fromCache;
    }

    public Throwable getRealThrowable(){
        return getCause();
    }

    /**
     * 非服务器产生的异常，比如本地无无网络请求，Json数据解析错误,HttpException,socket超时等等。
     *
     * 无法捕获rxjava内部产生的异常,比如设置的超时异常:
     * ObservableTimeoutTimed$TimeoutObserver.onTimeout(ObservableTimeoutTimed.java:132)
     *
     * @param <T>
     */
    public static <T> Function<Throwable, ObservableSource<? extends T>> wrapperException(ConfigInfo info,boolean fromCache){
        return new Function<Throwable, ObservableSource<? extends T>>() {
            @Override
            public ObservableSource<? extends T> apply(Throwable throwable) throws Exception {
                if (throwable instanceof ExceptionWrapper) {
                    return Observable.error(throwable);
                } else {
                    return Observable.error(new ExceptionWrapper(throwable, info, false));
                }
            }
        };
    }





}
