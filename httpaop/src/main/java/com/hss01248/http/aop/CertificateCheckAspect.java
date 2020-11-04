package com.hss01248.http.aop;


import android.util.Log;

import com.hss01248.logforaop.LogMethodAspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;

import okio.ByteString;

@Aspect
public class CertificateCheckAspect {

    public static Map<String,String> piners = new HashMap<>();//*.zhihu.com  ,   7f97cd62f0c950a16c3b9d54a302a4c8a025597657a5a18ae816a1e325fdce62




    @Around("execution(void javax.net.ssl.X509TrustManager.checkServerTrusted(..))")
    public Object weaveJoinPoint(ProceedingJoinPoint joinPoint) throws Throwable {
       return LogMethodAspect.logAround(true, OkhttpAspect.TAG, false, joinPoint, new LogMethodAspect.IAround() {
            @Override
            public void before(ProceedingJoinPoint joinPoin, String desc) {
                X509Certificate[] chain = (X509Certificate[]) joinPoin.getArgs()[0];
                for (int i = 0; i < chain.length; i++) {
                    X509Certificate certificate = chain[i];


                    //CN=*.zhihu.com,OU=IT,O=智者四海（北京）技术有限公司,L=北京市,C=CN
                    //CN=GeoTrust RSA CA 2018,OU=www.digicert.com,O=DigiCert Inc,C=US
                    //CN=DigiCert Global Root CA,OU=www.digicert.com,O=DigiCert Inc,C=US
                    Log.d(OkhttpAspect.TAG, certificate.getSubjectDN().getName());



                    //Log.d(OkhttpAspect.TAG, certificate.checkValidity());//检验有效期


                    //certificate.getSignature()
                            //Subject: C=CN, L=\xE5\x8C\x97\xE4\xBA\xAC\xE5\xB8\x82, O=\xE6\x99\xBA\xE8\x80\x85\xE5\x9B\x9B\xE6\xB5\xB7\xEF\xBC\x88\xE5\x8C\x97\xE4\xBA\xAC\xEF\x
                            // BC\x89\xE6\x8A\x80\xE6\x9C\xAF\xE6\x9C\x89\xE9\x99\x90\xE5\x85\xAC\xE5\x8F\xB8, OU=IT, CN=*.zhihu.com
                    try {
                        Log.d(OkhttpAspect.TAG,ByteString.of(certificate.getEncoded()).sha256().hex());
                    } catch (CertificateEncodingException e) {
                        e.printStackTrace();
                    }
                    //Tool.logJson(certificate);
                }
            }
        });
    }
}
