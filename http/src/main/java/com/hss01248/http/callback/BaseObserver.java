package com.hss01248.http.callback;

import androidx.lifecycle.Observer;

public class BaseObserver<T> implements Observer<T> {

    public BaseObserver(MyNetCallback<T> callback) {
        this.callback = callback;
        callback.onStart();
    }

    protected MyNetCallback<T> callback;

    @Override
    public void onChanged(T t) {
        callback.onResult(t);
    }
}
