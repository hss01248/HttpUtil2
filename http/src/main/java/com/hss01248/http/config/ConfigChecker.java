package com.hss01248.http.config;

import android.text.TextUtils;

import com.blankj.utilcode.util.LogUtils;
import com.hss01248.http.ConfigInfo;
import com.hss01248.http.GlobalConfig;
import com.hss01248.http.Tool;

import java.util.Iterator;
import java.util.Map;

/**
 * Created by hss on 2018/7/22.
 */

public class ConfigChecker {

    /**
     *
     * @param config
     * @return 返回检查不通过的描述,如果返回"",则代表检查通过
     */
    public static String check(ConfigInfo config){

        Map<String,Object> params = config.getParams();
        if(params == null || params.isEmpty()){
            return "";
        }
        Iterator<Map.Entry<String, Object>> iterator = params.entrySet().iterator();
        while (iterator.hasNext()){
            Map.Entry<String, Object> next = iterator.next();
            if(next.getValue() ==null){
                iterator.remove();
                LogUtils.w("remove the key when value is null : "+next.getKey());
            }
        }
        return "";
    }

    /**
     * 将paramsStr解析并放入params的Map中
     * @param info
     * @param <T>
     */
    public static <T> void convertParams(ConfigInfo<T> info) {

        //添加公共参数
        if(info.isAppendCommonParams()){
            Map<String, Object> commonParams = GlobalConfig.get().getCommonParams();
            if(commonParams != null && !commonParams.isEmpty()){
                for (Map.Entry<String,Object> entry : commonParams.entrySet()) {
                    info.addParamOptional(entry.getKey(),entry.getValue());
                }
            }
        }

        if(info.isAppendCommonHeaders()){
            Map<String, String> commonParams = GlobalConfig.get().getCommonHeaders();
            if(commonParams != null && !commonParams.isEmpty()){
                for (Map.Entry<String,String> entry : commonParams.entrySet()) {
                    info.addHeader(entry.getKey(),entry.getValue());
                }
            }
        }


        if(TextUtils.isEmpty(info.getParamsStr())){
            return;
        }
        //key=value&xxx形式
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
        //json 在真正执行请求时直接发送,这里不再拆解
    }
}
