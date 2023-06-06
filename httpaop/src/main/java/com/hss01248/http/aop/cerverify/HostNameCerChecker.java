package com.hss01248.http.aop.cerverify;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


import java.security.cert.Certificate;
import java.security.cert.CertificateExpiredException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

import okio.ByteString;

/**
 * 使用:
 * HostNameCerChecker.init()  IGetCerConfigRequest: 接口里实现拉配置的请求.注意需要同步实现
 * HostNameCerChecker.enableLog = true
 *
 * 设置给okhttpbuilder.hostnameverifyer(new HostNameCerChecker())
 * <p>
 * <p>
 * <p>
 * 测试结果:
 * <p>
 * 本地不匹配,远程也不匹配  触发拉配置,新配置校验失败后,网络请求失败
 *
 RxCachedThreadScheduler-2 准备拉配置-zhuanlan.zhihu.com
RxCachedThreadScheduler-2 piners0-{zhihu.com=efaa8a257e9608d49058abe7525504eb05de1ce26229fa20de6b88434e6dffb6--}
RxCachedThreadScheduler-2 已经拉到了配置-切换开关,并重新校验
 RxCachedThreadScheduler-2 已经拉过了配置,不再进锁-zhuanlan.zhihu.com
 RxCachedThreadScheduler-2 重新校验是否通过:false
RxCachedThreadScheduler-5 已经拉到了配置,不再请求,在锁内直接重新校验-zhuanlan.zhihu.com
RxCachedThreadScheduler-5 已经拉过了配置,不再进锁-zhuanlan.zhihu.com
RxCachedThreadScheduler-5 重新校验是否通过:false
 RxCachedThreadScheduler-1 已经拉到了配置,不再请求,在锁内直接重新校验-zhuanlan.zhihu.com
 * <p>
 * <p>
 * 本地不匹配,远程匹配,触发拉配置,新配置校验成功后,网络请求成功
 *
 RxCachedThreadScheduler-2 准备拉配置-zhuanlan.zhihu.com
RxCachedThreadScheduler-2 piners0-{zhihu.com=efaa8a257e9608d49058abe7525504eb05de1ce26229fa20de6b88434e6dffb6}
RxCachedThreadScheduler-2 已经拉到了配置-切换开关,并重新校验
RxCachedThreadScheduler-2 重新校验是否通过:true
RxCachedThreadScheduler-4 已经拉到了配置,不再请求,在锁内直接重新校验-zhuanlan.zhihu.com
RxCachedThreadScheduler-4 重新校验是否通过:true
RxCachedThreadScheduler-1 已经拉到了配置,不再请求,在锁内直接重新校验-zhuanlan.zhihu.com
RxCachedThreadScheduler-1 重新校验是否通过:true
 */
public class HostNameCerChecker implements HostnameVerifier {

    static IGetCerConfigRequest configRequest;
    static Context context;
    public static boolean enableLog ;

    static Map<String, List<String>> piners = new HashMap<>();//zhihu.com或者mail.zhihu.com ,   7f97cd62f0c950a16c3b9d54a302a4c8a025597657a5a18ae816a1e325fdce62
    public static boolean isOpen() {
        return open;
    }

    public static boolean open = false;
    static AtomicBoolean requested = new AtomicBoolean(false);
    static String TAG = "cercheck";
    static String SP_XX = "ExecutorsAtomic";
    static String SP_KEY = "THREADS";

    /**
     * 全局忽略证书锁定
     */
    static boolean ignoreHostNameCheck;


