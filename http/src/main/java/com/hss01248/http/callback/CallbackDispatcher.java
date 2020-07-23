package com.hss01248.http.callback;

import com.hss01248.http.Tool;
import com.hss01248.http.response.ErrorCallbackDispatcher;
import com.hss01248.http.response.ResponseBean;

public class CallbackDispatcher {

    public static <T> void dispatch(MyNetCallback<T> callback, T t){
        if(!(t instanceof ResponseBean)){
            Tool.logw("t not instance of ResponseBean:"+t);
            return;
        }
        onEnd(callback,t);
        ResponseBean bean = (ResponseBean) t;
        if(bean.success){
            onSuccess(callback,t);
        }else {
            onError(callback,bean.errorInfo);
        }
    }

    private static <T> void onEnd(MyNetCallback<T> callback, T t) {
        Tool.logd("-->http end,cost time : " + (System.currentTimeMillis() - callback.startTime) + " ms," +callback.getUrl());
    }

    private static <T> void onError(MyNetCallback<T> callback, Throwable e) {
        try {
            Tool.logd("-->http is onError: "+callback.getUrl() );
            Tool.dismissLoadingDialog(callback.dialogConfig, callback.tagForCancel);
            ErrorCallbackDispatcher.dispatchException(callback, e);
        }catch (Throwable e2){
            e2.printStackTrace();
        }
    }



    private static <T> void onSuccess(MyNetCallback<T> callback, T t) {
        Tool.logd("-->http is onsuccess: "+callback.getUrl());
        callback.onSuccess(t);
    }


}
