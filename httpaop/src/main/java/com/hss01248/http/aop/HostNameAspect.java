package com.hss01248.http.aop;

import com.hss01248.http.aop.cerverify.CertErrorInterceptor;
import com.hss01248.http.aop.cerverify.HostNameCerChecker;

import okhttp3.OkHttpClient;

public class HostNameAspect implements OkhttpAspect.OkhttpHook {
    @Override
    public void beforeBuild(OkHttpClient.Builder builder) {
        builder.hostnameVerifier(new HostNameCerChecker());
        //builder.addInterceptor(new CertErrorInterceptor());
    }
}
