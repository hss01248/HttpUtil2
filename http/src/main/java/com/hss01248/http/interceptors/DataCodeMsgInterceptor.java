package com.hss01248.http.interceptors;

import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.internal.http.RealResponseBody;
import okio.Okio;

public class DataCodeMsgInterceptor implements Interceptor {


    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        if(TextUtils.isEmpty(request.header("isDataCodeMsg"))){
            return chain.proceed(request);
        }
        String isSuccessKey =  request.header("isSuccessKey");
        String codeKey =  request.header("codeKey");
        String msgKey = request.header("msgKey");
        String dataKey = request.header("dataKey");
        Response response = chain.proceed(request);
        if(!response.isSuccessful()){
            return response;
        }
        String json = response.body().string();
        try {
            JSONObject object = new JSONObject(json);
            boolean isSuccess =  object.optBoolean(isSuccessKey);
           String code =  object.optString(codeKey);
           String msg = object.optString(msgKey);
           String data = object.optString(dataKey);
          return new  Response.Builder().headers(response.headers())
                  .code(200)
                  .message(msg)
                  .protocol(response.protocol())
                  .body(new RealResponseBody(response.header("Content-Type"), data.length(),
                          Okio.buffer(Okio.source(new ByteArrayInputStream(data.getBytes()))))).build();
        } catch (Exception e) {
            e.printStackTrace();
            return response;
        }

    }
}
