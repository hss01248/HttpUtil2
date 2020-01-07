package com.hss01248.http.response;


import android.net.ParseException;
import android.text.TextUtils;

import com.hss01248.http.ConfigInfo;
import com.hss01248.http.GlobalConfig;
import com.hss01248.http.HttpUtil;
import com.hss01248.http.Tool;
import com.hss01248.http.callback.BaseSubscriber;
import com.hss01248.http.config.DataCodeMsgJsonConfig;
import com.hss01248.http.exceptions.DataCodeMsgCodeErrorException;
import com.hss01248.http.exceptions.ExceptionWrapper;
import com.hss01248.http.exceptions.FileDownloadException;
import com.hss01248.http.exceptions.RequestConfigCheckException;
import com.hss01248.http.exceptions.ResponseStrEmptyException;

import org.apache.http.conn.ConnectTimeoutException;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.NotSerializableException;
import java.net.ConnectException;
import java.net.UnknownHostException;
import java.util.concurrent.TimeoutException;

import retrofit2.HttpException;

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


        if (e instanceof ResponseStrEmptyException) {
            callback.onEmpty();
        } else if (e instanceof DataCodeMsgCodeErrorException) {
            DataCodeMsgCodeErrorException e1 = (DataCodeMsgCodeErrorException) e;
            //callback.onCodeError();//todo
            //callback.onCodeError(e1.info.);
            preParseCodeError(e1, callback);
        } else if (e instanceof RequestConfigCheckException) {
            callback.onError(e.getMessage());
        } else if (e instanceof HttpException) {
            HttpException httpException = (HttpException) e;
            int code = httpException.code();
            String message = httpException.message();
            // e.getMessage(): HTTP 405 Method Not Allowed .

            String responseStr = "";
            if (httpException.response() != null && httpException.response().errorBody() != null) {
                try {
                    responseStr = httpException.response().errorBody().string();
                } catch (Exception e2) {
                    if (GlobalConfig.get().isOpenLog()) {
                        e2.printStackTrace();
                    }
                }
            }
            Tool.logw(String.format("httpcode:%d,msg:%s,errorBody:\n%s", code, message, responseStr));
            callback.onHttpError(code, message, responseStr);
        } else if (isJsonException(e)) {
            callback.onJsonParseError(e);
        } else if (e instanceof ClassCastException) {
            callback.onClassCastException(e);
        } else if (e instanceof ConnectException) {
            callback.onPoorNetwork(e);
            // ex.message = "连接失败";

        } else if (e instanceof javax.net.ssl.SSLHandshakeException) {
            callback.onSSLError(e);
            // ex.message = "证书验证失败";

        } else if (e instanceof ConnectTimeoutException) {
            callback.onPoorNetwork(e);
            //  ex.message = "连接超时";

        } else if (e instanceof java.net.SocketTimeoutException) {
            callback.onPoorNetwork(e);
            // ex.message = "连接超时";

        } else if (e instanceof UnknownHostException) {
            if(Tool.isNetworkAvailable(HttpUtil.context)){
                callback.onNoNetwork(e);
            }else {
                callback.onPoorNetwork(e);
            }

            // ex.message = "无法解析该域名";

        } else if (e instanceof NullPointerException) {

            callback.onNullPointerException(e);
            // ex.message = "NullPointerException";

        } else if (e instanceof FileDownloadException) {
            FileDownloadException exception = (FileDownloadException) e;
            callback.onError(exception.getMessage());
        } else if (e instanceof TimeoutException) {
            callback.onTimeout(e);
        } else {
            callback.onUnknowError(e);
        }
    }

    private static boolean isJsonException(Throwable e) {
        if(e ==null){
            return false;
        }
        if(e instanceof JSONException){
            return true;
        }
        if(e instanceof  NotSerializableException){
            return true;
        }
        if(e instanceof ParseException){
            return true;
        }
        if("com.alibaba.fastjson.JSONException".equalsIgnoreCase(e.getClass().getName())){
            return true;
        }
        if("com.google.gson.JsonParseException".equalsIgnoreCase(e.getClass().getName())){
            return true;
        }
        if("com.google.gson.JsonIOException".equalsIgnoreCase(e.getClass().getName())){
            return true;
        }
        return false;
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
