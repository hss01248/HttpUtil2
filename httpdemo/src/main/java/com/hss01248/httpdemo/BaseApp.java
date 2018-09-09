package com.hss01248.httpdemo;

import android.app.Activity;
import android.app.Application;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;

import com.hss01248.http.GlobalConfig;
import com.hss01248.http.HttpUtil;
import com.hss01248.http.INetTool;
import com.hss01248.http.cache.CacheMode;
import com.hss01248.http.config.DataCodeMsgJsonConfig;
import com.hss01248.http.config.LoadingDialogConfig;
import com.hss01248.testtool.TestTool;
import com.orhanobut.logger.IJsonToStr;
import com.orhanobut.logger.MyLog;

import org.json.JSONObject;

import java.util.List;

import es.dmoral.toasty.MyToast;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

/**
 * Created by hss on 2018/7/28.
 */

public class BaseApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();


        try {
            initTestTool();
            initHttp(this);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void initTestTool() {
        TestTool.init(this,true,false);
        MyLog.init(true, "httpdemo",2, new IJsonToStr() {
            @Override
            public String toStr(Object o) {
                return MyJson.toJsonStr(o);
            }
        });
        MyToast.init(this,true,false);
    }

    private void initHttp(BaseApp baseApp) throws PackageManager.NameNotFoundException {

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
                MyLog.i(str);
            }

            @Override
            public void logd(String str) {
                MyLog.d(str);
            }

            @Override
            public void logw(String str) {
                MyLog.w(str);
            }

            @Override
            public void logdJson(String json) {
                MyLog.json(json);
            }

            @Override
            public void initialStetho(Application application) {

            }

            @Override
            public void addChuckInterceptor(OkHttpClient.Builder builder) {
                //TestTool.addChuckInterceptorForOkhttp(builder);
            }

            @Override
            public void addStethoInterceptor(OkHttpClient.Builder builder) {
                //TestTool.addStethoInterceptorForOkhttp(builder);
            }

            @Override
            public void addHttpLogInterceptor(OkHttpClient.Builder builder) {
                builder.addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY));
            }

            @Override
            public Activity getTopActivity() {
                return null;
            }

            @Override
            public void logObj(Object t) {
                MyLog.obj(t);
            }
        };


        HttpUtil.init(this,true,"http://api.qxinli.com:9005/api/",tool)
                .setDataCodeMsgJsonConfig(DataCodeMsgJsonConfig
                        .newBuilder()
                        .key_data("data")
                        .key_code("code")
                        .key_msg("message")
                        .successJudge(new DataCodeMsgJsonConfig.DataSuccessJudge() {
                            @Override
                            public boolean isResponseSuccess(JSONObject object) {
                                int code = object.optInt("code");
                                return code==0;
                            }
                        })

                        .build())
                .setDefaultLoadingDialog(new LoadingDialogConfig.ILoadingDialog() {
                    @Override
                    public Dialog showLoadingDialog(Context context,String msg) {
                        ProgressDialog dialog =  new ProgressDialog(context);
                        dialog.setContentView(R.layout.toast_layout);
                        dialog.setMessage(msg);
                        dialog.show();
                        return dialog;
                    }
                })
                // .addCrtificateRaw(R.raw.srca)
                //.addCrtificateAssert("srca.cer")
                .setLogTag("okhttp")
                .setCacheMode(CacheMode.NO_CACHE)
                .setCookieMode(GlobalConfig.COOKIE_DISK)
                .setDefaultUserAgent(System.getProperty("http.agent"))
                .setIgnoreCertificateVerify(true)
                //.setReadTimeout(15000)
                .setConnectTimeout(10000)
                //.setWriteTimeout(10000)
                .setTotalTimeOut(15000)
                .setRetryCount(0)
                .setRetryOnConnectionFailure(false)
                .addCommonHeader("clienttype","android");
    }
}
