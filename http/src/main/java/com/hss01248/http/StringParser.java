package com.hss01248.http;

import android.text.TextUtils;

import com.hss01248.http.exceptions.DataCodeMsgCodeErrorException;
import com.hss01248.http.exceptions.ResponseStrEmptyException;
import com.hss01248.http.response.ResponseBean;

import org.json.JSONObject;

/**
 * Created by hss on 2018/7/24.
 */

public class StringParser {


    public static <T> ResponseBean<T> parseString(String str, ConfigInfo<T> info, boolean fromCache) throws Exception {
        //MyLog.json(str);
        info.responseBodyStr = str;

        ResponseBean<T> response = new ResponseBean<>();
        response.bodyStr = str;
        response.isFromCache = fromCache;
        //互相引用,可能导致json打印栈溢出.需要重新tostring方法或者打印方法
        response.setInfo(info);
        //info.response = response;


        //字符串
        if (info.responseAsString) {
            if (judgeIsResponseStrEmpty(str)) {
                if (!info.isTreatEmptyDataAsSuccess()) {
                    throw new ResponseStrEmptyException(info, "content of data is empty");//在回调里当做onempty处理
                }else {
                    return response;
                }
            }
            response.data = (T) str;
            return response;
        }

        //普通json
        if (info.isResponseAsNormalJson()) {
            processDataStr(str, response, info);
            return response;
        }

        //data-code-msg格式json
        if (info.isResponseAsDataCodeMsgInJson()) {

            JSONObject object = new JSONObject(str);
            response.json = object;
            String data = object.optString(info.getDataCodeMsgJsonConfig().key_data);
            response.dataStr = data;

            //判断成功或者失败
            boolean isResponseSuccess = info.getDataCodeMsgJsonConfig().successJudge.isResponseSuccess(object);
            if (!isResponseSuccess) {
                //将错误携带并抛出去
                throw new DataCodeMsgCodeErrorException("code fail", info, response);
            }

            //处理data的转换
            processDataStr(data, response, info);

            return response;

        }
        /*JSONObject object = new JSONObject(str);
        //String data = object.optString("data");//测试解析json
        String data = object.getJSONObject("data").optString("menuList");//测试解析jsonarr
        T t = JSON.parseObject(data, new TypeReference<T>() {});*/
        return null;
    }

    public static <T> void processDataStr(String data, ResponseBean<T> response, ConfigInfo<T> info) throws Exception {
        if (judgeIsResponseStrEmpty(data)) {
            if (!info.isTreatEmptyDataAsSuccess()) {
                throw new ResponseStrEmptyException(info, "content of data is empty");//在回调里当做onempty处理
            }
            if (info.isResponseAsJsonArray()) {
                data = "[]";
            } else {
                data = "{}";
            }
        }

           /*
           一样会有泛型擦除

            TypeReference<T> typeReference = new TypeReference<T>(){};
            T t = JSON.parseObject(data, typeReference);

            GlobalConfig.get().getTool().logi("type of TypeReference:"+t.getClass());*/


        boolean isResponseJsonArr = false;
        if (data.startsWith("[")) {
            isResponseJsonArr = true;
        }


        Object t2 = null;
        if (isResponseJsonArr) {
            t2 = GlobalConfig.get().getTool().parseArray(data, info.getClazz());
        } else {
            t2 = GlobalConfig.get().getTool().parseObject(data, info.getClazz());
        }

        //GlobalConfig.get().getTool().logi("type of response.data:" + t2.getClass());

        response.data = (T) t2;
    }


    private static boolean judgeIsResponseStrEmpty(String data) {
        if (TextUtils.isEmpty(data)) {
            return true;
        }
        if ("null".equals(data)) {
            return true;
        }
        if (" ".equals(data)) {
            return true;
        }
        if ("{}".equals(data)) {
            return true;
        }
        if ("[]".equals(data)) {
            return true;
        }
        return false;
    }
}
