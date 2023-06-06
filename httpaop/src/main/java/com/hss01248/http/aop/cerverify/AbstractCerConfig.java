package com.hss01248.http.aop.cerverify;


import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


import org.json.JSONObject;

import java.security.MessageDigest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public abstract class AbstractCerConfig implements IGetCerConfigRequest {

    static String TAG = "cercheck";
    @Override
    public Map<String, List<String>> requestConfig() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(20, TimeUnit.SECONDS);
        builder.readTimeout(20, TimeUnit.SECONDS);
        builder.writeTimeout(20, TimeUnit.SECONDS).retryOnConnectionFailure(true);
        configRequestBuilder(builder);
        OkHttpClient okHttpClient = builder.build();

        try {
           Response response =  okHttpClient.newCall(buildRequest()).execute();
           if(response.isSuccessful()){
               String json = response.body().string();
               HostNameCerChecker.d(TAG,"http success:"+json);
               boolean same =  checkSingnature(response,json);
               HostNameCerChecker.d(TAG,"http response sha1比对是否一致? "+same);
               if(!same){
                    reportException(new ResponseModifiedException(json));
                   return null;
               }

               //HostNameCerChecker.d("json",json);
               if(!TextUtils.isEmpty(json)){
                   JSONObject jsonObject = new JSONObject(json);
                   if(isResponseBodySuccess(jsonObject)){
                       Map<String, List<String>> map = new HashMap<>();
                       String data = getDataStr(jsonObject);
                       if(!TextUtils.isEmpty(data)){
                           List<CerConfig> cerConfigs = new Gson().fromJson(data,new TypeToken<List<CerConfig>>(){}.getType());
                           if(cerConfigs !=  null && !cerConfigs.isEmpty()){
                               for (CerConfig cerConfig : cerConfigs) {
                                   map.put(cerConfig.host,cerConfig.cetificatePrints);
                               }
                           }
                           HostNameCerChecker.d(TAG,"http success map:\n"+map);
                           return map;
                       }
                   }
               }

           }else {
              HostNameCerChecker.d(TAG,"http fail:"+response.code()+response.message());
           }

        } catch (Throwable e) {
            HostNameCerChecker.w("request",e.getMessage());
        }
        return null;
    }

    protected abstract void reportException(ResponseModifiedException e);

    /**
     * 响应成功时,校验响应体的指纹.不匹配,则此请求无效.最终返回空.
     * @param response 用于获取header里的校验字段
     * @param responseBodyStr
     * @return
     */
    protected    boolean checkSingnature(Response response,String responseBodyStr){
        //校验响应的sha1值:校验不通过,就返回
        String fp =  response.header("X-Response-FP");
        if(TextUtils.isEmpty(fp)){
            HostNameCerChecker.w(TAG,"X-Response-FP 为空,未开启响应签名,或者签名为空串");
            return true;
        }
        if(TextUtils.isEmpty(responseBodyStr)){
            return true;
        }
        //responseBodyStr = JsonSortUtil.sortJson(responseBodyStr);
        if(fp.equals(sha1(responseBodyStr))){
            return true;
        }
        return false;

    }

    /**
     * 对请求配置接口的okhttpclient进行配置
     * @param builder
     */
    protected  void configRequestBuilder(OkHttpClient.Builder builder){}

    /**
     *  Request request = new Request.Builder()
     *                 .url(getCerConfigUrl())
     *                 .build();
     * @return
     */
    protected abstract Request buildRequest();

    private String getDataStr(JSONObject jsonObject) {
        return jsonObject.optString("data");
    }

    protected boolean isResponseBodySuccess(JSONObject jsonObject) {
        return jsonObject.optBoolean("success");
    }



    private static final char[] HEX = {'0', '1', '2', '3', '4', '5',
            '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    private static String getFormattedText(byte[] bytes) {
        int len = bytes.length;
        StringBuilder buf = new StringBuilder(len * 2);
        // 把密文转换成十六进制的字符串形式
        for (int j = 0; j < len; j++) {
            buf.append(HEX[(bytes[j] >> 4) & 0x0f]);
            buf.append(HEX[bytes[j] & 0x0f]);
        }
        return buf.toString();
    }
     static String sha1(String str) {
        if (str == null) {
            return null;
        }
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA1");
            messageDigest.update(str.getBytes());
            return getFormattedText(messageDigest.digest());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


}
