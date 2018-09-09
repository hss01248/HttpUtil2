package com.hss01248.http.exceptions;

import com.hss01248.http.ConfigInfo;

/**
 * Created by hss on 2018/7/24.
 */

public class ResponseStrEmptyException extends NetBaseException {
    public ResponseStrEmptyException(ConfigInfo info, String realCause) {
        super(info, realCause);
    }

    public ResponseStrEmptyException(ConfigInfo info, String realCause, String msgCanShow) {
        super(info, realCause, msgCanShow);
    }
}
