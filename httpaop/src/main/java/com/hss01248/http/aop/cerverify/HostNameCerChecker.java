package com.hss01248.http.aop.cerverify;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;

import java.security.cert.Certificate;
import java.security.cert.CertificateExpiredException;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
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
 * 2020-11-06 15:06:55.884 12380-12458/com.hss01248.httpdemo W/Certhh: RxCachedThreadScheduler-2 准备拉配置-zhuanlan.zhihu.com
 * 2020-11-06 15:06:57.885 12380-12458/com.hss01248.httpdemo W/Certhh: RxCachedThreadScheduler-2 piners0-{zhihu.com=efaa8a257e9608d49058abe7525504eb05de1ce26229fa20de6b88434e6dffb6--}
 * 2020-11-06 15:06:57.885 12380-12458/com.hss01248.httpdemo W/Certhh: RxCachedThreadScheduler-2 已经拉到了配置-切换开关,并重新校验
 * 2020-11-06 15:06:57.890 12380-12458/com.hss01248.httpdemo W/Certhh: RxCachedThreadScheduler-2 已经拉过了配置,不再进锁-zhuanlan.zhihu.com
 * 2020-11-06 15:06:57.890 12380-12458/com.hss01248.httpdemo W/Certhh: RxCachedThreadScheduler-2 重新校验是否通过:false
 * 2020-11-06 15:06:57.891 12380-12466/com.hss01248.httpdemo W/zygote64: Long monitor contention with owner RxCachedThreadScheduler-2 (12458) at void java.lang.Thread.sleep(java.lang.Object, long, int)(Thread.java:-2) waiters=0 in boolean com.hss01248.http.aop.cerverify.HostNameCerChecker.verify(java.lang.String, javax.net.ssl.SSLSession) for 2.005s
 * 2020-11-06 15:06:57.891 12380-12466/com.hss01248.httpdemo W/Certhh: RxCachedThreadScheduler-5 已经拉到了配置,不再请求,在锁内直接重新校验-zhuanlan.zhihu.com
 * 2020-11-06 15:06:57.899 12380-12466/com.hss01248.httpdemo W/Certhh: RxCachedThreadScheduler-5 已经拉过了配置,不再进锁-zhuanlan.zhihu.com
 * 2020-11-06 15:06:57.900 12380-12466/com.hss01248.httpdemo W/Certhh: RxCachedThreadScheduler-5 重新校验是否通过:false
 * 2020-11-06 15:06:57.900 12380-12457/com.hss01248.httpdemo W/zygote64: Long monitor contention with owner RxCachedThreadScheduler-2 (12458) at void java.lang.Thread.sleep(java.lang.Object, long, int)(Thread.java:-2) waiters=1 in boolean com.hss01248.http.aop.cerverify.HostNameCerChecker.verify(java.lang.String, javax.net.ssl.SSLSession) for 2.010s
 * 2020-11-06 15:06:57.900 12380-12457/com.hss01248.httpdemo W/Certhh: RxCachedThreadScheduler-1 已经拉到了配置,不再请求,在锁内直接重新校验-zhuanlan.zhihu.com
 * <p>
 * <p>
 * 本地不匹配,远程匹配,触发拉配置,新配置校验成功后,网络请求成功
 * 2020-11-06 15:11:25.395 12824-12874/com.hss01248.httpdemo W/Certhh: RxCachedThreadScheduler-2 准备拉配置-zhuanlan.zhihu.com
 * 2020-11-06 15:11:27.396 12824-12874/com.hss01248.httpdemo W/Certhh: RxCachedThreadScheduler-2 piners0-{zhihu.com=efaa8a257e9608d49058abe7525504eb05de1ce26229fa20de6b88434e6dffb6}
 * 2020-11-06 15:11:27.396 12824-12874/com.hss01248.httpdemo W/Certhh: RxCachedThreadScheduler-2 已经拉到了配置-切换开关,并重新校验
 * 2020-11-06 15:11:27.403 12824-12874/com.hss01248.httpdemo W/Certhh: RxCachedThreadScheduler-2 重新校验是否通过:true
 * 2020-11-06 15:11:27.403 12824-12878/com.hss01248.httpdemo W/zygote64: Long monitor contention with owner RxCachedThreadScheduler-2 (12874) at void java.lang.Thread.sleep(java.lang.Object, long, int)(Thread.java:-2) waiters=0 in boolean com.hss01248.http.aop.cerverify.HostNameCerChecker.verify(java.lang.String, javax.net.ssl.SSLSession) for 1.997s
 * 2020-11-06 15:11:27.403 12824-12878/com.hss01248.httpdemo W/Certhh: RxCachedThreadScheduler-4 已经拉到了配置,不再请求,在锁内直接重新校验-zhuanlan.zhihu.com
 * 2020-11-06 15:11:27.409 12824-12878/com.hss01248.httpdemo W/Certhh: RxCachedThreadScheduler-4 重新校验是否通过:true
 * 2020-11-06 15:11:27.410 12824-12873/com.hss01248.httpdemo W/zygote64: Long monitor contention with owner RxCachedThreadScheduler-2 (12874) at void java.lang.Thread.sleep(java.lang.Object, long, int)(Thread.java:-2) waiters=1 in boolean com.hss01248.http.aop.cerverify.HostNameCerChecker.verify(java.lang.String, javax.net.ssl.SSLSession) for 2.004s
 * 2020-11-06 15:11:27.410 12824-12873/com.hss01248.httpdemo W/Certhh: RxCachedThreadScheduler-1 已经拉到了配置,不再请求,在锁内直接重新校验-zhuanlan.zhihu.com
 * 2020-11-06 15:11:27.418 12824-12873/com.hss01248.httpdemo W/Certhh: RxCachedThreadScheduler-1 重新校验是否通过:true
 */
