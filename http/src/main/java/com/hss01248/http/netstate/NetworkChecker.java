package com.hss01248.http.netstate;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import com.hss01248.http.GlobalConfig;

import java.net.NetworkInterface;
import java.util.Collections;
import java.util.Enumeration;


/**
 * Created by cyp on 2016/1/25.
 *
 * 对手机网络状态的监测
 */
public class NetworkChecker {

    static public boolean isNetworkAvailable() {
        ConnectivityManager conManager = (ConnectivityManager) GlobalConfig.get().getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (conManager != null) {
            NetworkInfo[] netInf = conManager.getAllNetworkInfo();
            for (int i = 0; i < netInf.length; i++) {
                if (netInf[i].getState() == NetworkInfo.State.CONNECTED) {
                    return true;
                }
            }
        }
        return false;
    }

    /*
     * 判断设备 是否使用代理上网
     * */
    private boolean isWifiProxy(Context context) {

        final boolean IS_ICS_OR_LATER = Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH;

        String proxyAddress;

        int proxyPort;

        if (IS_ICS_OR_LATER) {

            proxyAddress = System.getProperty("http.proxyHost");

            String portStr = System.getProperty("http.proxyPort");

            proxyPort = Integer.parseInt((portStr != null ? portStr : "-1"));

        } else {

            proxyAddress = android.net.Proxy.getHost(context);

            proxyPort = android.net.Proxy.getPort(context);

        }

        return (!TextUtils.isEmpty(proxyAddress)) && (proxyPort != -1);

    }


    public static boolean isVpnUsed() {
        try {
            Enumeration<NetworkInterface> niList = NetworkInterface.getNetworkInterfaces();
            if(niList != null) {
                for (NetworkInterface intf : Collections.list(niList)) {
                    if(!intf.isUp() || intf.getInterfaceAddresses().isEmpty()) {
                        continue;
                    }
                    Log.d("-----", "isVpnUsed() NetworkInterface Name: " + intf.getName());
                    if ("tun0".equals(intf.getName()) || "ppp0".equals(intf.getName())){
                        return true; // The VPN is up
                    }
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return false;
    }

   /* public static void printIpAddress(String url){
        if(!Config.enableLog()){
            return;
        }
        ThreadPoolFactory.instance().executeImmediately(new Runnable() {
            @Override
            public void run() {

                try {
                    Uri uri = Uri.parse(url);
                    String host = uri.getHost();
                    InetAddress[] addresses = InetAddress.getAllByName(host);
                    if(addresses == null || addresses.length == 0){
                        XLogUtil.w("ip is empty");
                        return;
                    }
                    XLogUtil.w("InetAddress.getAllByName:"+Arrays.toString(addresses));



                    Enumeration<NetworkInterface> niList = NetworkInterface.getNetworkInterfaces();
                    if(niList != null) {
                        for (NetworkInterface intf : Collections.list(niList)) {
                            //intf.isup()
                            if(intf.getInterfaceAddresses().isEmpty()) {
                                continue;
                            }
                            XLogUtil.obj(intf.getInetAddresses());
                        }
                    }

                    //代理：
                    String proxyAddress = System.getProperty("http.proxyHost");
                    String portStr = System.getProperty("http.proxyPort");
                    XLogUtil.w("proxyHost:"+proxyAddress+":"+portStr);

                    //获取ip：http://ip.taobao.com/service/getIpInfo2.php?ip=myip

                    getMyIp();



                }catch (Throwable throwable){
                    throwable.printStackTrace();
                }
            }
        });
    }*/


    /**
     * http://pv.sohu.com/cityjson?ie=utf-8
     */
    private static void getMyIp() {
        /*HttpUtil.request("http://ip.taobao.com/service/getIpInfo2.php?ip=myip",MyIpByTaobao.class)
                .addHeader("User-Agent","Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/76.0.3809.100 Safari/537.36")
                .callback(new MyNetCallback<MyIpByTaobao>() {
                   // @Override
                    public void onSuccess(MyIpByTaobao obj, String responseStr, boolean isFromCache, long sysTime) {
                        //XLogUtil.w("my ip and address:"+obj.data.ip+","+obj.data.country+" "+obj.data.region+" "+obj.data.city+","+obj.data.isp);
                    }

                    @Override
                    public void onSuccess(MyIpByTaobao response) {

                    }

                    @Override
                    public void onError(String msgCanShow) {

                    }
                })
                .request();*/
    }






}
