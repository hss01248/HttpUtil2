package com.hss01248.http.callback;

import com.hss01248.http.ConfigInfo;
import com.hss01248.http.response.ResponseBean;

import io.reactivex.functions.Consumer;

/**
 * Created by huangshuisheng on 2018/7/25.
 */

public abstract class NetErrorConsumer implements Consumer<Throwable> {
    @Override
    public void accept(Throwable throwable) throws Exception {
        //ErrorCallbackDispatcher.dispatchException(this,throwable);
    }

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
        onError("onServerError:code-"+code+" message:"+message);
    }

    public void onHttpError(int code,String message,String responseBodyStr){
        //onError("OtherHttpError:code-"+code+" message:"+message);
        //HTTP 405 Method Not Allowed
        if(code >=500){
            onServerError(code,message,responseBodyStr);
        }else if(code == 401){
            onHttp401(responseBodyStr);
        }else {
            onError("HttpError:code-"+code+"\nmessage:"+message+"\nresponseStr:"+responseBodyStr);
        }
    }



    public void onJsonParseError(Throwable e){
        onError(e.getMessage());
    }


    public void onEmpty(){
        onError("data is empty");
    }


    /**
     * Callback method that an error has been occurred with the
     * provided error code and optional user-readable message.
     */
    public abstract void onError(String msgCanShow);

    public void onCodeError(int code, String msg, String dataStr, String codeStr,
                            String extra1,String extra2,String extra3,
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
}
