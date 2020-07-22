package com.hss01248.http.response;

import com.hss01248.http.ConfigInfo;

import org.json.JSONObject;

import java.util.Map;

/**
 * Created by huangshuisheng on 2018/7/25.
 *
 * 序列化时,只序列化基础数据: url,headers,body,其他均不序列化
 */

public class ResponseBean<T> {

    public transient boolean isFromCache;

    public void setInfo(ConfigInfo info) {
        this.info = info;
        this.extraFromOut = info.getExtraFromOut();
    }

    public ConfigInfo getInfo() {
        return info;
    }

    private transient ConfigInfo info;

    public transient Object extraFromOut;//外面传递进来,一直沿着rx链一直传递下去的

    public String url;
    public String bodyStr;//responsebody
    public Map<String, String> headers;

    public transient T data;//解析得到的有效javabean
    public boolean success;
    public String code;
    public String msg;

    //public Map<String,Object> bodyMap;//仅适用于data-code-msg
    public transient JSONObject json;//仅适用于data-code-msg
    public transient String dataStr;//仅适用于data-code-msg结构

    public transient Throwable errorInfo;


    public static <T> ResponseBean<T> success(T data){
        ResponseBean<T> bean = new ResponseBean<>();
        bean.data = data;
        bean.success = true;
        return bean;
    }

    public static <T> ResponseBean<T> error(String code,String msg){
        ResponseBean<T> bean = new ResponseBean<>();
        bean.code = code;
        bean.msg = msg;
        return bean;
    }
    public static <T> ResponseBean<T> error(Throwable throwable){
        ResponseBean<T> bean = new ResponseBean<>();
        bean.errorInfo = throwable;
        return bean;
    }


}
