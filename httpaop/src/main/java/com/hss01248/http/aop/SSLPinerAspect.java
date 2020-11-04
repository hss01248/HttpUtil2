package com.hss01248.http.aop;


import android.util.Log;

import com.hss01248.logforaop.LogMethodAspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;


import java.security.cert.Certificate;
import java.util.List;

import okhttp3.CertificatePinner;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okio.ByteString;

//@Aspect
public class SSLPinerAspect implements OkhttpAspect.OkhttpHook {
    @Override
    public void beforeBuild(OkHttpClient.Builder builder) {
        CertificatePinner.Builder certificatePinner = new CertificatePinner.Builder();
        certificatePinner.add("*.zhihu.com","sha256/RUZBQThBMjU3RTk2MDhENDkwNThBQkU3NTI1NTA0RUIwNURFMUNFMjYyMjlGQTIwREU2Qjg4NDM0RTZERkZCNg==");
        builder.certificatePinner(certificatePinner.build());
    }

    //static final String client = "execution(void okhttp3.CertificatePinner.check(..))";

    //@Before(client)
    public void addLog(JoinPoint joinPoint) throws Throwable {
        LogMethodAspect.logBefore(true, OkhttpAspect.TAG, joinPoint, new LogMethodAspect.IBefore() {
            @Override
            public void before(JoinPoint joinPoin, String desc) {
                try {
                    List<Certificate> peerCertificates = (List<Certificate>) joinPoin.getArgs()[1];
                    for (int i = 0; i < peerCertificates.size(); i++) {
                        Certificate certificate = peerCertificates.get(i);
                        Log.i(OkhttpAspect.TAG,"getPublicKey().getAlgorithm:"+certificate.getPublicKey().getAlgorithm());
                        Log.i(OkhttpAspect.TAG,"getPublicKey().getFormat:"+certificate.getPublicKey().getFormat());

                        Log.i(OkhttpAspect.TAG, "certificate.getPublicKey-sha256->toAsciiUppercase:"+ByteString.of(certificate.getPublicKey().getEncoded()).sha256().toAsciiUppercase());
                        Log.i(OkhttpAspect.TAG, "certificate.getPublicKey-sha256->hex:"+ByteString.of(certificate.getPublicKey().getEncoded()).sha256().hex());
                        Log.i(OkhttpAspect.TAG, "certificate.getPublicKey-sha256->base64:"+ByteString.of(certificate.getPublicKey().getEncoded()).sha256().base64());
                        Log.i(OkhttpAspect.TAG,"certificate-sha256->hex:"+ByteString.of(certificate.getEncoded()).sha256().hex());
                        //Log.i(OkhttpAspect.TAG, "sha256->utf-8:"+ByteString.of(certificate.getPublicKey().getEncoded()).sha256().utf8());
                    }
                }catch (Throwable throwable){
                    throwable.printStackTrace();
                }

            }

            @Override
            public String descExtraForLog() {
                return "";
            }
        });
    }

    /**
     *
     * @param hostname 如 *.zhihu.com
     * @param url 如https://www.zhihu.com
     *
     * 方法功能: 通过错误的pin,让日志打印出正确的pin
     * 注意: 不能同时忽略证书
     */
    public static void testSSlPin(String hostname,String url){
        CertificatePinner certificatePinner = new CertificatePinner.Builder()
                .add(hostname, "sha256/AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA=")//sha256/f5fNYvDJUKFsO51UowKkyKAlWXZXpaGK6Bah4yX9zmI=)
                .build();
        OkHttpClient.Builder client = new OkHttpClient.Builder();
        client.certificatePinner(certificatePinner);

        Request request = new Request.Builder()
                .url(url)
                .build();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    client.build().newCall(request).execute();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
