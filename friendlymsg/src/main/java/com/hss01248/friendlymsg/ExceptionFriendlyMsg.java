package com.hss01248.friendlymsg;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.ParseException;
import android.text.TextUtils;
import android.util.Log;


import org.apache.http.conn.ConnectTimeoutException;
import org.json.JSONException;

import java.io.NotSerializableException;
import java.net.ConnectException;
import java.net.UnknownHostException;
import java.util.concurrent.TimeoutException;

import retrofit2.HttpException;

/**
 * by hss
 * data:2020/7/1
 * desc:
 */
public class ExceptionFriendlyMsg {

    static Application application;
    static IFriendlyMsg friendlyMsg;
    public static void init(Application application,IFriendlyMsg friendlyMsg){
        ExceptionFriendlyMsg.application = application;
        ExceptionFriendlyMsg.friendlyMsg = friendlyMsg;
    }
    

    public static ReturnMsg toFriendlyMsg(Throwable e){
        ReturnMsg bean = null;
        if (e instanceof HttpException) {
            HttpException httpException = (HttpException) e;
            int code = httpException.code();
            String message = httpException.message();
            // e.getMessage(): HTTP 405 Method Not Allowed .

            String responseStr = "";
            if (httpException.response() != null && httpException.response().errorBody() != null) {
                try {
                    responseStr = httpException.response().errorBody().string();
                } catch (Exception e2) {

                }
            }
            Log.w("httperror",String.format("httpcode:%d,msg:%s,errorBody:\n%s", code, message, responseStr));
            String friendlyMsg = "";
            if (code >= 500) {
                friendlyMsg = getString(R.string.httputl_server_error);
            } else if (code == 401) {
                friendlyMsg = getString(R.string.httputl_unlogin_error);
            }
            bean = ReturnMsg.newBuilder()
                    .code(code+"")
                    .realMsg(message)
                    .friendlyMsg(friendlyMsg)
                    .responseBody(responseStr)
                    .build();
        } else if (isJsonException(e)) {
            bean = ReturnMsg.newBuilder()
                    .code("JsonParseException")
                    .realMsg(e.getMessage())
                    .friendlyMsg(getString(R.string.json_parse_error))
                    .build();

        } else if (e instanceof ClassCastException) {
            bean = ReturnMsg.newBuilder()
                    .code("ClassCastException")
                    .realMsg(e.getMessage())
                    .friendlyMsg(getString(R.string.some_error))
                    .build();
        } else if (e instanceof ConnectException) {
            bean = ReturnMsg.newBuilder()
                    .code("ConnectException")
                    .realMsg(e.getMessage())
                    .friendlyMsg(getString(R.string.httputl_poor_network))
                    .build();
            // ex.message = "连接失败";

        } else if (e instanceof javax.net.ssl.SSLException) {
            bean = ReturnMsg.newBuilder()
                    .code("SSLException")
                    .realMsg(e.getMessage())
                    .friendlyMsg(getString(R.string.httputil_ssl_error))
                    .build();
            // ex.message = "证书验证失败";

        } else if (e instanceof ConnectTimeoutException
                || e instanceof java.net.SocketTimeoutException
                || e instanceof UnknownHostException) {
            if(!isNetworkAvailable()){
                bean = ReturnMsg.newBuilder()
                        .code("NoNetworkException")
                        .realMsg(e.getMessage())
                        .friendlyMsg(getString(R.string.httputl_no_network))
                        .build();
            }else {
                bean = ReturnMsg.newBuilder()
                        .code("ConnectTimeoutException")
                        .realMsg(e.getMessage())
                        .friendlyMsg(getString(R.string.httputl_timeout))
                        .build();
            }


        }  else if (e instanceof NullPointerException) {

            bean = ReturnMsg.newBuilder()
                    .code("NullPointerException")
                    .realMsg(e.getMessage())
                    .friendlyMsg(getString(R.string.some_error))
                    .build();

        } else if (e instanceof TimeoutException) {
            bean = ReturnMsg.newBuilder()
                    .code("TimeoutException")
                    .realMsg(e.getMessage())
                    .friendlyMsg(getString(R.string.httputl_timeout))
                    .build();
        } else {
            bean = ReturnMsg.newBuilder()
                    .code("UnknownException")
                    .realMsg(e.getMessage())
                    .friendlyMsg(getString(R.string.some_error))
                    .build();
        }
        /*if(e.getCause() != null && !TextUtils.isEmpty(e.getCause().getMessage())){
            bean.realMsg = e.getCause().getMessage();
        }*/
        if(friendlyMsg != null){
            bean.friendlyMsg = friendlyMsg.toMsg(bean.code);
        }

        return bean;

    }

    static boolean isNetworkAvailable() {
        ConnectivityManager conManager = (ConnectivityManager) application.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (conManager != null) {
            NetworkInfo[] netInf = conManager.getAllNetworkInfo();
            for (int i = 0; i < netInf.length; i++) {
                if (netInf[i].getState() == NetworkInfo.State.CONNECTED) {
                    return true;
                }
            }
        }
        return false;
    }

    private static String getString(int id) {
        return application.getResources().getString(id);
    }

    private static boolean isJsonException(Throwable e) {
        if(e ==null){
            return false;
        }
        if(e instanceof JSONException){
            return true;
        }
        if(e instanceof NotSerializableException){
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

}
