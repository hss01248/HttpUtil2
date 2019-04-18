package com.silvrr.akuhttp.other;

import com.hss01248.http.callback.MyNetCallback;
import com.hss01248.http.response.ResponseBean;
import com.orhanobut.logger.MyLog;
import com.silvrr.akuhttp.NetUtil;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by hss on 2018/8/18.
 */

public class LoginModel {

    public static LoginInfo loginInfo;


    public static void login(){

        NetUtil.request(HttpReqUrl.LOGIN_URL,LoginInfo.class)
                .post()
                .addParam("areaId", "1")
                .addParam("countryId", "1")
                .addParam("phoneNumber", "0811111111")
                .addParam("password", encrypt("12345678","MD5"))
                .addParam("appVersion", "2.4.8")
                .addParam("deviceType", SystemUtils.getDeviceType())
                .addParam("osVersion", SystemUtils.getSystemVersion())
                .addParam("deviceToken", "")
                .addParam("deviceId", SystemUtils.getUniquePsuedoID())
                .addParam("deviceModel", SystemUtils.getPhoneModel())
                .callback(new MyNetCallback<ResponseBean<LoginInfo>>(true,null) {
                    @Override
                    public void onSuccess(ResponseBean<LoginInfo> response) {
                        loginInfo = response.bean;
                    }

                    @Override
                    public void onError(String msgCanShow) {
                        MyLog.e(msgCanShow);

                    }
                });

    }

    public static String encrypt(String strSrc, String encName) {
        MessageDigest md;
        String strDes;

        try {
            byte[] bt = strSrc.getBytes();
            if (encName == null || encName.equals("")) {
                encName = "SHA-256";
            }
            md = MessageDigest.getInstance(encName);
            md.update(bt);
            // to HexString
            strDes = bytes2Hex(md.digest());
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
        return strDes;
    }

    public static String bytes2Hex(byte[] bts) {
        StringBuilder hex = new StringBuilder(bts.length * 2);
        for (byte b : bts) {
            if ((b & 0xFF) < 0x10) {
                hex.append("0");
            }
            hex.append(Integer.toHexString(b & 0xFF));
        }

        return hex.toString();
    }
}
