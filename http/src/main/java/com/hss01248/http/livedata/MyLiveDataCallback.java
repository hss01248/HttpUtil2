package com.hss01248.http.livedata;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.hss01248.http.callback.MyNetCallback;
import com.hss01248.http.response.ResponseBean;

public class MyLiveDataCallback<T> extends MyNetCallback<ResponseBean<T>> {


    MutableLiveData<T> mutableLiveData = new MutableLiveData<>();


    @Override
    public void onSuccess(ResponseBean<T> response) {
        mutableLiveData.setValue(response.data);
    }

    @Override
    public void onError(String msgCanShow) {
       // mutableLiveData.setValue(msgCanShow);
    }
}
