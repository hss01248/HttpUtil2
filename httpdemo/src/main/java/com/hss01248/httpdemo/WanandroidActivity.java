package com.hss01248.httpdemo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.hss01248.http.HttpUtil;
import com.hss01248.http.Tool;

import com.hss01248.http.callback.MyNetCallback;
import com.hss01248.http.exceptions.ExceptionWrapper;
import com.hss01248.http.response.ResponseBean;
import com.hss01248.httpdemo.threadpool.ThreadPoolFactory;
import com.hss01248.httpdemo.wanandroid.ReqUrls;
import com.hss01248.httpdemo.wanandroid.WanDataCodeMsgConfig;
import com.hss01248.httpdemo.wanandroid.bean.ArticleListBean;
import com.hss01248.httpdemo.wanandroid.bean.BannerInfo;
import com.hss01248.httpdemo.wanandroid.bean.HomeTabsBean;
import com.orhanobut.logger.MyLog;

import java.io.IOException;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import es.dmoral.toasty.MyToast;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import okhttp3.CertificatePinner;
import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * Created by hss on 2018/12/1.
 * 接口文档: https://www.wanandroid.com/blog/show/2
 */

public class WanandroidActivity extends AppCompatActivity {


    @BindView(R.id.get_string)
    Button getString;

    @BindView(R.id.get_json)
    Button getJson;
    @BindView(R.id.post_json)
    Button postJson;
    @BindView(R.id.get_standard_json)
    Button getStandardJson;
    @BindView(R.id.post_standard_json)
    Button postStandardJson;
    @BindView(R.id.download)
    Button download;
    @BindView(R.id.upload)
    Button upload;
    @BindView(R.id.postbyjson)
    Button postbyjson;
    @BindView(R.id.testvoice)
    Button testvoice;
    @BindView(R.id.testvoice2)
    Button testvoice2;
    @BindView(R.id.unbind)
    Button unbind;