public class HostNameCerChecker implements HostnameVerifier {

    static IGetCerConfigRequest configRequest;
    static Context context;
    public static boolean enableLog = false;
    static AtomicBoolean requested = new AtomicBoolean(false);
    static String TAG = "Certhh";

    public static void init(Context context,IGetCerConfigRequest configRequest) {
        HostNameCerChecker.context = context;
        HostNameCerChecker.configRequest = configRequest;
        piners.putAll(configRequest.defaultConfig());
        //读取上次缓存sp的配置
        readFromSp();
        Map<String, String> piners0 = configRequest.requestConfig();
        if (piners0 != null) {
            piners.putAll(piners0);
            writeSp();
        }

    }

    private static void readFromSp() {
        try {
            SharedPreferences sp =  context.getSharedPreferences("HostNameCerChecker",Context.MODE_PRIVATE);
            String config = sp.getString("HostNameCer","");
            if(!TextUtils.isEmpty(config)){
                Map map = new Gson().fromJson(config,Map.class);
                piners.putAll(map);
            }
        }catch (Throwable throwable){
            throwable.printStackTrace();
        }

    }

    private static void writeSp() {
        try {
            SharedPreferences sp =  context.getSharedPreferences("HostNameCerChecker",Context.MODE_PRIVATE);
            String str = new Gson().toJson(piners);
            sp.edit().putString("HostNameCer",str).apply();
        }catch (Throwable throwable){
            throwable.printStackTrace();
        }

    }

    static Map<String, String> piners = new HashMap<>();//zhihu.com或者mail.zhihu.com ,   7f97cd62f0c950a16c3b9d54a302a4c8a025597657a5a18ae816a1e325fdce62


    @Override
    public boolean verify(String hostname, SSLSession session) {
        log(hostname, session);


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
                    i(TAG, "host certificate certificate.getEncoded()).sha256().hex() match :" + matchedSha256);
                    return true;
                }
                if (matchedSha256.equalsIgnoreCase(ByteString.of(certificate.getPublicKey().getEncoded()).sha256().hex())) {
                    i(TAG, "host certificate certificate.getPublicKey().getEncoded()).sha256().hex() match :" + matchedSha256);
                    return true;
                }
            }

            //走到这里,说明需要校验,那么:
            if (HostNameCerChecker.configRequest == null) {
                return false;
            }
            if (requested.get()) {
                w(TAG, Thread.currentThread().getName() + " 已经拉过了配置,不再进锁-" + hostname);
                return false;
            }
            synchronized (CertErrorInterceptor.class) {
                if (requested.get()) {
                    w(TAG, Thread.currentThread().getName() + " 已经拉到了配置,不再请求,在锁内直接重新校验-" + hostname);
                    boolean v = verify(hostname, session);
                    w(TAG, Thread.currentThread().getName() + " 重新校验是否通过:" + v);
                    return v;
                }
                w(TAG, Thread.currentThread().getName() + " 准备拉配置-" + hostname);
                Map<String, String> piners0 = HostNameCerChecker.configRequest.requestConfig();
                w(TAG, Thread.currentThread().getName() + " piners0-" + piners0);
                if (piners0 != null) {
                    HostNameCerChecker.piners.putAll(piners0);
                    writeSp();
                    w(TAG, Thread.currentThread().getName() + " 已经拉到了配置-切换开关,并重新校验");
                    requested.getAndSet(true);
                    //重新校验
                    boolean v = verify(hostname, session);
                    w(TAG, Thread.currentThread().getName() + " 重新校验是否通过:" + v);
                    return v;
                }
                w(TAG, Thread.currentThread().getName() + " 已经拉到了配置-切换开关, 配置为空");
                requested.getAndSet(true);
            }

        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        w(TAG, Thread.currentThread().getName() + "   host certificate not match : expected:" + matchedSha256);
        return false;
    }

    void w(String tag, String msg) {
        if (enableLog) {
            w(tag, msg);
        }
    }

    void i(String tag, String msg) {
        if (enableLog) {
            i(tag, msg);
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
