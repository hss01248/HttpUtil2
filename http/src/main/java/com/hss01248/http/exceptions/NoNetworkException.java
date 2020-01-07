package com.hss01248.http.exceptions;

import java.net.UnknownHostException;

/**
 * time:2019/7/20
 * author:hss
 * desription:
 */
public class NoNetworkException extends UnknownHostException {

    public NoNetworkException() {
    }

    public NoNetworkException(String message) {
        super(message);
    }


}
