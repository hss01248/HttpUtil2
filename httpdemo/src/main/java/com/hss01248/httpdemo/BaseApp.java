package com.hss01248.httpdemo;

import android.app.Activity;
import android.app.Application;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.view.Gravity;
import android.widget.TextView;

import androidx.multidex.MultiDexApplication;

import com.akaita.java.rxjava2debug.RxJava2Debug;
import com.google.gson.Gson;
import com.hss01248.basecache.BaseCacher;
import com.hss01248.http.GlobalConfig;
import com.hss01248.http.HttpUtil;
import com.hss01248.http.INetTool;

import com.hss01248.http.cache.CacheMode;
import com.hss01248.http.config.DataCodeMsgJsonConfig;
import com.hss01248.http.config.LoadingDialogConfig;
import com.hss01248.http.https.SafetyUtil;
import com.hss01248.testtool.TestTool;
import com.orhanobut.logger.IJsonToStr;
import com.orhanobut.logger.MyLog;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.dmoral.toasty.MyToast;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

/**
 * Created by hss on 2018/7/28.
 */

public class BaseApp extends MultiDexApplication {
    @Override
    public void onCreate() {
        super.onCreate();


        try {
            BaseCacher.init(this);

            initTestTool();
            initHttp(this);
            ActivityStackManager.init(this);
            TestCache.getInstance().prefetch();
            //RxJava2Debug.enableRxJava2AssemblyTracking(new String[]{"com.hss01248.http","com.hss01248.httpdemo"});
//            HostNameCerChecker.enableLog = true;
//            HostNameCerChecker.init(this,new IGetCerConfigRequest() {
//                @Override
//                public Map<String, String> requestConfig() {
//                    try {
//                        Thread.sleep(2000);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                    Map<String, String> map = new HashMap<>();
//                    map.put("zhihu.com","efaa8a257e9608d49058abe7525504eb05de1ce26229fa20de6b88434e6dffb6");
//                    return map;
//                }
//
//                @Override
//                public Map<String, String> defaultConfig() {
//                    Map<String, String> map = new HashMap<>();
//                    map.put("zhihu.com","efaa8a257e9608d49058abe7525504eb05de1ce26229fa20de6b88434e6dffb6-");
//                    return map;
//                }
//            });


        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void initTestTool() {
        TestTool.init(this,true,false);
        MyLog.init(true, "httpdemo",3, new IJsonToStr() {
            @Override
            public String toStr(Object o) {
                return new Gson().toJson(o);
            }
        });
        MyToast.init(this,true,false);
    }

    private void initHttp(BaseApp baseApp) throws PackageManager.NameNotFoundException {

        INetTool tool = new INetTool() {
            @Override
            public String toJsonStr(Object obj) {
                return new Gson().toJson(obj);
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
                return ActivityStackManager.getInstance().getTopActivity();
            }

            @Override
            public void logObj(Object t) {
                MyLog.obj(t);
            }

            @Override
            public void reportError(String code, String msg, String url) {

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
                    public Dialog buildLoadingDialog(Context context, String msg) {
                        ProgressDialog dialog =  new ProgressDialog(ActivityStackManager.getInstance().getTopActivity());
                        //dialog.setContentView(R.layout.toast_layout);
                        dialog.getWindow().setGravity(Gravity.CENTER);
                        dialog.setMessage(msg);
                        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                            @Override
                            public void onShow(DialogInterface dialog0) {

                                dialog.setContentView(R.layout.dialog_loading);
                               TextView textView =  dialog.getWindow().getDecorView().findViewById(R.id.toast_text);
                               textView.setText("加载中....");
                            }
                        });
                        return dialog;
                    }
                })
                // .addCrtificateRaw(R.raw.srca)
                //.addCrtificateAssert("srca.cer")
                .setLogTag("httpdemo")
                .setCacheMode(CacheMode.NO_CACHE)
                .setCookieMode(GlobalConfig.COOKIE_DISK)
                .setDefaultUserAgent(System.getProperty("http.agent"))
                .setIgnoreCertificateVerify(false)
                //.setReadTimeout(15000)
                .setConnectTimeout(10000)
                //.setWriteTimeout(10000)
                .setTotalTimeOut(15000)
                .setRetryCount(0)
                .setRetryOnConnectionFailure(false)
                .addCommonHeader("clienttype","android");

        //IgnoreSslVerifyForAll.enable = true;

        //SafetyUtil.addSSLPinner("*.zhihu.com","sha256/RUZBQThBMjU3RTk2MDhENDkwNThBQkU3NTI1NTA0RUIwNURFMUNFMjYyMjlGQTIwREU2Qjg4NDM0RTZERkZCNg==");
        //SafetyUtil.setSSLPinnerConfig("*.zhihu.com",true);
    }
}