    /**
     *
     * @param context
     * @param ignoreHostNameCheck 忽略证书锁定,一般测试环境设置为true,便于抓包,线上设置false.
     * @param configRequest
     */
    public static void init(Context context,
                            boolean ignoreHostNameCheck,
                            IGetCerConfigRequest configRequest) {
        HostNameCerChecker.context = context;
        HostNameCerChecker.configRequest = configRequest;
        HostNameCerChecker.ignoreHostNameCheck = ignoreHostNameCheck;
        piners.putAll(configRequest.defaultConfig());
        open = !piners.isEmpty();
        //读取上次缓存sp的配置
        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                readFromSp();
                //doRequestSync();

            }
        });


    }

    public static void requestConfig(){
        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                doRequestSync();
            }
        });
    }

    private static void doRequestSync() {
        Map<String, List<String>> piners0 = configRequest.requestConfig();
        if (piners0 != null) {
            if(piners0.isEmpty()){
                i(TAG, Thread.currentThread().getName() + " requestConfig 远程返回空集合,功能关闭 ");
                //远程返回为空,代表此功能关闭
                open  = false;
                piners.clear();
                writeSp();
            }else {
                i(TAG, Thread.currentThread().getName() + " requestConfig 远程返回,功能继续开启 ");
                open = true;
                //如果不clear,那么以前某个错误的host配置会一直保留,永远无法纠正
                piners.clear();
                piners.putAll(piners0);
                writeSp();
            }
        }else {
            w(TAG, Thread.currentThread().getName() + " requestConfig 请求失败 ");
        }
    }

    private static void readFromSp() {
        try {
            String config =  configRequest.getString(SP_KEY,"");
            w(TAG, Thread.currentThread().getName() + " sp- " + config);
            if(!TextUtils.isEmpty(config)){
                List<CerConfig> cerConfigs = new Gson().fromJson(config,new TypeToken<List<CerConfig>>(){}.getType());
                if(cerConfigs !=  null && !cerConfigs.isEmpty()){
                    for (CerConfig cerConfig : cerConfigs) {
                        piners.put(cerConfig.host,cerConfig.cetificatePrints);
                    }
                }
            }
        }catch (Throwable throwable){
            throwable.printStackTrace();
        }

    }

    private static void writeSp() {
        try {
            List<CerConfig> cerConfigs = new ArrayList<>();
            if(piners.isEmpty()){
                return;
            }
            for (Map.Entry<String, List<String>> stringListEntry : piners.entrySet()) {
                CerConfig cerConfig = new CerConfig();
                cerConfig.host = stringListEntry.getKey();
                cerConfig.cetificatePrints = stringListEntry.getValue();
                cerConfigs.add(cerConfig);
            }
            String str = new Gson().toJson(cerConfigs);
            configRequest.putString(SP_KEY,str);
        }catch (Throwable throwable){
            throwable.printStackTrace();
        }

    }




    @Override
    public boolean verify(String hostname, SSLSession session) {
        //w(TAG, Thread.currentThread().getName() + " hostname: "+hostname);
        if(ignoreHostNameCheck){
            w(TAG, Thread.currentThread().getName() + " 证书指纹锁定功能全局关闭");
            return true;
        }
        if(!open){
            w(TAG, Thread.currentThread().getName() + " 证书指纹锁定功能已经关闭");
            return true;
        }
        //log(hostname, session);
        if(piners.isEmpty()){
            w(TAG, Thread.currentThread().getName() + " piners.isEmpty");
            return true;
        }


        List<String> matchedSha256 = null;
        if (piners.containsKey(hostname)) {
            //隐藏bug: 二级域名证书 vs 三级域名证书 不同时,又同时配置,就会出现校验错误. 比如阿里云oss的.
            // 通过规范配置来规避
            matchedSha256 = piners.get(hostname);
        } else {
            Set<Map.Entry<String, List<String>>> entrySet = piners.entrySet();
            for (Map.Entry<String, List<String>> entry : entrySet) {
                String key = entry.getKey();
                if (key.startsWith("*.")) {
                    key = key.substring(1);
                }
                if (hostname.contains(key)) {
                    matchedSha256 = entry.getValue();
                }
            }
        }
        if (matchedSha256 ==  null || matchedSha256.isEmpty()) {
            //不需要校验
            //w(TAG, Thread.currentThread().getName() + " 不需要校验:"+hostname);
            return true;
        }

        //需要校验
        try {
            Certificate[] peerCertificates = session.getPeerCertificates();
            for (int i = 0; i < peerCertificates.length; i++) {
                Certificate certificate = peerCertificates[i];
                for (int j = 0; j < matchedSha256.size(); j++) {

                    if (matchedSha256.get(j) != null && matchedSha256.get(j).equalsIgnoreCase(ByteString.of(certificate.getEncoded()).sha256().hex())) {
                        i(TAG, hostname+":host certificate certificate.getEncoded()).sha256().hex() match :" + matchedSha256);
                        return true;
                    }
                    //i(TAG, "compare : host certificate certificate.getEncoded()).sha256().hex()" + ByteString.of(certificate.getEncoded()).sha256().hex());
                    /*if (matchedSha256.get(j) != null && matchedSha256.get(j).equalsIgnoreCase(ByteString.of(certificate.getPublicKey().getEncoded()).sha256().hex())) {
                        w(TAG, "host certificate certificate.getPublicKey().getEncoded()).sha256().hex() match :" + matchedSha256);
                        return true;
                    }*/
                }
            }
            w(TAG, Thread.currentThread().getName() + "   host certificate not match : expected:" + matchedSha256);
            //w(TAG, Thread.currentThread().getName() + "   host certificate not match : actual map:\n" + piners.toString());
            //走到这里,说明需要校验,那么:
            if (HostNameCerChecker.configRequest == null) {
                return false;
            }
            if (requested.get()) {
                i(TAG, Thread.currentThread().getName() + " 已经拉过了配置,也没有校验通过, 不再进锁-" + hostname);

                return false;
            }
            synchronized (HostNameCerChecker.class) {
                if (requested.get()) {
                    i(TAG, Thread.currentThread().getName() + " 已经拉到了配置,不再请求,在锁内直接重新校验-" + hostname);
                    boolean v = verify(hostname, session);
                    i(TAG, Thread.currentThread().getName() + " 重新校验是否通过:" + v);
                    return v;
                }
                i(TAG, Thread.currentThread().getName() + " 准备拉配置-" + hostname);

                doRequestSync();
                i(TAG, Thread.currentThread().getName() + " 已经拉到了配置-切换开关,并重新校验");
                requested.getAndSet(true);
                //重新校验
                boolean v = verify(hostname, session);
                configRequest.reportException(new CertificateVerifyFailedException(hostname));
                w(TAG, Thread.currentThread().getName() + " 重新校验是否通过:" + v);
                return v;
            }

        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        w(TAG, Thread.currentThread().getName() + "   host certificate not match : expected:" + matchedSha256);
        return false;
    }

   static void w(String tag, String msg) {
        if (enableLog && !TextUtils.isEmpty(msg)) {
            Log.w(tag, msg);
        }
    }
    static void d(String tag, String msg) {
        if (enableLog && !TextUtils.isEmpty(msg)) {
            Log.d(tag, msg);
        }
    }

   static void i(String tag, String msg) {
        if (enableLog && !TextUtils.isEmpty(msg)) {
            Log.i(tag, msg);
        }
    }

    private void log(String hostname, SSLSession session) {
        if (enableLog) {
            Log.d(TAG, "HostnameVerifier-域名校验开始:" + hostname);

            try {
                Certificate[] peerCertificates = session.getPeerCertificates();
                for (int i = 0; i < peerCertificates.length; i++) {
                    Certificate certificate = peerCertificates[i];
                    i(TAG, "certificate.getClass():" + certificate.getClass().getName());//com.android.org.conscrypt.OpenSSLX509Certificate
                    if (certificate instanceof X509Certificate) {
                        X509Certificate x509 = (X509Certificate) certificate;
                        i(TAG, "x509.getSubjectDN():" + x509.getSubjectDN());
                        i(TAG, "x509.getIssuerDN():" + x509.getIssuerDN());
                        try {
                            x509.checkValidity(new Date(System.currentTimeMillis() + 24 * 60 * 60 * 1000));
                            i(TAG, "x509.checkValidity: 有效期大于1天:");
                        } catch (CertificateExpiredException expiredException) {
                            i(TAG, "x509.checkValidity: 有效期不足一天");
                        }
                    }


                    i(TAG, "getPublicKey().getAlgorithm:" + certificate.getPublicKey().getAlgorithm());
                    i(TAG, "getPublicKey().getFormat:" + certificate.getPublicKey().getFormat());

                    i(TAG, "certificate.getPublicKey-sha256->hex:" + ByteString.of(certificate.getPublicKey().getEncoded()).sha256().hex());
                    i(TAG, "certificate.getPublicKey-sha256->base64:" + ByteString.of(certificate.getPublicKey().getEncoded()).sha256().base64());
                    i(TAG, "certificate-sha256->hex:" + ByteString.of(certificate.getEncoded()).sha256().hex());
                    //i(TAG, "sha256->utf-8:"+ByteString.of(certificate.getPublicKey().getEncoded()).sha256().utf8());
                }
                //i(TAG, Arrays.toString(peerCertificates));
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        }
    }


}
