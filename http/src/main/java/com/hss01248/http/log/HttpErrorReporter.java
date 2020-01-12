package com.hss01248.http.log;

import android.text.TextUtils;

import com.hss01248.http.GlobalConfig;
import com.hss01248.http.Tool;

import java.io.IOException;
import java.net.ConnectException;
import java.net.UnknownHostException;

/**
 * time:2019/8/13
 * author:hss
 * desription:
 */
public class HttpErrorReporter {

    public static void reportHttpError(String url,int code,String  msg,boolean isOkHttp){
        try {
            url = filterUrl(url);
            if(TextUtils.isEmpty(url)){
                return;
            }
            String code2 = code+"";
        /*if(Config.enableLog()){
            code2 = code + (isOkHttp ? "" : "-asynchttp");
        }*/

            GlobalConfig.get().getTool().reportError(code2,"",url);
        }catch (Throwable e){
            e.printStackTrace();
        }
    }



    public static void reportNetworkException(String url,Throwable e,boolean isOkHttp){
        if(e == null){
            return;
        }
        try {
            url = filterUrl(url);
            if(TextUtils.isEmpty(url)){
                return;
            }
            String msg = e.getMessage();
            if(TextUtils.isEmpty(msg)){
                msg = "";
            }
            if("Socket is closed".equals(msg)){
                msg = "Socket closed";
            }else if(msg.equals("Canceled")){
                if(!GlobalConfig.get().isDebug()){
                    return;
                }
            }
            if("Socket closed".equals(msg)){
                if(!GlobalConfig.get().isDebug()){
                    return;
                }
            }
            //XLogUtil.exception(e.getMessage(),e);
            //IOException-okhttp(Unexpected response code for CONNECT: 503)
            //throw new IOException(
            //         "Unexpected response code for CONNECT: " + response.code());
            //RealConnection.createTunnel(RealConnection.java:411)
            //适配内部exception
            if(e instanceof IOException && !TextUtils.isEmpty(e.getMessage())){
                if(e.getMessage().startsWith("Unexpected response code for CONNECT: ")){
                    int idx = e.getMessage().indexOf(": ");
                    try {
                        String code = e.getMessage().substring(idx+1);
                        GlobalConfig.get().getTool().reportError(code,"",url);
                        return;
                    }catch (Exception e3){
                        e3.printStackTrace();
                    }
                }else {
                    //java.io.IOException: UnknownHostException exception:
                    // Unable to resolve host "dev-id-app.xxx.com": No address associated with hostname
                    //java.net.UnknownHostException: Unable to resolve host "dev-id-app.xxx.com": No address associated with hostname
                    if(e.getMessage().startsWith("UnknownHostException exception: ")){
                        String msg2 = e.getMessage().substring(e.getMessage().indexOf(": "));
                        e = new UnknownHostException(msg2);
                    }
                }
            }

            String code = e.getClass().getSimpleName();
            //处理近似的://|| e instanceof HttpHostConnectException
            //RealConnection.createTunnel(RealConnection.java:411) :
            //
            if(e instanceof UnknownHostException || e instanceof ConnectException){
                //有点性能问题，是否放到子线程？
                boolean isNetWorkConnected = Tool.isNetworkAvailable();
                if(!isNetWorkConnected){
                    msg = "network not connected";
                    code = "NoNetworkException";
                }
            }


            //java.io.IOException: UnknownHostException exception:
            // Unable to resolve host "dev-id-app.xxx.com": No address associated with hostname

            //java.net.UnknownHostException: Unable to resolve host "dev-id-app.xxx.com": No address associated with hostname

            String code2 = code+"";
            /*if(Config.enableLog()){
                code2 = code + (isOkHttp ? "-okhttp" : "-asynchttp");
            }*/
            GlobalConfig.get().getTool().reportError(code2,msg,url);
        }catch (Throwable e2){
            //ExceptionReporterHelper.reportException(e2);
        }

    }


    private static String filterUrl(String url) {
        if(TextUtils.isEmpty(url)){
            return "";
        }
        //图片类的下载,上传不care
        if(url.endsWith(".jpg") || url.endsWith(".png") || url.endsWith(".jpeg")){
            return "";
        }
        if(url.contains("?")){
            return url.substring(0,url.indexOf("?"));
        }
        return url;
    }



}

