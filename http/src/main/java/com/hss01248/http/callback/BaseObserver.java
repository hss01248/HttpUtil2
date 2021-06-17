package com.hss01248.http.callback;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

public class BaseObserver<T> implements Observer<T> {

    public BaseObserver(MyNetCallback<T> callback, LiveData liveData, LifecycleOwner lifecycleOwner) {
        this.callback = callback;
        this.liveData = liveData;
        this.lifecycleOwner = lifecycleOwner;
        callback.onStart();
    }

    protected MyNetCallback<T> callback;
    protected LiveData liveData;
    protected LifecycleOwner lifecycleOwner;

    @Override
    public void onChanged(T t) {
        callback.onResult(t);
        //防止onresume重复发送. 也可以使用singleliveevent
        liveData.removeObservers(lifecycleOwner);
    }
}
