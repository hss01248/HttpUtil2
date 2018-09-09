package com.hss01248.http.exceptions;

import com.hss01248.http.ConfigInfo;
import com.hss01248.http.response.ResponseBean;

/**
 * Created by hss on 2018/7/28.
 */

public class FileDownloadException extends NetBaseException {
    public FileDownloadException(String message, ConfigInfo info, ResponseBean responseBean) {
        super(message, info, responseBean);
    }
}
