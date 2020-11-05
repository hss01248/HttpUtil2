package com.hss01248.http.aop.cerverify;

import android.util.Log;


import java.io.IOException;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.net.ssl.SSLPeerUnverifiedException;

import okhttp3.Interceptor;
import okhttp3.Response;

public class CertErrorInterceptor implements Interceptor {
    static AtomicBoolean requesting = new AtomicBoolean(false);
    @Override
    public Response intercept(Chain chain) throws IOException {
        try {
            return chain.proceed(chain.request());
        } catch (Throwable throwable) {
            if (throwable instanceof SSLPeerUnverifiedException
                    && throwable.getMessage().contains("Hostname")
                    && throwable.getMessage().contains("not verified")) {
                //证书校验抛出的
                Log.e("dd", "cert error", throwable);
                if (HostNameCerChecker.configRequest != null) {
                    /*if(requesting.get()){

                    }*/
                    Map<String, String> piners0 = HostNameCerChecker.configRequest.requestConfig();
                    if (piners0 != null) {
                        HostNameCerChecker.piners.putAll(piners0);
                        //继续请求
                        return chain.proceed(chain.request());
                    }
                }
            }
            throw throwable;
        }
    }
}
