package com.silvrr.akuhttp;

import android.app.Activity;
import android.app.Application;

import com.hss01248.http.ConfigInfo;
import com.hss01248.http.GlobalConfig;
import com.hss01248.http.HttpUtil;
import com.hss01248.http.INetTool;
import com.hss01248.http.cache.CacheMode;
import com.hss01248.http.callback.ProgressCallback;
import com.hss01248.http.config.DataCodeMsgJsonConfig;
import com.hss01248.http.config.FileDownlodConfig;
import com.hss01248.http.response.ResponseBean;
import com.silvrr.testtool.TestTool;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.MyToast;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Function;
import io.silvrr.installment.common.utils.IJsonToStr;
import io.silvrr.installment.common.utils.XLogUtil;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

/**
 * Created by hss on 2018/7/29.
 */

public class NetUtil {

    static String baseUrl;
    static String riskBaseUrl;
    static  String getUploadTokenPath;
     static final String IMAGE_JPEG = "image/jpeg";// 图片类型
    static Application context;

    public static Application getContext() {
        return context;
    }

    public static void init(Application application, boolean showLog, String baseUrl, String riskBaseUrl,
                            String getUploadTokenPath){
        NetUtil.riskBaseUrl = riskBaseUrl;
        context = application;
        ActivityStackManager.init(application);
        NetUtil.getUploadTokenPath = getUploadTokenPath;
        try {
            TestTool.init(application,showLog,null);
            XLogUtil.init(showLog, "http", new IJsonToStr() {
                @Override
                public String toStr(Object obj) {
                    return MyJson.toJsonStr(obj);
                }
            });
            MyToast.init(application,showLog,false);

            initHttp(application,showLog,baseUrl);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void initHttp(Application baseApp,boolean showLog,String baseUrl) throws Exception{
        INetTool tool = new INetTool() {
            @Override
            public String toJsonStr(Object obj) {
                return MyJson.toJsonStr(obj);
            }

            @Override
            public <T> T parseObject(String str, Class<T> clazz) {
                return MyJson.parseObject(str,clazz);
            }

            @Override
            public <E> List<E> parseArray(String str, Class<E> clazz) {
                return MyJson.parseArray(str,clazz);
            }

            @Override
            public void logi(String str) {
                XLogUtil.i(str);
            }

            @Override
            public void logd(String str) {
                XLogUtil.d(str);
            }

            @Override
            public void logw(String str) {
                XLogUtil.w(str);
            }

            @Override
            public void logdJson(String json) {
                XLogUtil.json(json);
            }

            @Override
            public void initialStetho(Application application) {

            }

            @Override
            public void addChuckInterceptor(OkHttpClient.Builder builder) {
                TestTool.addChuckInterceptorForOkhttp(builder);
            }

            @Override
            public void addStethoInterceptor(OkHttpClient.Builder builder) {
                TestTool.addStethoInterceptorForOkhttp(builder);
            }

            @Override
            public void addHttpLogInterceptor(OkHttpClient.Builder builder) {
                builder.addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY));
            }

            @Override
            public Activity getTopActivity() {
                return ActivityStackManager.getInstance().getTopActivity();
            }

            @Override
            public void logObj(Object t) {
                XLogUtil.obj(t);
            }
        };
        HttpUtil
                .init(baseApp,showLog,baseUrl,tool)
                .setDataCodeMsgJsonConfig(DataCodeMsgJsonConfig
                        .newBuilder()
                        .key_data("data")
                        .key_code("errCode")
                        .key_msg("errMsg")
                        .key_extra1("sysTime")
                        .key_extra2("sysMsg")
                        .isCodeAsString(true)
                        .successJudge(new DataCodeMsgJsonConfig.DataSuccessJudge() {
                            @Override
                            public boolean isResponseSuccess(JSONObject object) {
                                return object.optBoolean("success");
                            }
                        })

                        .build())
                // .addCrtificateRaw(R.raw.srca)
                //.addCrtificateAssert("srca.cer")
                .setLogTag("okhttp")
                .setCacheMode(CacheMode.NO_CACHE)
                .setCookieMode(GlobalConfig.COOKIE_DISK)
                .setDefaultUserAgent(System.getProperty("http.agent"))
                .setIgnoreCertificateVerify(true)
                //.setReadTimeout(15000)
                .setConnectTimeout(12000)
                //.setWriteTimeout(10000)
                .setTotalTimeOut(15000)
                .setRetryCount(0)
                .setRetryOnConnectionFailure(false)
                .addCommonHeader("dt","4")//android
                .addCommonHeader("cv",baseApp.getPackageManager().getPackageInfo(baseApp.getPackageName(),0).versionCode+"");
    }

