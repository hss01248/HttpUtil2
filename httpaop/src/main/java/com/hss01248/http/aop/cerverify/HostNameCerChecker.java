package com.hss01248.http.aop.cerverify;

import android.text.TextUtils;
import android.util.Log;

import com.hss01248.http.aop.OkhttpAspect;
import com.hss01248.http.aop.cerverify.IGetCerConfigRequest;

import java.security.cert.Certificate;
import java.security.cert.CertificateExpiredException;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

import okio.ByteString;

public class HostNameCerChecker implements HostnameVerifier {

   static IGetCerConfigRequest configRequest;

    public static void init(IGetCerConfigRequest configRequest){
        HostNameCerChecker.configRequest = configRequest;
        Map<String, String> piners0 = configRequest.requestConfig();
        if(piners0 != null){
            piners.putAll(piners0);
        }

    }

    public static Map<String, String> piners = new HashMap<>();//zhihu.com或者mail.zhihu.com ,   7f97cd62f0c950a16c3b9d54a302a4c8a025597657a5a18ae816a1e325fdce62

    static {
        piners = new HashMap<>();
        piners.put("zhihu.com","efaa8a257e9608d49058abe7525504eb05de1ce26229fa20de6b88434e6dffb6-");
    }





    @Override
    public boolean verify(String hostname, SSLSession session) {
        Log.d(OkhttpAspect.TAG, "HostnameVerifier-域名校验开始:" + hostname);

        try {
            Certificate[] peerCertificates = session.getPeerCertificates();
            for (int i = 0; i < peerCertificates.length; i++) {
                Certificate certificate = peerCertificates[i];
                Log.i(OkhttpAspect.TAG, "certificate.getClass():" + certificate.getClass().getName());//com.android.org.conscrypt.OpenSSLX509Certificate
                if(certificate instanceof X509Certificate){
                    X509Certificate x509 = (X509Certificate) certificate;
                    Log.i(OkhttpAspect.TAG, "x509.getSubjectDN():" + x509.getSubjectDN());
                    Log.i(OkhttpAspect.TAG, "x509.getIssuerDN():" + x509.getIssuerDN());
                    try {
                        x509.checkValidity(new Date(System.currentTimeMillis()+24*60*60*1000));
                        Log.i(OkhttpAspect.TAG, "x509.checkValidity: 有效期大于1天:" );
                    }catch (CertificateExpiredException expiredException){
                        Log.i(OkhttpAspect.TAG, "x509.checkValidity: 有效期不足一天" );
                    }
                }



                Log.i(OkhttpAspect.TAG, "getPublicKey().getAlgorithm:" + certificate.getPublicKey().getAlgorithm());
                Log.i(OkhttpAspect.TAG, "getPublicKey().getFormat:" + certificate.getPublicKey().getFormat());

                Log.i(OkhttpAspect.TAG, "certificate.getPublicKey-sha256->hex:" + ByteString.of(certificate.getPublicKey().getEncoded()).sha256().hex());
                Log.i(OkhttpAspect.TAG, "certificate.getPublicKey-sha256->base64:" + ByteString.of(certificate.getPublicKey().getEncoded()).sha256().base64());
                Log.i(OkhttpAspect.TAG, "certificate-sha256->hex:" + ByteString.of(certificate.getEncoded()).sha256().hex());
                //Log.i(OkhttpAspect.TAG, "sha256->utf-8:"+ByteString.of(certificate.getPublicKey().getEncoded()).sha256().utf8());


            }
            Log.i(OkhttpAspect.TAG, Arrays.toString(peerCertificates));
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }

        String matchedSha256 = "";
        if (piners.containsKey(hostname)) {
            matchedSha256 = piners.get(hostname);
        } else {
            Set<Map.Entry<String, String>> entrySet = piners.entrySet();
            for (Map.Entry<String, String> entry : entrySet) {
                String key = entry.getKey();
                if (key.startsWith("*.")) {
                    key = key.substring(2);
                }
                if (hostname.contains(key)) {
                    matchedSha256 = entry.getValue();
                }
            }
        }
        if (TextUtils.isEmpty(matchedSha256)) {
            //不需要校验
            return true;
        }

        //需要校验
        try {
            Certificate[] peerCertificates = session.getPeerCertificates();
            for (int i = 0; i < peerCertificates.length; i++) {
                Certificate certificate = peerCertificates[i];
                if (matchedSha256.equalsIgnoreCase(ByteString.of(certificate.getEncoded()).sha256().hex())) {
                    Log.i(OkhttpAspect.TAG, "host certificate certificate.getEncoded()).sha256().hex() match :" + matchedSha256);
                    return true;
                }
                if (matchedSha256.equalsIgnoreCase(ByteString.of(certificate.getPublicKey().getEncoded()).sha256().hex())) {
                    Log.i(OkhttpAspect.TAG, "host certificate certificate.getPublicKey().getEncoded()).sha256().hex() match :" + matchedSha256);
                    return true;
                }
                    /*Log.i(OkhttpAspect.TAG,"getPublicKey().getAlgorithm:"+certificate.getPublicKey().getAlgorithm());
                    Log.i(OkhttpAspect.TAG,"getPublicKey().getFormat:"+certificate.getPublicKey().getFormat());

                    Log.i(OkhttpAspect.TAG, "certificate.getPublicKey-sha256->toAsciiUppercase:"+ ByteString.of(certificate.getPublicKey().getEncoded()).sha256().toAsciiUppercase());
                    Log.i(OkhttpAspect.TAG, "certificate.getPublicKey-sha256->hex:"+ByteString.of(certificate.getPublicKey().getEncoded()).sha256().hex());
                    Log.i(OkhttpAspect.TAG, "certificate.getPublicKey-sha256->base64:"+ByteString.of(certificate.getPublicKey().getEncoded()).sha256().base64());
                    Log.i(OkhttpAspect.TAG,"certificate-sha256->hex:"+ByteString.of(certificate.getEncoded()).sha256().hex());*/
                //Log.i(OkhttpAspect.TAG, "sha256->utf-8:"+ByteString.of(certificate.getPublicKey().getEncoded()).sha256().utf8());
            }
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        Log.i(OkhttpAspect.TAG, "host certificate not match : expected:" + matchedSha256);
        return false;
    }
}
