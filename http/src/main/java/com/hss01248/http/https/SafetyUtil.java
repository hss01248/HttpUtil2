package com.hss01248.http.https;

import android.content.Context;
import android.net.Proxy;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import okhttp3.CertificatePinner;
import okhttp3.OkHttpClient;

/**
 * by hss
 * data:2020/7/14
 * desc:
 * ssl piner
 * http://appblog.cn/2020/01/02/OKHttp%E9%94%81%E5%AE%9A%E8%AF%81%E4%B9%A6CertificatePinner/
 */
public class SafetyUtil {

    private static Map<String,String> pins = new HashMap<>();
    private static Map<String,Boolean> pinsConfig = new HashMap<>();//默认关

    /**
     * 只能识别本机. 万一本机就是要代理才能联网,就误杀了
     * @param context
     * @return
     */
    @Deprecated
    public static boolean isWifiProxy(Context context) {
        CharSequence property;
        int parseInt;
        if ((Build.VERSION.SDK_INT >= 14 ? 1 : null) != null) {
            property = System.getProperty("http.proxyHost");
            String property2 = System.getProperty("http.proxyPort");
            if (TextUtils.isEmpty(property2)) {
                property2 = "-1";
            }
            parseInt = Integer.parseInt(property2);
        } else {
            String host = Proxy.getHost(context);
            parseInt = Proxy.getPort(context);
            property = host;
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("proxyAddress : ");
        stringBuilder.append(property);
        stringBuilder.append(", port : ");
        stringBuilder.append(parseInt);
        Log.i("checkWifiProxy", stringBuilder.toString());
        if (TextUtils.isEmpty(property) || parseInt == -1) {
            return false;
        }
        return true;
    }

    /**
     * 设置不使用本机代理,容易造成误杀,原因同上
     * @param builder
     */
    @Deprecated
    public static void noProxy(OkHttpClient.Builder builder){
        builder.proxy(java.net.Proxy.NO_PROXY);
    }

    public static void setCertificatePinner(OkHttpClient.Builder builder){
        if(pins.isEmpty()){
            return;
        }
        if(pinsConfig.isEmpty()){
            return;
        }

        CertificatePinner.Builder certificatePinner = new CertificatePinner.Builder();
        Iterator<String> iterator = pins.keySet().iterator();
        int enableCount = 0;
        while (iterator.hasNext()){
            String key = (String) iterator.next();
            if(TextUtils.isEmpty(pins.get(key))){
                continue;
            }
            if(pinsConfig.containsKey(key)){
                if(pinsConfig.get(key) != null && pinsConfig.get(key)){
                    certificatePinner.add(key,pins.get(key));
                    enableCount++;
                }
            }
        }
        if(enableCount > 0){
            builder.certificatePinner(certificatePinner.build());
        }


    }

    /**
     * 例子:    .add("publicobject.com", "sha256/afwiKY3RxoMmLkuRW1l7QsPZTJPwDS2pdDROQjXw8ig=")
     *           .add("*.zhihu.com", "sha256/RUZBQThBMjU3RTk2MDhENDkwNThBQkU3NTI1NTA0RUIwNURFMUNFMjYyMjlGQTIwREU2Qjg4NDM0RTZERkZCNg==")
     *
     * 通配符模式规则
     * 星号*只允许出现在最左边的域名标签中，并且必须是该标签(即必须匹配整个最左边的标签)。例如，允许*.example.com，而*a.example.com, a*.example.com, a*b.example.com, a.*.example.com不允许
     * 星号*不能跨域名标签匹配。例如：*.example.com匹配test.example.com，但不匹配sub.test.example.com
     * 不允许为单标签域名使用通配符模式
     * 如果主机名直接或通过通配符模式锁定，将使用直接或通配符固定。例如：*.example.com用pin1固定，a.example.com用pin2固定，检查a.example.com将使用pin1和pin2

     * 警告: 证书锁定是危险的！
     * 锁定证书限制了服务器团队更新TLS证书的能力。通过锁定证书，可以增加操作复杂性，并限制在证书颁发机构之间迁移的能力。如果没有服务器的TLS管理员的许可，不要使用证书固定!
     * @param hostName
     * @param sha256
     */
    public static void addSSLPinner(String hostName, String sha256){
        pins.put(hostName,sha256);
    }

    /**
     * 可以做成远程配置开关
     * @param hostName
     * @param enable
     */
    public static void setSSLPinnerConfig(String hostName, Boolean enable){
        pinsConfig.put(hostName,enable);
    }
}
