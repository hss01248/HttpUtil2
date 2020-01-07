package com.hss01248.http.exceptions;

import com.hss01248.http.ConfigInfo;
import com.hss01248.http.response.ResponseBean;

/**
 * Created by hss on 2018/7/25.
 */

public class NetBaseException extends Exception {

    public ConfigInfo info;
    public ResponseBean responseBean;

    public Throwable e;

    public static NetBaseException wrap(Throwable e,ConfigInfo info){
         NetBaseException exception = new NetBaseException(info,e.getMessage());
         exception.e = e;
         return exception;
    }

    public NetBaseException(String message, ConfigInfo info, ResponseBean responseBean) {
        super(message);
        this.info = info;
        this.responseBean = responseBean;
        this.realCause = message;
    }




    public NetBaseException(ConfigInfo info, String realCause) {
        super(realCause);
        this.info = info;
        this.realCause = realCause;
    }

    public NetBaseException(ConfigInfo info,String realCause,  String msgCanShow) {
        super(realCause);
        this.info = info;
        this.realCause = realCause;
        this.msgCanShow = msgCanShow;
    }

    public String realCause;
    public String msgCanShow;


}
