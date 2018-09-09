package com.hss01248.http.exceptions;

import com.hss01248.http.ConfigInfo;

/**
 * Created by hss on 2018/7/25.
 */

public class RequestConfigCheckException extends NetBaseException {


    public RequestConfigCheckException(ConfigInfo info, String realCause) {
        super(info, realCause);
    }

    public RequestConfigCheckException(ConfigInfo info, String realCause, String msgCanShow) {
        super(info, realCause, msgCanShow);
    }
}
