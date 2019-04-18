package com.hss01248.http.exceptions;

import com.hss01248.http.ConfigInfo;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Function;

/**
 * Created by hss on 2018/11/26.
 */

public class ExceptionWrapper extends Exception {


    public Throwable e;
    public ConfigInfo info;
    public boolean fromCache;


    public ExceptionWrapper(Throwable e, ConfigInfo info,boolean fromCache){
        this.e = e;
        this.info = info;
        this.fromCache = fromCache;
    }

    public Throwable getRealThrowable(){
        return e;
    }


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
