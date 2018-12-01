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

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.hss01248.http.ConfigInfo;
import com.hss01248.http.GlobalConfig;
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
public abstract class BaseSubscriber<T> extends DisposableObserver<T> implements ProgressCallback{




    public LoadingDialogConfig dialogConfig;
    Object tagForCancel;
    public volatile ConfigInfo info;
    public volatile boolean fromCache;
    public long startTime;


    public BaseSubscriber(){
        this(false,null);
    }
    /**
     *
     * @param showLoadingDialog
     * @param tagForCancel activity,或者某些特殊
     */
    public BaseSubscriber(boolean showLoadingDialog,@Nullable Object tagForCancel) {
        this.tagForCancel = tagForCancel;
        if(!showLoadingDialog){
            return;
        }
        if(this.dialogConfig ==null){
            dialogConfig = LoadingDialogConfig.newInstance();
        }

    }

    public BaseSubscriber(LoadingDialogConfig dialogConfig,@Nullable Object tagForCancel) {
        this.tagForCancel = tagForCancel;
        this.dialogConfig = dialogConfig;
    }

    @Override
    protected void onStart() {
        Tool.logd("-->http is onStart");
        startTime = System.currentTimeMillis();
        Tool.showLoadingDialog(dialogConfig,tagForCancel,this);
        Tool.addByTag(tagForCancel,this);
    }

    //原生的方法

    @Override
    public final void onNext(@NonNull T t) {
        Tool.logd("-->http is onNext");
        Tool.logJson(t);
        onSuccess(t);
    }

    @Override
    public final void onError(Throwable e) {
        //Tool.dismissLoadingDialog(dialogConfig,tagForCancel,this);
        ErrorCallbackDispatcher.dispatchException(this,e);
    }

    @Override
    public void onComplete() {
        Tool.logd("-->http is onComplete");
        Tool.logd("cost time : "+ (System.currentTimeMillis() - startTime)+" ms");
        Tool.dismissLoadingDialog(dialogConfig,tagForCancel,this);
    }







    /**
     * 真正类型是ResponseBean,内部已有各种数据封装,核心是ResponseBean.bean
     * @param response
     */
    public abstract void onSuccess(T response);


    /**
     * Callback method that an error has been occurred with the
     * provided error code and optional user-readable message.
     */
    public abstract void onError(String msgCanShow);





    /**
     * dns解析异常,socket timout异常等等
     */
    public void onNoNetwork(Throwable e){
        onError("no network connection:"+e.getMessage());
    }

    public void onPoorNetwork(){
        onError("network connection is poor");
    }

    /**
     * 总时长超时了
     */
    public void onTimeout(Throwable e){
        onError("connect time out,please check your network:"+e.getMessage());
    }


    /**
     * 401错误
     */
    public  void onHttp401(String responseBodyStr){
        onError("unlogin");
    }

    /**
     * 50x错误
     */
    public void onServerError(int code,String message,String responseBodyStr){
        onError("onServerError:code:"+code+" message:"+message);
    }

    public void onHttpError(int code,String message,String responseBodyStr){
        //onError("OtherHttpError:code-"+code+" message:"+message);
        //HTTP 405 Method Not Allowed
        if(code >=500){
            onServerError(code,message,responseBodyStr);
        }else if(code == 401){
            onHttp401(responseBodyStr);
        }else {
            onError("HttpError:code:"+code+"\nmessage:"+message+"\nresponseStr:"+responseBodyStr);
        }
    }



    public void onJsonParseError(Throwable e){
        onError(e.getMessage());
    }


    public void onEmpty(){
        onError("data is empty");
    }




    public void onCodeError(int code, String msg, String dataStr, String codeStr,
                            String extra1, String extra2, String extra3,
                            ResponseBean responseBean, ConfigInfo info) {
        onError(msg);
    }


    public void onError(String code, String serverMsg, String exceptionMsg) {
        onError(serverMsg);
    }

    /**
     * 取消的请求走空,不要再回调到onError
     */
    public void onCancel() {
        //onError("请求已取消");
    }


    public void onClassCastException(Throwable e) {
        onError(e.getMessage());
    }

    public void onSSLError(Throwable e) {
        onError(e.getMessage());
    }

    public void onNullPointerException(Throwable e) {
        onError(e.getMessage());
    }

    public void onUnknowError(Throwable e) {
        onError(e.getMessage());
    }


    /**
     * 都是B作为单位
     */
    @Override
    public void onProgressChange(long transPortedBytes, long totalBytes, ConfigInfo info) {
        //GlobalConfig.get().getTool().logd("transPortedBytes:"+transPortedBytes+"--totalBytes:"+totalBytes);
    }

    /**
     *
     * @param transPortedBytes
     * @param totalBytes
     * @param fileIndex
     * @param filesCount 总的上传文件数量
     */
    @Override
    public void onFilesUploadProgress(long transPortedBytes, long totalBytes,int fileIndex,int filesCount, ConfigInfo info) {
        GlobalConfig.get().getTool().logd("FilesUploadprogress:"+transPortedBytes+"--totalBytes:"+totalBytes+"--fileIndex:"+fileIndex+"-----filecount:"+filesCount);
        onProgressChange(transPortedBytes,totalBytes,info);
    }

}
