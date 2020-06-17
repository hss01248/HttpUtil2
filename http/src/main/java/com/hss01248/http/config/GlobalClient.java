package com.hss01248.http.config;

import com.codahale.metrics.MetricRegistry;
import com.hss01248.gsonconverter.validator.GsonConverterFactoryWithBeanValidator;
import com.hss01248.http.ConfigInfo;
import com.hss01248.http.GlobalConfig;
import com.hss01248.http.HttpUtil;
import com.hss01248.http.request.ApiService;
import com.raskasa.metrics.okhttp.InstrumentedOkHttpClients;

import java.io.File;

import okhttp3.Cache;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

/**
 * Created by hss on 2018/7/26.
 */

public class GlobalClient {


    private static volatile GlobalClient mInstance;

    private GlobalClient() {
        buildByGlobalConfig(GlobalConfig.get());
    }



    public static GlobalClient getInstance() {
        if (mInstance == null) {
            synchronized (GlobalClient.class) {
                if (mInstance == null) {
                    mInstance = new GlobalClient();
                }
            }
        }
        return mInstance;
    }


    private OkHttpClient okHttpClient;
    private Retrofit retrofit;
    private ApiService apiService;


    public ApiService getApiService() {
        return apiService;
    }




    private void buildByGlobalConfig(GlobalConfig config) {

         okHttpClient = buildClient(config);
         retrofit = new Retrofit.Builder()
                 .baseUrl(config.getBaseUrl())
                 .client(okHttpClient)
                 .addConverterFactory(GsonConverterFactoryWithBeanValidator.create())
                 .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                 .build();
         apiService = retrofit.create(ApiService.class);
    }

    private OkHttpClient buildClient(GlobalConfig config) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();

        Configer.setGloablConfig(builder);

        //设置缓存文件夹
        File cacheFile = new File(HttpUtil.context.getCacheDir(), "okhttpcache");
        Cache cache = new Cache(cacheFile, 1024 * 1024 * 200); //100Mb


        //HttpsUtil.setHttps(builder);
        OkHttpClient client = builder
               /* .connectTimeout(GlobalConfig.get().getConnectTimeout(), TimeUnit.MILLISECONDS)
                .readTimeout(0, TimeUnit.MILLISECONDS)
                .writeTimeout(0, TimeUnit.MILLISECONDS)*/
                .cache(cache)
                .build();


        MetricRegistry registry = new MetricRegistry();

        OkHttpClient client2 = InstrumentedOkHttpClients.create(registry, client);
        return client2;
    }

    public static <T> ApiService getApiService(ConfigInfo<T> info){
        if(!differentWithGlobal(info)){
            return GlobalClient.getInstance().getApiService();
        }else {
            return getCustomApiService(info);
        }
    }

    private static boolean differentWithGlobal(ConfigInfo info){

        if(info.getCookieMode() >0 && info.getCookieMode()!= GlobalConfig.get().getCookieMode()){
            return true;
        }
        if(info.isIgnoreCer() != GlobalConfig.get().isIgnoreCertificateVerify() ){
            return true;
        }

        if(info.getTotalTimeOut() >0 && info.getTotalTimeOut() != GlobalConfig.get().getTotalTimeOut() ){
            return true;
        }

        if(info.getCacheMode() != GlobalConfig.get().getCacheMode()){
            return true;
        }
        if(info.isResponseAsJsonArray() != GlobalConfig.get().isRetryOnConnectionFailure()){
            return true;
        }

        return false;
    }


    private  static ApiService getCustomApiService(ConfigInfo info){
        OkHttpClient okHttpClient = buildCustomClient(info);
       Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(GlobalConfig.get().getBaseUrl())
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactoryWithBeanValidator.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
        ApiService  apiService = retrofit.create(ApiService.class);
        return apiService;
    }

    private static OkHttpClient buildCustomClient(ConfigInfo info) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();

        Configer.setCustomConfig(builder,info);

        //设置缓存文件夹
        File cacheFile = new File(HttpUtil.context.getCacheDir(), "okhttpcache");
        Cache cache = new Cache(cacheFile, 1024 * 1024 * 200); //100Mb


        //HttpsUtil.setHttps(builder);
        OkHttpClient client = builder
                /*.connectTimeout(info.totalTimeOut, TimeUnit.MILLISECONDS)
                .readTimeout(0, TimeUnit.MILLISECONDS)
                .writeTimeout(0, TimeUnit.MILLISECONDS)*/
                .cache(cache)
                .build();

        MetricRegistry registry = new MetricRegistry();

        OkHttpClient client2 = InstrumentedOkHttpClients.create(registry, client);
        return client2;
    }


}
