package com.hss01248.http.config;

import android.text.TextUtils;

import com.hss01248.http.ConfigInfo;
import com.hss01248.http.Tool;

/**
 * Created by hss on 2018/7/22.
 */

public class ConfigChecker {

    /**
     *
     * @param info
     * @return 返回检查不通过的描述,如果返回"",则代表检查通过
     */
    public static String check(ConfigInfo info){



        return "";
    }

    /**
     * 将paramsStr解析并放入params的Map中
     * @param info
     * @param <T>
     */
    public static <T> void convertParams(ConfigInfo<T> info) {
        if(TextUtils.isEmpty(info.getParamsStr())){
            return;
        }
        String[] strs = info.getParamsStr().split("&");
        for (String str : strs){
            if(TextUtils.isEmpty(str)){
                continue;
            }
            String[] keyValue = str.split("=");
            if(keyValue.length != 2){
                Tool.logw("str contains more than one = :"+str);
                continue;
            }
            info.addParam(keyValue[0],keyValue[1]);
        }
    }
}
