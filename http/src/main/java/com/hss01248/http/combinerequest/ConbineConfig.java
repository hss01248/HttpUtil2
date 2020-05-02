package com.hss01248.http.combinerequest;

import com.hss01248.http.response.ResponseBean;

import io.reactivex.Observable;


/**
 * time:2020/5/2
 * author:hss
 * desription:
 */
public class ConbineConfig<T> {
    /*public String url;
    public Map<String,String> params;
    public Class clazz;*/
    public Observable<ResponseBean<T>> observable;
    public boolean failToResult;
}
