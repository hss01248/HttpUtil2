package com.hss01248.ssldeni;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import java.io.IOException;
import java.security.cert.Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLSession;
import javax.security.cert.X509Certificate;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.CertificatePinner;
import okhttp3.CipherSuite;
import okhttp3.ConnectionSpec;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.TlsVersion;
import okhttp3.logging.HttpLoggingInterceptor;
import okio.ByteString;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    /**
     * 没什么鸟用
     * @param view
     */
    public void sslcompact(View view) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        ArrayList<ConnectionSpec> specs = new ArrayList<>();
        specs.add(ConnectionSpec.CLEARTEXT);
        specs.add(ConnectionSpec.COMPATIBLE_TLS);
        specs.add(ConnectionSpec.RESTRICTED_TLS);
        specs.add(ConnectionSpec.MODERN_TLS);
        builder.connectionSpecs(specs)
        .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY));
        OkHttpClient client = builder.build();

        //github  https://myssl.com/github.com?domain=github.com&status=q
        //TLS 1.3	支持
        //TLS 1.2	支持
        //TLS 1.1	不支持
        String url = "https://github.com/";

        Request request = new Request.Builder()
                .url(url).get().build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

            }
        });
    }

    /**
     * 正确,没有兼容问题
     * @param view
     */
    public void sslcompact2(View view) {
        OkHttpClient.Builder okhttpBuilder = new OkHttpClient.Builder();
        okhttpBuilder.sslSocketFactory(new TLSCompactSocketFactory())
           .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY));

        okhttpBuilder.certificatePinner(new CertificatePinner.Builder()
                .add("*.github.com","公钥的sha256->base64字符串","证书链上一级的公钥的sha256->base64字符串")
                .build());

        okhttpBuilder.hostnameVerifier(new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                try {
                    Certificate[] certificates =  session.getPeerCertificates();
                    for (int i = 0; i < certificates.length; i++) {
                        //证书本身的指纹
                        ByteString.of(certificates[i].getEncoded()).sha256().hex();
                        //公钥的指纹,certificatePinner校验的就是这里
                        ByteString.of(certificates[i].getPublicKey().getEncoded()).sha256().hex();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return false;
            }
        });
        OkHttpClient client = okhttpBuilder.build();

        String url = "https://github.com/";
        Request request = new Request.Builder()
                .url(url).get().build();

        client.newCall(request).enqueue(new Callback() {
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