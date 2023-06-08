package com.hss01248.http.aop.cerverify;

import androidx.core.app.ActivityCompat;

import java.io.IOException;
import java.net.Proxy;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;

/**
 * @Despciption todo
 * @Author hss
 * @Date 08/06/2023 11:09
 * @Version 1.0
 */
public class SslCertInfoUtil {

    public static void fetchCertInfo(String url){
        if(!url.startsWith("http")){
            url = "https://"+url;
        }
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .proxy(Proxy.NO_PROXY)
                .addInterceptor(new HttpLoggingInterceptor()
                        .setLevel(HttpLoggingInterceptor.Level.BODY));
        builder.hostnameVerifier(new CertInfoInHostNameVerifier());
        OkHttpClient client = builder.build();
        client.newCall(new Request.Builder().url(url).get().build())
                .enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {

                    }
                });



    }
}