    public static void launch(Activity activity) {
        activity.startActivity(new Intent(activity, WanandroidActivity.class));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wananzhuo);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.get_string)
    public void onGetStringClicked() {

        HttpUtil.requestString("https://www.github.com")
                .callbackByLiveData(this, new MyNetCallback<ResponseBean<String>>() {
                    @Override
                    public void onSuccess(ResponseBean<String> response) {
                        MyLog.obj(response);
                    }

                    @Override
                    public void onError(String msgCanShow) {
                        MyLog.w(msgCanShow);
                    }
                });
       /* HttpUtil.requestAsJsonArray(ReqUrls.HOME_TABS, HomeTabsBean.class)
                .callback(new MyNetCallback<ResponseBean<List<HomeTabsBean>>>(true,null) {
                    @Override
                    public void onSuccess(ResponseBean<List<HomeTabsBean>> response) {
                        Tool.logJson(response.data);
                    }

                    @Override
                    public void onError(String msgCanShow) {
                        Tool.logw(msgCanShow);
                        MyToast.errorBigL(msgCanShow);

                    }
                });*/

    }

    @OnClick(R.id.combine)
    public void combine() {
      Observable<ResponseBean<List<HomeTabsBean>>> observable1 =  HttpUtil.requestAsJsonArray(ReqUrls.BASE_URL+ReqUrls.HOME_TABS, HomeTabsBean.class)
                .setDataCodeMsgJsonConfig(WanDataCodeMsgConfig.build()).asObservable();

      Observable observable2 =   HttpUtil.requestAsJsonArray(ReqUrls.BASE_URL+ReqUrls.HOME_articles, ArticleListBean.class)
                .setDataCodeMsgJsonConfig(WanDataCodeMsgConfig.build()).asObservable();//允许失败,必失败

        Observable observable3 =   HttpUtil.request(ReqUrls.BASE_URL+ReqUrls.HOME_banners, BannerInfo.class)
                .setDataCodeMsgJsonConfig(WanDataCodeMsgConfig.build()).asObservable();
        //.setTotalTimeOut(200)

        Observable.merge(observable1,observable2,observable3)
                .onErrorResumeNext(new Function<Throwable, ObservableSource>() {
                    @Override
                    public ObservableSource apply(Throwable throwable) throws Exception {
                        if(throwable instanceof ExceptionWrapper){
                            ExceptionWrapper wrapper = (ExceptionWrapper) throwable;
                            if((ReqUrls.BASE_URL+ReqUrls.HOME_articles).equals(wrapper.info.getUrl())){
                                return Observable.just(new Object());
                            }

                        }
                        return Observable.error(throwable);
                    }
                })
                .subscribe(new Observer() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        MyLog.d("onSubscribe",d.toString());
                    }

                    @Override
                    public void onNext(Object o) {
                        MyLog.obj("onNext",o);
                    }

                    @Override
                    public void onError(Throwable e) {
                        MyLog.e("onError",e.getMessage());
                        if(e.getCause() != null){
                            e.getCause().printStackTrace();
                        }else {
                            e.printStackTrace();
                        }


                    }

                    @Override
                    public void onComplete() {
                        MyLog.i("onComplete","finish");
                    }
                });







    }



    @OnClick(R.id.get_json)
    public void onGetJsonClicked() {

        ThreadPoolFactory.getDownLoadPool().execute(new Runnable() {
            @Override
            public void run() {
                MyLog.e("before.....");
                HttpUtil.requestAsJsonArray(ReqUrls.BASE_URL+ReqUrls.HOME_TABS, HomeTabsBean.class)
                        .setSync(true)
                        .setDataCodeMsgJsonConfig(WanDataCodeMsgConfig.build())
                        .callback(new MyNetCallback<ResponseBean<List<HomeTabsBean>>>() {
                            @Override
                            public void onSuccess(ResponseBean<List<HomeTabsBean>> response) {
                                Tool.logJson(response.data);
                            }

                            @Override
                            public void onError(String msgCanShow) {
                                Tool.logw(msgCanShow);

                            }
                        });

                MyLog.e("after.....");
            }
        });



    }

    @OnClick(R.id.post_json)
    public void onPostJsonClicked() {
    }

    @OnClick(R.id.get_standard_json)
    public void onGetStandardJsonClicked() {
    }

    @OnClick(R.id.post_standard_json)
    public void onPostStandardJsonClicked() {
    }

    @OnClick(R.id.download)
    public void onDownloadClicked() {
    }

    @OnClick(R.id.upload)
    public void onUploadClicked() {
    }

    @OnClick(R.id.postbyjson)
    public void onPostbyjsonClicked() {
    }

    @OnClick(R.id.testvoice)
    public void onTestvoiceClicked() {
    }

    @OnClick(R.id.testvoice2)
    public void onTestvoice2Clicked() {
    }

    @OnClick(R.id.unbind)
    public void onUnbindClicked() {
    }

    /**
     * https://blog.csdn.net/challenge51all/article/details/82909965
     * 不能与证书忽略共存,会冲突
     *
     * 2020-11-04 17:25:25.480 11286-11373/com.hss01248.httpdemo I/OkhttpAspect: getPublicKey().getAlgorithm:RSA
     * 2020-11-04 17:25:25.480 11286-11373/com.hss01248.httpdemo I/OkhttpAspect: getPublicKey().getFormat:X.509
     * 2020-11-04 17:25:25.481 11286-11373/com.hss01248.httpdemo I/OkhttpAspect: certificate.getPublicKey-sha256->toAsciiUppercase:[hex=7f97cd42f0c950a14c3b9d54a302a4c8a025595657a5a18ae816a1e325fdce42]
     * 2020-11-04 17:25:25.481 11286-11373/com.hss01248.httpdemo I/OkhttpAspect: certificate.getPublicKey-sha256->hex:7f97cd62f0c950a16c3b9d54a302a4c8a025597657a5a18ae816a1e325fdce62
     * 2020-11-04 17:25:25.481 11286-11373/com.hss01248.httpdemo I/OkhttpAspect: certificate.getPublicKey-sha256->base64:f5fNYvDJUKFsO51UowKkyKAlWXZXpaGK6Bah4yX9zmI=
     * 2020-11-04 17:25:25.481 11286-11373/com.hss01248.httpdemo I/OkhttpAspect: certificate-sha256->hex:efaa8a257e9608d49058abe7525504eb05de1ce26229fa20de6b88434e6dffb6
     * 2020-11-04 17:25:25.481 11286-11373/com.hss01248.httpdemo I/OkhttpAspect: getPublicKey().getAlgorithm:RSA
     * 2020-11-04 17:25:25.481 11286-11373/com.hss01248.httpdemo I/OkhttpAspect: getPublicKey().getFormat:X.509
     * 2020-11-04 17:25:25.482 11286-11373/com.hss01248.httpdemo I/OkhttpAspect: certificate.getPublicKey-sha256->toAsciiUppercase:[hex=cd422b491348fb824801803b44e7968c044d228358ac811b0a97c24504fa37a0]
     * 2020-11-04 17:25:25.483 11286-11373/com.hss01248.httpdemo I/OkhttpAspect: certificate.getPublicKey-sha256->hex:cd422b691368fb826801803b44e7968c046d228378ac811b0a97c24504fa37a0
     * 2020-11-04 17:25:25.483 11286-11373/com.hss01248.httpdemo I/OkhttpAspect: certificate.getPublicKey-sha256->base64:zUIraRNo+4JoAYA7ROeWjARtIoN4rIEbCpfCRQT6N6A=
     * 2020-11-04 17:25:25.483 11286-11373/com.hss01248.httpdemo I/OkhttpAspect: certificate-sha256->hex:8cc34e11c167045824ade61c4907a6440edb2c4398e99c112a859d661f8e2bc7
     * 2020-11-04 17:25:25.484 11286-11373/com.hss01248.httpdemo I/OkhttpAspect: getPublicKey().getAlgorithm:RSA
     * 2020-11-04 17:25:25.484 11286-11373/com.hss01248.httpdemo I/OkhttpAspect: getPublicKey().getFormat:X.509
     * 2020-11-04 17:25:25.484 11286-11373/com.hss01248.httpdemo I/OkhttpAspect: certificate.getPublicKey-sha256->toAsciiUppercase:[hex=aff988904dde12955d9bebbf928fdcc31cce328d5b9384f21c8941ca26e20391]
     * 2020-11-04 17:25:25.484 11286-11373/com.hss01248.httpdemo I/OkhttpAspect: certificate.getPublicKey-sha256->hex:aff988906dde12955d9bebbf928fdcc31cce328d5b9384f21c8941ca26e20391
     * 2020-11-04 17:25:25.486 11286-11373/com.hss01248.httpdemo I/OkhttpAspect: certificate.getPublicKey-sha256->base64:r/mIkG3eEpVdm+u/ko/cwxzOMo1bk4TyHIlByibiA5E=
     * 2020-11-04 17:25:25.486 11286-11373/com.hss01248.httpdemo I/OkhttpAspect: certificate-sha256->hex:4348a0e9444c78cb265e058d5e8944b4d84f9662bd26db257f8934a443c70161
     * 2020-11-04 17:25:25.506 11286-11373/com.hss01248.httpdemo W/System.err: javax.net.ssl.SSLPeerUnverifiedException: Certificate pinning failure!
     * 2020-11-04 17:25:25.506 11286-11373/com.hss01248.httpdemo W/System.err:   Peer certificate chain:
     * 2020-11-04 17:25:25.507 11286-11373/com.hss01248.httpdemo W/System.err:     sha256/f5fNYvDJUKFsO51UowKkyKAlWXZXpaGK6Bah4yX9zmI=: CN=*.zhihu.com,OU=IT,O=智者四海（北京）技术有限公司,L=北京市,C=CN
     * 2020-11-04 17:25:25.507 11286-11373/com.hss01248.httpdemo W/System.err:     sha256/zUIraRNo+4JoAYA7ROeWjARtIoN4rIEbCpfCRQT6N6A=: CN=GeoTrust RSA CA 2018,OU=www.digicert.com,O=DigiCert Inc,C=US
     * 2020-11-04 17:25:25.507 11286-11373/com.hss01248.httpdemo W/System.err:     sha256/r/mIkG3eEpVdm+u/ko/cwxzOMo1bk4TyHIlByibiA5E=: CN=DigiCert Global Root CA,OU=www.digicert.com,O=DigiCert Inc,C=US
     * 2020-11-04 17:25:25.507 11286-11373/com.hss01248.httpdemo W/System.err:   Pinned certificates for zhuanlan.zhihu.com:
     * 2020-11-04 17:25:25.507 11286-11373/com.hss01248.httpdemo W/System.err:     sha256/AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA=
     * 2020-11-04 17:25:25.507 11286-11373/com.hss01248.httpdemo W/System.err:     at okhttp3.CertificatePinner.check(CertificatePinner.java:204)
     * 2020-11-04 17:25:25.507 11286-11373/com.hss01248.httpdemo W/System.err:     at okhttp3.internal.connection.RealConnection.connectTls(RealConnection.java:334)
     * 2020-11-04 17:25:25.507 11286-11373/com.hss01248.httpdemo W/System.err:     at okhttp3.internal.connection.RealConnection.establishProtocol(RealConnection.java:283)
     * 2020-11-04 17:25:25.507 11286-11373/com.hss01248.httpdemo W/System.err:     at okhttp3.internal.connection.RealConnection.connect(RealConnection.java:168)
     * @param view
     *
     * chrome上看到的是certificate-sha256->hex
     * okhttp的CertificatePinner配置的证书锁定是certificate.getPublicKey-sha256->base64
     */
    public void sslPinner(View view) {
        //SSLPinerAspect.testSSlPin("*.zhihu.com","https://zhuanlan.zhihu.com/p/58308036");

        for (int i = 0; i < 3; i++) {
            HttpUtil.requestString("https://zhuanlan.zhihu.com/p/58308036")
                    .callback(new MyNetCallback<ResponseBean<String>>() {
                        @Override
                        public void onSuccess(ResponseBean<String> response) {

                        }

                        @Override
                        public void onError(String msgCanShow) {

                        }
                    });
        }



        /*String hostname = "zhihu.com";
        CertificatePinner certificatePinner = new CertificatePinner.Builder()
                .add("*.zhihu.com", "sha256/AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA=",//sha256/f5fNYvDJUKFsO51UowKkyKAlWXZXpaGK6Bah4yX9zmI=
                        "sha256/zUIraRNo+4JoAYA7ROeWjARtIoN4rIEbCpfCRQT6N6A=",
                        "sha256/r/mIkG3eEpVdm+u/ko/cwxzOMo1bk4TyHIlByibiA5E=")
                .build();
        OkHttpClient.Builder client = new OkHttpClient.Builder();
        client.certificatePinner(certificatePinner);

        Request request = new Request.Builder()
                .url("https://" + hostname)
                .build();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    client.build().newCall(request).execute();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();*/

    }
}
