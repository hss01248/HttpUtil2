package com.hss01248.http.old;

import java.util.Map;

import io.reactivex.functions.Consumer;

/**
 * Created by huangshuisheng on 2017/9/28.
 */

public interface HttpCallback<T> extends Consumer<T> {




    void onNoNetwork();

    void onTimeout();

    void onCancel();

    void onUnlogin();




    void onEmpty();

    void dispatchException(Throwable throwable);




    void onProgressChange(long transPortedBytes, long totalBytes);
    void onFilesUploadProgress(long transPortedBytes, long totalBytes, int fileIndex, int filesCount);



    void onSuccess(T obj, String wholeResponseBodyStr, boolean isFromCache);
    void onSuccess(T response, String responseStr, boolean isFromCache, String dataStr, Map<String, Object> datas);


    void onError(String msgCanShow);
    void onError(String code, String serverMsg, String exceptionMsg);
    void onCodeError(String msgCanShow, String hiddenMsg, int code);

}
