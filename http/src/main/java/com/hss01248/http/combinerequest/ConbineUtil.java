package com.hss01248.http.combinerequest;

import android.widget.Button;

import com.hss01248.http.HttpUtil;
import com.hss01248.http.callback.BaseSubscriber;
import com.hss01248.http.response.ResponseBean;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Observer;


/**
 * time:2020/5/2
 * author:hss
 * desription:
 */
public class ConbineUtil {

    public void request(){
        List<ConbineConfig> configs = new ArrayList<>();
       Observable<ResponseBean<ConbineUtil>> observable =  HttpUtil.request("",ConbineUtil.class)
                .addParam("key","value")
                .asObservable();
        ConbineConfig config  = new ConbineConfig();
        config.failToResult = true;
        config.observable = observable;


        Observable<ResponseBean<Button>> observable2 =  HttpUtil.request("", Button.class)
                .addParam("key","value")
                .asObservable();
        ConbineConfig config2  = new ConbineConfig();
        config2.failToResult = false;
        config2.observable = observable2;

        Observable.merge(observable,observable2,observable)
                .onErrorResumeNext(new Observable<ResponseBean<?>>() {
                    @Override
                    protected void subscribeActual(Observer<? super ResponseBean<?>> observer) {

                    }
                })
                .subscribe(new BaseSubscriber<ResponseBean<?>>() {
                    @Override
                    public void onSuccess(ResponseBean<?> response) {

                    }

                    @Override
                    public void onError(String msgCanShow) {

                    }
                });








    }
}
