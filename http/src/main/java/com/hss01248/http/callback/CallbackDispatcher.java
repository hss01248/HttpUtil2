package com.hss01248.http.callback;

import com.blankj.utilcode.util.LogUtils;
import com.hss01248.http.GlobalConfig;
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
            //todo 下载时的success判断有问题
            try {
                onSuccess(callback,t);
            }catch (Throwable throwable){
                onError(callback,throwable);
            }
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
            if(GlobalConfig.get().getErrorHandler() != null){
                try {
                    GlobalConfig.get().getErrorHandler().accept(e2);
                } catch (Exception exception) {
                    exception.printStackTrace();
                }

            }else {
                if(!GlobalConfig.get().isDebug()){
                    LogUtils.w(e2);
                }
            }
            //测试环境,都崩溃,提醒一下
            if(GlobalConfig.get().isDebug()){
                throw e2;
            }
        }
    }



    private static <T> void onSuccess(MyNetCallback<T> callback, T t) {
        Tool.logd("-->http is onsuccess: "+callback.getUrl());
        Tool.dismissLoadingDialog(callback.dialogConfig,null);
        callback.onSuccess(t);
    }


}
