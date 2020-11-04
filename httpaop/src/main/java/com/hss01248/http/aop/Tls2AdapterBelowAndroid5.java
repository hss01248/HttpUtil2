package com.hss01248.http.aop;

import android.os.Build;
import android.util.Log;

import java.security.Provider;
import java.security.Security;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ConnectionSpec;
import okhttp3.OkHttpClient;
import okhttp3.TlsVersion;

public class Tls2AdapterBelowAndroid5 implements OkhttpAspect.OkhttpHook {
    @Override
    public void beforeBuild(OkHttpClient.Builder client) {
        try {
            Class ConscryptProvider = Class.forName("org.conscrypt.OpenSSLProvider");
            Security.insertProviderAt((Provider) ConscryptProvider.newInstance(), 1);
        } catch (Exception e) {
             enableTls12OnPreLollipop(client);
             Log.d(OkhttpAspect.TAG,"enable tls1.2 below android 5 :"+Build.VERSION.SDK_INT+","+client.toString());
        }
    }

    public static OkHttpClient.Builder enableTls12OnPreLollipop(OkHttpClient.Builder client) {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
            try {
                client.sslSocketFactory(new TLSSocketFactory());

                ConnectionSpec cs =
                        new ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
                                .tlsVersions(TlsVersion.TLS_1_2)
                                .build();

                List<ConnectionSpec> specs = new ArrayList<>();
                specs.add(cs);
                specs.add(ConnectionSpec.COMPATIBLE_TLS);
                specs.add(ConnectionSpec.CLEARTEXT);
                client.connectionSpecs(specs);
            } catch (Exception exc) {
                Log.e("OkHttpClientProvider", "Error while enabling TLS 1.2", exc);
            }
        }

        return client;
    }
}
