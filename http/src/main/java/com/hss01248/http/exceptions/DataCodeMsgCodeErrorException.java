package com.hss01248.http.exceptions;


import com.hss01248.http.ConfigInfo;
import com.hss01248.http.response.ResponseBean;

/**
 * Created by hss on 2018/7/24.
 */

public class DataCodeMsgCodeErrorException extends NetBaseException{


    public DataCodeMsgCodeErrorException(String message, ConfigInfo info, ResponseBean responseBean) {
        super(message, info, responseBean);
    }
}
