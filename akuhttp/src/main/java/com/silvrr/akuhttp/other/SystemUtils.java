package com.silvrr.akuhttp.other;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import com.silvrr.akuhttp.NetUtil;

import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


import static android.content.Context.TELEPHONY_SERVICE;

/**
 * Created by cyp on 2016/3/23.
 * <p>
 * description : 获取手机系统相关信息
 */
public class SystemUtils {

    /**
     * 获取手机型号
     */
    public static String getPhoneModel() {
        return android.os.Build.MODEL;
    }


    /**
     * 获取当前APP的版本号
     */
    public static int getCurrentVersionCode(Context context) {
        try {
            PackageManager pm = context.getPackageManager();
            // getPackageName()是你当前类的包名，0代表是获取版本信息
            PackageInfo packInfo = pm.getPackageInfo(context.getPackageName(), 0);
            return packInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            return -1;
        }
    }

    /**
     * Android操作系统的版本
     */
    public static String getSystemVersion() {
        return android.os.Build.VERSION.RELEASE;
    }

    /**
     * 获取当前APP的版本名
     */
    public static String getCurrentVersionName(Context context) {
        try {
            PackageManager pm = context.getPackageManager();
            // getPackageName()是你当前类的包名，0代表是获取版本信息
            PackageInfo packInfo = pm.getPackageInfo(context.getPackageName(), 0);
            return packInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            return "";
        }
    }

    /**
     * 服务器指定Android端上传的DeviceType
     */
    public static int getDeviceType() {
        return 4;
    }

    /**
     * 获取手机号码
     *
     * @return
     */
    @SuppressLint("MissingPermission")
    public static String getPhoneNumber() {
        Context context = NetUtil.getContext();
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        return tm != null ? tm.getLine1Number() : null;//手机号码
    }

    /**
     * 获取手机的DeviceId
     */
    public static String getIMEI() {
        String deviceId;
        try {
            Context context = NetUtil.getContext();
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return tm.getDeviceId();
            }
        } catch (Exception e) {
            Log.e("SystemUtils", "imei exception:" + e.getMessage());
            deviceId = null;
        }
        return "";
    }

    public static String[] fetchImei() {
        String imei1 = "";
        String imei2 = "";
        if (Build.VERSION.SDK_INT < 21) {
            imei1 = getIMEI();
            // 21版本是5.0，判断是否是5.0以上的系统  5.0系统直接获取IMEI1,IMEI2,MEID
        } else if (Build.VERSION.SDK_INT >= 21 && Build.VERSION.SDK_INT < 23) {
            imei1 = getImei(0);
            imei2 = getImei(1);
        } else if (Build.VERSION.SDK_INT >= 23) {
            imei1 = getImeiOf23(0);
            imei2 = getImeiOf23(1);
        }
        return new String[]{imei1, imei2};
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private static String getImeiOf23(int slot) {
        String imei1 = "";
        String imei2 = "";
        Context context = NetUtil.getContext();
        if (ActivityCompat
                .checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE)
                == PackageManager.PERMISSION_GRANTED) {

            final TelephonyManager tm = (TelephonyManager) context
                    .getSystemService(TELEPHONY_SERVICE);
            imei1 = tm.getDeviceId(0);
            imei2 = tm.getDeviceId(1);
        }
        if (slot == 0) {
            return imei1;
        }
        if (slot == 1) {
            return imei2;
        }
        return imei1;

    }

    @SuppressLint("MissingPermission")
    private static String getImei(int slot) {
        String imei1 = "";
        String imei2 = "";
        Context context = NetUtil.getContext();
        Map<String, String> map = new HashMap<String, String>();
        TelephonyManager tm = (TelephonyManager) context
                .getSystemService(TELEPHONY_SERVICE);
        imei1 = tm.getDeviceId();
        Class<?> clazz = null;
        Method method = null;//(int slotId)
        try {

            clazz = Class.forName("android.os.SystemProperties");
            method = clazz.getMethod("get", String.class, String.class);
            String gsm = (String) method.invoke(tm, "ril.gsm.imei", "");
            Log.d("TAG", "" + gsm);
            if (!TextUtils.isEmpty(gsm)) {
                //the value of gsm like:xxxxxx,xxxxxx
                String imeiArray[] = gsm.split(",");
                if (imeiArray.length > 0) {
                    imei1 = imeiArray[0];
                    if (imeiArray.length > 1) {
                        imei2 = imeiArray[1];
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (slot == 0) {
            return imei1;
        }
        if (slot == 1) {
            return imei2;
        }
        return imei1;

    }

    public static String getSerial() {
        try {
            return Build.class.getField("SERIAL").get(null).toString();
        } catch (Exception e) {
            return "";
        }
    }

    public static String getAndroidId() {
        try {
            String id = Settings.Secure.getString(NetUtil.getContext().getContentResolver(), Settings.Secure.ANDROID_ID);
            return id;
        }catch (Exception e){
            e.printStackTrace();
            return UUID.randomUUID().toString();
        }


    }

    public static String getPsuedoID() {
        return Build.BOARD
                + Build.BRAND
                + Build.CPU_ABI
                + Build.DEVICE
                + Build.MANUFACTURER
                + Build.MODEL
                + Build.PRODUCT;
    }

    /**
     * Return pseudo unique ID
     * @return ID
     */
    public static String getUniquePsuedoID() {
        String id1 = "";
        try {
            id1 = getSerial();
        } catch (Exception exception) {
            //do nothing
        }
        String id2 = Settings.Secure.getString(NetUtil.getContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        if (id2 == null) id2 = "";
        String id3 = getPsuedoID();
        String id = id1 + id2 + id3;
        return new UUID(id.hashCode(), id3.hashCode()).toString();
    }

    /**
     * 生成Akulaku唯一标识符 (待数据服务器兼容处理后更改)
     * @param str AndroidId+SerialNo+PsuedoId
     * @return (e.g ee34d343-0448-2472-3336-5a680f096744)
     * @throws NoSuchAlgorithmException 环境不支持MD5
     */
    private static String generateAlId(String str)
            throws NoSuchAlgorithmException {
        byte[] digest = MessageDigest.getInstance("MD5").digest((str).getBytes(
                Charset.forName("UTF-8")));
        StringBuilder hexString = new StringBuilder();
        for (int i = 0; i < digest.length; i++) {
            hexString.append(Integer.toHexString(
                    (digest[i] & 0x000000FF) | 0xFFFFFF00).substring(6));
            if(i==3||i==5||i==7||i==9)hexString.append("-");
        }
        return hexString.toString();
    }

    /**
     * 延时
     * @param time
     */
    public static void sleep(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取当前进程名
     * @return
     */
    public static String getProcessName() {
        int pid = android.os.Process.myPid();
        String processName = "";
        ActivityManager manager = (ActivityManager)NetUtil.getContext().getSystemService(Context.ACTIVITY_SERVICE);
        if (manager == null) {
            return processName;
        }
        for (ActivityManager.RunningAppProcessInfo process: manager.getRunningAppProcesses()) {
            if(process.pid == pid) {
                processName = process.processName;
            }
        }
        return processName;
    }


}
