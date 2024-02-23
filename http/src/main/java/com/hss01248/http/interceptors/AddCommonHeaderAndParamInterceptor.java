package com.hss01248.http.interceptors;

import android.text.TextUtils;
import android.webkit.MimeTypeMap;

import com.hss01248.okhttpinterceptors.BaseOkhttpInterceptor;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.Buffer;

/**
 * 参考  https://github.com/jkyeo/okhttp-basicparamsinterceptor/blob/master/basicparamsinterceptor/src/main/java/com/jkyeo/basicparamsinterceptor/BasicParamsInterceptor.java
 */
public class AddCommonHeaderAndParamInterceptor extends BaseOkhttpInterceptor {

    static ICommonMap headerMap;
    static ICommonMap paramMap;

    /**
     * 每一次都动态计算获取.
     * 如果要缓存,在接口里面自己做
     *
     * @param header
     * @param param
     */
    public static void init(ICommonMap header, ICommonMap param) {
        headerMap = header;
        paramMap = param;
    }


    @Override
    protected Response interceptReally(Chain chain) throws IOException {
        Request request = chain.request();
        Request.Builder builder = request.newBuilder();
        //请求头
        if (headerMap != null) {
            Map<String, String> map = headerMap.getMap();
            if (map != null) {
                for (Map.Entry<String, String> entry : map.entrySet()) {
                    builder.header(entry.getKey(), entry.getValue());
                }
            }
        }

        if (paramMap != null) {
            Map<String, String> map = paramMap.getMap();
            if (map != null && !map.isEmpty()) {
                if ("GET".equals(request.method())) {
                    //get请求参数
                    HttpUrl url = request.url();
                    HttpUrl.Builder builder1 = url.newBuilder();
                    for (Map.Entry<String, String> entry : map.entrySet()) {
                        builder1.addEncodedQueryParameter(URLEncoder.encode(entry.getKey()), URLEncoder.encode(entry.getValue()));
                    }
                    builder.url(builder1.build());
                }

                String type = request.header("Content-Type");
                if (TextUtils.isEmpty(type)) {
                    type = request.header("content-type");
                }
                boolean isJson = !TextUtils.isEmpty(type) && type.contains("json");

                RequestBody requestBody = request.body();
                //请求体:
                if (request.body() instanceof FormBody) {
                    // 构造新的请求表单
                    FormBody.Builder builder2 = new FormBody.Builder();

                    FormBody body = (FormBody) request.body();

                    //将以前的参数添加
                    for (int i = 0; i < body.size(); i++) {
                        builder2.add(body.encodedName(i), body.encodedValue(i));
                    }
                    //追加新的参数
                    for (Map.Entry<String, String> entry : map.entrySet()) {
                        builder2.add(entry.getKey(), entry.getValue());
                    }
                    requestBody = builder2.build();

                    //构造新的请求体
                } else if (request.body() instanceof MultipartBody) {
                    MultipartBody body = (MultipartBody) request.body();
                    //body.parts().add(MultipartBody.create())
                    MultipartBody.Builder builder1 = new MultipartBody.Builder();
                    List<MultipartBody.Part> parts = body.parts();
                    for (MultipartBody.Part part : parts) {
                        builder1.addPart(part);
                    }

                    for (Map.Entry<String, String> entry : map.entrySet()) {
                        builder1.addFormDataPart(entry.getKey(), entry.getValue());
                    }
                    requestBody = builder1.build();

                } else if (isJson) {
                    Buffer buffer = new Buffer();// 创建缓存
                    try {
                        request.body().writeTo(buffer);//将请求体内容,写入缓存
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    String json = buffer.readUtf8();// 读取参数字符串
                    //如果是json串就解析 从新加餐 如果是字符串就进行修改 具体业务逻辑自己加
                    try {
                        if (json.startsWith("{")) {
                            JSONObject jsonObject = new JSONObject(json);
                            for (Map.Entry<String, String> entry : map.entrySet()) {
                                jsonObject.put(entry.getKey(), entry.getValue());
                            }
                            json = jsonObject.toString();
                        } else if (json.startsWith("[")) {
                            JSONArray jsonArray = new JSONArray(json);
                            for (int i = 0; i < jsonArray.length(); i++) {
                                Object o = jsonArray.get(i);
                                if (o instanceof JSONObject) {
                                    JSONObject object = (JSONObject) o;
                                    for (Map.Entry<String, String> entry : map.entrySet()) {
                                        object.put(entry.getKey(), entry.getValue());
                                    }
                                }
                            }
                            json = jsonArray.toString();
                        }
                    } catch (Throwable throwable) {
                        throwable.printStackTrace();
                    }
                    //对应请求头大伙按照自己的传输方式 定义
                    requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json);
                }
                builder.method(request.method(), requestBody);
            }
        }


        return chain.proceed(builder.build());

    }

    public interface ICommonMap {
        Map<String, String> getMap();
    }
}
