package com.hss01248.http.response;



import android.text.TextUtils;

import com.hss01248.friendlymsg.ExceptionFriendlyMsg;

import com.hss01248.friendlymsg.ReturnMsg;
import com.hss01248.http.ConfigInfo;
import com.hss01248.http.GlobalConfig;
import com.hss01248.http.HttpUtil;
import com.hss01248.http.R;
import com.hss01248.http.Tool;
import com.hss01248.http.callback.BaseSubscriber;
import com.hss01248.http.config.DataCodeMsgJsonConfig;
import com.hss01248.http.exceptions.DataCodeMsgCodeErrorException;
import com.hss01248.http.exceptions.ExceptionWrapper;
import com.hss01248.http.exceptions.FileDownloadException;
import com.hss01248.http.exceptions.RequestConfigCheckException;
import com.hss01248.http.exceptions.ResponseStrEmptyException;


import org.json.JSONObject;


/**
 * Created by hss on 2018/7/24.
 */

public class ErrorCallbackDispatcher {

    /**
     * DataCodeMsgCodeErrorException
     * <p>
     * 根据throwable的类型进行判断
     *
     * @param callback
     * @param e        rx框架接我们各处代码发出的异常
     */
    public static void dispatchException(BaseSubscriber callback, Throwable e) {


        ConfigInfo info = null;
        boolean isFromCache = false;
        if(e instanceof ExceptionWrapper){
            ExceptionWrapper wrapper = (ExceptionWrapper) e;
            info = wrapper.info;
            e = wrapper.getRealThrowable();
            isFromCache = wrapper.fromCache;
            callback.info = info;
            callback.fromCache = isFromCache;
        }else {
            Tool.logw("e not instanceof ExceptionWrapper!!!!");
            //rxjava本身超时机制抛出的异常无法用onerrorresume捕获,所以无法包裹,所以拿不到info
            //java.util.concurrent.TimeoutException: The source did not signal an event for 200 milliseconds and has been terminated.
        }

        if (GlobalConfig.get().isOpenLog()) {
            e.printStackTrace();
        }

        callback.info = info;
        if(info != null){
           // Tool.logJson(info);
        }else {
            Tool.logw("callback.info is null ");

        }
        Tool.logd("is from cache : "+isFromCache);


        if (e instanceof DataCodeMsgCodeErrorException) {
            DataCodeMsgCodeErrorException e1 = (DataCodeMsgCodeErrorException) e;
            //callback.onCodeError();//todo
            //callback.onCodeError(e1.info.);
            preParseCodeError(e1, callback);
        } else  {
            ReturnMsg bean = ExceptionFriendlyMsg.toFriendlyMsg(e);

            if (e instanceof ResponseStrEmptyException) {
                bean = ReturnMsg.newBuilder()
                        .code("ResponseStrEmptyException")
                        //.realMsg(e.getMessage())
                        .friendlyMsg(Tool.getString(R.string.httputl_empty_error))
                        .build();
            } else if (e instanceof RequestConfigCheckException) {
                bean = ReturnMsg.newBuilder()
                        .code("RequestParamException")
                        .realMsg(e.getMessage())
                        .friendlyMsg(Tool.getString(R.string.json_param_error))
                        .build();
            }else if (e instanceof FileDownloadException) {
                bean = ReturnMsg.newBuilder()
                        .code("FileDownloadException")
                        .realMsg(e.getMessage())
                        .friendlyMsg(Tool.getString(R.string.httputl_filedownload_error))
                        .build();
            }
            if("ResponseStrEmptyException".equals(bean.code)){
                callback.onEmpty();
                return;
            }
            if("401".equals(bean.code)){
                callback.onUnlogin(bean.responseBody);
                return;
            }

            callback.onError(bean.code,bean.friendlyMsg+"",bean.realMsg,bean.responseBody);
        }
    }


    private static void preParseCodeError(DataCodeMsgCodeErrorException e, BaseSubscriber callback) {
        ResponseBean bean = e.responseBean;
        ConfigInfo info = e.info;
        DataCodeMsgJsonConfig config = info.getDataCodeMsgJsonConfig();
        JSONObject json = bean.json;
        String msg = json.optString(config.getKey_msg());
        int code = -1;
        String codeStr = "";
        if (!config.isCodeAsString()) {
            if(json.has(config.getKey_code())){
                code = json.optInt(config.getKey_code());
            }
        } else {
            codeStr = json.optString(config.getKey_code());
        }


        String extra1 = "";
        String extra2 = "";
        String extra3 = "";
        if (!TextUtils.isEmpty(config.getKey_extra1())) {
            extra1 = json.optString(config.getKey_extra1());
        }
        if (!TextUtils.isEmpty(config.getKey_extra2())) {
            extra2 = json.optString(config.getKey_extra2());
        }
        if (!TextUtils.isEmpty(config.getKey_extra3())) {
            extra3 = json.optString(config.getKey_extra3());
        }


        String print = String.format("code:%d,codeStr:%s,msg:%s,data:%s,%s:%s,%s:%s,%s:%s",
                code, codeStr, msg, bean.dataStr, config.getKey_extra1(), extra1,
                config.getKey_extra2(), extra2, config.getKey_extra3(), extra3);
        GlobalConfig.get().getTool().logd(print);
        callback.onCodeError(code, msg, bean.dataStr, codeStr, extra1, extra2, extra3, bean, info);
    }


}
