package com.hss01248.http.callback;


import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.hss01248.http.ConfigInfo;
import com.hss01248.http.GlobalConfig;
import com.hss01248.http.R;
import com.hss01248.http.Tool;
import com.hss01248.http.config.LoadingDialogConfig;
import com.hss01248.http.response.ErrorCallbackDispatcher;
import com.hss01248.http.response.ResponseBean;

/**
 * Created by Administrator on 2016/4/15 0015.
 */
//@MainThread
public abstract class MyNetCallback<T>  implements ProgressCallback{

    public LoadingDialogConfig dialogConfig;
    Object tagForCancel;
    public volatile ConfigInfo info;
    public volatile boolean fromCache;
    public long startTime;


    public MyNetCallback() {
        this(false, null);
    }

    public String getUrl(){
        if(info != null){
            return info.getUrl();
        }
        return "";
    }

    /**
     * @param showLoadingDialog
     * @param tagForCancel      activity,或者某些特殊
     */
    public MyNetCallback(boolean showLoadingDialog, @Nullable Object tagForCancel) {
        this.tagForCancel = tagForCancel;
        if (!showLoadingDialog) {
            return;
        }
        if (this.dialogConfig == null) {
            dialogConfig = LoadingDialogConfig.newInstance();
        }

    }

    public MyNetCallback(LoadingDialogConfig dialogConfig, @Nullable Object tagForCancel) {
        this.tagForCancel = tagForCancel;
        this.dialogConfig = dialogConfig;
    }


     void onStart() {
        Tool.logd("-->http is onStart");
        startTime = System.currentTimeMillis();
        boolean hasShow =  Tool.showLoadingDialog(dialogConfig, tagForCancel, null);
        if(!hasShow){
            //Tool.addByTag(tagForCancel, this);
        }
    }



    protected void onResult(T t){
        CallbackDispatcher.dispatch(this,t);
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
        String friendlyMsg = "";
        if(TextUtils.isEmpty(exceptionMsg)){
            friendlyMsg = msgCanShow +"\n\n(" + code + ")";
        }else {
            friendlyMsg = msgCanShow +"\n\n(" + code + "\n" + exceptionMsg+ ")";
        }
        onError(code,friendlyMsg);
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
        Tool.logw("request canceled:");
    }

    public void onCache(boolean hasCache){}


    /**
     * 都是B作为单位
     */
    public void onProgressChange(long transPortedBytes, long totalBytes, ConfigInfo info) {
        //GlobalConfig.get().getTool().logd("transPortedBytes:"+transPortedBytes+"--totalBytes:"+totalBytes);
    }

    /**
     * @param transPortedBytes
     * @param totalBytes
     * @param fileIndex
     * @param filesCount       总的上传文件数量
     */
    public void onFilesUploadProgress(long transPortedBytes, long totalBytes, int fileIndex, int filesCount, ConfigInfo info) {
        GlobalConfig.get().getTool().logd("FilesUploadprogress:" + transPortedBytes + "--totalBytes:" + totalBytes + "--fileIndex:" + fileIndex + "-----filecount:" + filesCount);
        onProgressChange(transPortedBytes, totalBytes, info);
    }


}
