package com.hss01248.http.aop;

import android.os.Build;
import android.util.Log;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.OkHttpClient;
import okio.ByteString;


public class IgnoreSslVerifyForAll implements OkhttpAspect.OkhttpHook {

    public static boolean enable = true;
    @Override
    public void beforeBuild(OkHttpClient.Builder builder) {
        if(enable){
            setAllCerPass(builder);
            Log.d(OkhttpAspect.TAG,"设置忽略证书校验:"+ builder.toString());
        }

    }

    /**
     * 让客户端通过所有证书的验证.
     * 注意:容易导致中间人攻击,轻易不要使用
     *
     * @param httpBuilder
     */
    public static void setAllCerPass(OkHttpClient.Builder httpBuilder) {
        X509TrustManager xtm = new X509TrustManager() {
            @Override
            public void checkClientTrusted(X509Certificate[] chain, String authType) {
                Log.d(OkhttpAspect.TAG,"checkClientTrusted-证书校验开始,直接忽略校验:"+ chain);
            }

            @Override
            public void checkServerTrusted(X509Certificate[] chain, String authType) {
                for (int i = 0; i < chain.length; i++) {
                    X509Certificate certificate = chain[i];
                    //Tool.logw(ByteString.of(certificate.getPublicKey().getEncoded()).sha256().hex().toString());
                    //Tool.logJson(certificate);
                }
                Log.d(OkhttpAspect.TAG,"checkServerTrusted-证书校验开始,直接忽略校验:"+ chain);
            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                X509Certificate[] x509Certificates = new X509Certificate[]{};
                return x509Certificates;
                // return null;
            }
        };

        SSLContext sslContext = null;
        try {
            sslContext = SSLContext.getInstance("SSL");

            sslContext.init(null, new TrustManager[]{xtm}, new SecureRandom());

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }


        HostnameVerifier DO_NOT_VERIFY = new HostnameVerifier() {
            //@Override
            public boolean verify(String hostname, SSLSession session) {
                Log.d(OkhttpAspect.TAG,"HostnameVerifier-域名校验开始,直接忽略校验:"+hostname);
                return true;
            }
        };

        httpBuilder.sslSocketFactory(sslContext.getSocketFactory())
                .hostnameVerifier(DO_NOT_VERIFY);
    }
}
