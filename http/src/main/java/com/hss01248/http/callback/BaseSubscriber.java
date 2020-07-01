/*
 * Copyright (C) 2017 zhouyou(478319399@qq.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hss01248.http.callback;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.hss01248.http.ConfigInfo;
import com.hss01248.http.GlobalConfig;
import com.hss01248.http.R;
import com.hss01248.http.Tool;
import com.hss01248.http.config.LoadingDialogConfig;
import com.hss01248.http.response.ErrorCallbackDispatcher;
import com.hss01248.http.response.ResponseBean;

import io.reactivex.observers.DisposableObserver;

/**
 * <p>描述：订阅的基类</p>
 * 1.可以防止内存泄露。<br>
 * 2.在onStart()没有网络时直接onCompleted();<br>
 * 3.统一处理了异常<br>
 * 作者： zhouyou<br>
 * 日期： 2016/12/20 10:35<br>
 * 版本： v2.0<br>
 */
public abstract class BaseSubscriber<T> extends DisposableObserver<T> implements ProgressCallback {


    public LoadingDialogConfig dialogConfig;
    Object tagForCancel;
    public volatile ConfigInfo info;
    public volatile boolean fromCache;
    public long startTime;


    public BaseSubscriber() {
        this(false, null);
    }

    /**
     * @param showLoadingDialog
     * @param tagForCancel      activity,或者某些特殊
     */
    public BaseSubscriber(boolean showLoadingDialog, @Nullable Object tagForCancel) {
        this.tagForCancel = tagForCancel;
        if (!showLoadingDialog) {
            return;
        }
        if (this.dialogConfig == null) {
            dialogConfig = LoadingDialogConfig.newInstance();
        }

    }

    public BaseSubscriber(LoadingDialogConfig dialogConfig, @Nullable Object tagForCancel) {
        this.tagForCancel = tagForCancel;
        this.dialogConfig = dialogConfig;
    }

    @Override
    protected void onStart() {
        Tool.logd("-->http is onStart");
        startTime = System.currentTimeMillis();
       boolean hasShow =  Tool.showLoadingDialog(dialogConfig, tagForCancel, this);
       if(!hasShow){
           Tool.addByTag(tagForCancel, this);
       }
    }



    //原生的方法

    @Override
    public final void onNext(@NonNull T t) {
        Tool.logd("-->http is onNext/onsuccess");
        Tool.logObj(t);
        try {
            onSuccess(t);
            //UndeliverableException: The exception could not be delivered to the consumer
            // because it has already canceled/disposed the flow or the exception
        }catch (Throwable e){
            ErrorCallbackDispatcher.dispatchException(this, e);
            //ErrorCallbackDispatcher.dispatchException(this, ExceptionWrapper.wrapperException());
        }

    }

    @Override
    public final void onError(Throwable e) {
        Tool.logd("-->http is onError,cost time : " + (System.currentTimeMillis() - startTime) + " ms");
        Tool.dismissLoadingDialog(dialogConfig, tagForCancel, this);
        try {
            ErrorCallbackDispatcher.dispatchException(this, e);
        }catch (Throwable e2){
            e2.printStackTrace();
        }
    }

    @Override
    public void onComplete() {
        Tool.logd("-->http is onComplete,cost time : " + (System.currentTimeMillis() - startTime) + " ms");
        Tool.dismissLoadingDialog(dialogConfig, tagForCancel, this);
    }


    /**
     * 真正类型是ResponseBean,内部已有各种数据封装,核心是ResponseBean.data
     *
     * @param response
     */
    public abstract void onSuccess(T response);


    /**
     * Callback method that an error has been occurred with the
     * provided error code and optional user-readable message.
     */
    public abstract void onError(String msgCanShow);

    public  void onError(String code,String msgCanShow){
        onError(msgCanShow);
    }

    public void onError(String code, String msgCanShow, String exceptionMsg, String responseBodyStr) {
        onError(code,msgCanShow);
    }

    public void onCodeError(int code, String msg, String dataStr, String codeStr,
                            String extra1, String extra2, String extra3,
                            ResponseBean responseBean, ConfigInfo info) {
        onError(code+"",msg,"",dataStr);
    }




    /**
     * 401错误
     * 类型: 没有登录, 登录过期,被踢
     */
    public void onUnlogin(String responseBodyStr) {
        onError(Tool.getString(R.string.http_unlogin));
    }


    public void onEmpty() {
        onError(Tool.getString(R.string.httputl_empty_error));
    }




    /**
     * 取消的请求走空,不要再回调到onError
     */
    public void onCancel() {
        //onError("请求已取消");
    }

    public void onCache(boolean hasCache){}


    /**
     * 都是B作为单位
     */
    @Override
    public void onProgressChange(long transPortedBytes, long totalBytes, ConfigInfo info) {
        //GlobalConfig.get().getTool().logd("transPortedBytes:"+transPortedBytes+"--totalBytes:"+totalBytes);
    }

    /**
     * @param transPortedBytes
     * @param totalBytes
     * @param fileIndex
     * @param filesCount       总的上传文件数量
     */
    @Override
    public void onFilesUploadProgress(long transPortedBytes, long totalBytes, int fileIndex, int filesCount, ConfigInfo info) {
        GlobalConfig.get().getTool().logd("FilesUploadprogress:" + transPortedBytes + "--totalBytes:" + totalBytes + "--fileIndex:" + fileIndex + "-----filecount:" + filesCount);
        onProgressChange(transPortedBytes, totalBytes, info);
    }

}