    public static <T> ConfigInfo<List<T>> postToRiskAsJsonArray(String url, Class<T> clazz){
        if(!url.startsWith("http")){
            url = riskBaseUrl + url;
        }
        return HttpUtil.requestAsJsonArray(url,clazz).post();
    }



    public static <T> ConfigInfo<T> postToRisk(String url,Class<T> clazz){
        if(!url.startsWith("http")){
            url = riskBaseUrl + url;
        }
        return HttpUtil.request(url,clazz).post();
    }

    public static <T> ConfigInfo<T> request(String url, Class<T> clazz) {
        return HttpUtil.request(url,clazz);
    }

    public static ConfigInfo<String> requestString(String url) {
        return HttpUtil.requestString(url);
    }

    public static <T> ConfigInfo<List<T>> requestAsJsonArray(String url, Class<T> clazz) {
        return HttpUtil.requestAsJsonArray(url,clazz);
    }

    public static ConfigInfo<FileDownlodConfig> download(String url) {
        return HttpUtil.download(url);
    }

    public static io.reactivex.Observable<ResponseBean<S3Info>> uploadSingleImg(String type, final String filePath, ProgressCallback progressCallback){
        String imageType = IMAGE_JPEG;
        //todo 预先压缩
       return HttpUtil.requestAsJsonArray(getUploadTokenPath,S3Info.class)
                .get()
                .addParam("type", type)
                .addParam("contentType", IMAGE_JPEG)
                .addParam("cnt","1")
                .asObservable()
                .flatMap(new Function<ResponseBean<List<S3Info>>, ObservableSource<ResponseBean<S3Info>>>() {
                    @Override
                    public ObservableSource<ResponseBean<S3Info>> apply(ResponseBean<List<S3Info>> bean) throws Exception {

                        S3Info info = bean.bean.get(0);
                        //这个S3Info怎么传递出去?
                        return HttpUtil.request(info.getUrl(),S3Info.class)
                                .setExtraFromOut(info)
                                .uploadBinary(filePath)
                                .put()
                                .setProgressCallback(progressCallback)
                                .treatEmptyDataAsSuccess()
                                .responseAsString()
                                .asObservable();
                    }
                });
    }

    public static io.reactivex.Observable<ResponseBean<S3Info>> uploadImgs(String type, final List<String> filePaths){
        final List<S3Info> infos = new ArrayList<>();
        io.reactivex.Observable<ResponseBean<S3Info>> observable =
                HttpUtil.requestAsJsonArray(getUploadTokenPath,S3Info.class)
                .get()
                .addParam("type", type)
                .addParam("contentType", IMAGE_JPEG)
                .addParam("cnt",filePaths.size())
                .asObservable()
                .flatMap(new Function<ResponseBean<List<S3Info>>, ObservableSource<ResponseBean<S3Info>>>() {
                    @Override
                    public ObservableSource<ResponseBean<S3Info>> apply(ResponseBean<List<S3Info>> bean) throws Exception {

                        infos.addAll(bean.bean);
                        List<io.reactivex.ObservableSource<ResponseBean<S3Info>>> observables = new ArrayList<>();
                        for(int i = 0; i< bean.bean.size(); i++){
                            S3Info info = bean.bean.get(i);
                            String filePath = filePaths.get(i);
                            io.reactivex.Observable<ResponseBean<S3Info>> observable =
                                    HttpUtil.request(info.getUrl(),S3Info.class)
                                            .uploadBinary(filePath)
                                            .put()
                                            .setExtraFromOut(info)
                                            .responseAsString()
                                            .treatEmptyDataAsSuccess()
                                            .asObservable();
                            observables.add(observable);
                        }
                        return io.reactivex.Observable.merge(observables);
                    }
                });

        return observable;
    }


}
