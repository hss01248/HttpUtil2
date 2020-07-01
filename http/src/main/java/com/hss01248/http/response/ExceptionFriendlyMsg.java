package com.hss01248.http.response;

import android.net.ParseException;
import android.text.TextUtils;

import com.hss01248.http.GlobalConfig;
import com.hss01248.http.HttpUtil;
import com.hss01248.http.R;
import com.hss01248.http.Tool;
import com.hss01248.http.exceptions.FileDownloadException;
import com.hss01248.http.exceptions.NoNetworkException;
import com.hss01248.http.exceptions.RequestConfigCheckException;
import com.hss01248.http.exceptions.ResponseStrEmptyException;

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
class ExceptionFriendlyMsg {

    public static ReturnMsg toFriendlyMsg(Throwable e){
        ReturnMsg bean = null;
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
            String friendlyMsg = "";
            if (code >= 500) {
                friendlyMsg = Tool.getString(R.string.http_50x_server_error);
            } else if (code == 401) {
                friendlyMsg = Tool.getString(R.string.http_unlogin);
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
                    .friendlyMsg(Tool.getString(R.string.json_parse_error))
                    .build();

        } else if (e instanceof ClassCastException) {
            bean = ReturnMsg.newBuilder()
                    .code("ClassCastException")
                    .realMsg(e.getMessage())
                    .friendlyMsg(Tool.getString(R.string.some_error))
                    .build();
        } else if (e instanceof ConnectException) {
            bean = ReturnMsg.newBuilder()
                    .code("ConnectException")
                    .realMsg(e.getMessage())
                    .friendlyMsg(Tool.getString(R.string.httputl_poor_network))
                    .build();
            // ex.message = "连接失败";

        } else if (e instanceof javax.net.ssl.SSLException) {
            bean = ReturnMsg.newBuilder()
                    .code("SSLException")
                    .realMsg(e.getMessage())
                    .friendlyMsg(Tool.getString(R.string.httputil_ssl_error))
                    .build();
            // ex.message = "证书验证失败";

        } else if (e instanceof ConnectTimeoutException
                || e instanceof NoNetworkException
                || e instanceof java.net.SocketTimeoutException
                || e instanceof UnknownHostException) {
            if(!Tool.isNetworkAvailable(HttpUtil.context)){
                bean = ReturnMsg.newBuilder()
                        .code("NoNetworkException")
                        .realMsg(e.getMessage())
                        .friendlyMsg(Tool.getString(R.string.httputl_no_network))
                        .build();
            }else {
                bean = ReturnMsg.newBuilder()
                        .code("ConnectTimeoutException")
                        .realMsg(e.getMessage())
                        .friendlyMsg(Tool.getString(R.string.httputl_timeout))
                        .build();
            }


        }  else if (e instanceof NullPointerException) {

            bean = ReturnMsg.newBuilder()
                    .code("NullPointerException")
                    .realMsg(e.getMessage())
                    .friendlyMsg(Tool.getString(R.string.some_error))
                    .build();

        } else if (e instanceof FileDownloadException) {
            bean = ReturnMsg.newBuilder()
                    .code("FileDownloadException")
                    .realMsg(e.getMessage())
                    .friendlyMsg(Tool.getString(R.string.httputl_filedownload_error))
                    .build();
        } else if (e instanceof TimeoutException) {
            bean = ReturnMsg.newBuilder()
                    .code("TimeoutException")
                    .realMsg(e.getMessage())
                    .friendlyMsg(Tool.getString(R.string.httputl_timeout))
                    .build();
        } else {
            bean = ReturnMsg.newBuilder()
                    .code("UnknownException")
                    .realMsg(e.getMessage())
                    .friendlyMsg(Tool.getString(R.string.some_error))
                    .build();
        }
        if(e.getCause() != null && !TextUtils.isEmpty(e.getCause().getMessage())){
            bean.realMsg = e.getCause().getMessage();
        }
        bean.friendlyMsg = bean.friendlyMsg +"\n" + bean.code + "\n" + bean.realMsg;

        return bean;

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
