package com.hss01248.http.callback;

import com.hss01248.http.response.ResponseBean;

public class CallbackDispatcher {

    public static <T> void dispatch(MyNetCallback<T> callback, T t){
        //dispatch2(callback,t);
        if(!(t instanceof ResponseBean)){
            return;
        }
        ResponseBean bean = (ResponseBean) t;
        if(bean.success){
            callback.onNext(t);
        }else {
            callback.onError(bean.errorInfo);
        }
    }

    public static <T> void dispatch2(MyNetCallback<ResponseBean<T>> callback, ResponseBean<T> t){

    }
}
