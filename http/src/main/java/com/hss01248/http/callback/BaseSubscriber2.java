package com.hss01248.http.callback;

import com.hss01248.http.response.ResponseBean;

import io.reactivex.annotations.NonNull;
import io.reactivex.observers.DisposableObserver;

public  class BaseSubscriber2<T> extends DisposableObserver<T>  {

    public BaseSubscriber2(MyNetCallback<T> callback) {
        this.callback = callback;
    }

    protected MyNetCallback<T> callback;

    @Override
    protected void onStart() {
        super.onStart();
        callback.onStart();
    }

    @Override
    public void onNext(@NonNull T t) {
        onResult(t);
    }

    protected void onResult(T t) {
        callback.onResult(t);
    }

    @Override
    public void onError(@NonNull Throwable e) {
        onResult((T)ResponseBean.error(e));
    }

    @Override
    public void onComplete() {

    }
}
