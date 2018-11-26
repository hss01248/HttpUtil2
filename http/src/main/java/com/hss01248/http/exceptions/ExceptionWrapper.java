package com.hss01248.http.exceptions;

import com.hss01248.http.ConfigInfo;

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





}
