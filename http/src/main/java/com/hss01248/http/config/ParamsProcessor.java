package com.hss01248.http.config;

import android.text.TextUtils;

import com.hss01248.http.ConfigInfo;
import com.hss01248.http.GlobalConfig;
import com.hss01248.http.utils.CollectionUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

/**
 * Created by hss on 2018/10/2.
 */

public class ParamsProcessor {

    public static String getFinalJsonStr(ConfigInfo info)  {
        if(!info.isParamsAsJson()){
            return "";
        }

        if(!TextUtils.isEmpty(info.getParamsStr())){
            if(info.getParamsStr().startsWith("[")){
                //json array,无法将map放入array
                return info.getParamsStr();
            }else if(info.getParamsStr().startsWith("{")){
                try {
                    String s =  getJsonStr(info);
                    return s;
                } catch (Exception e) {
                    e.printStackTrace();
                    return GlobalConfig.get().getTool().toJsonStr(info.getParams());
                }
            }else {
                return GlobalConfig.get().getTool().toJsonStr(info.getParams());
            }
        }else {
            return GlobalConfig.get().getTool().toJsonStr(info.getParams());
        }
    }

    private static String getJsonStr(ConfigInfo info) throws JSONException {
        if(info.getParams() == null || info.getParams().isEmpty()){
            return info.getParamsStr();
        }
        JSONObject object = new JSONObject(info.getParamsStr());
        CollectionUtil.forEach(info.getParams(), new CollectionUtil.EveryMap<String, Object>() {
            @Override
            public void item(Map.Entry<String, Object> entry) {
                try {
                    //params里的key的优先级高于info.getParamsStr()里的,如果相同,则覆盖.
                    object.put(entry.getKey(),entry.getValue());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        return object.toString();
    }


}
