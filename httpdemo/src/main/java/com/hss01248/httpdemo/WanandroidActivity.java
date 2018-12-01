package com.hss01248.httpdemo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;

import com.hss01248.http.HttpUtil;
import com.hss01248.http.Tool;
import com.hss01248.http.callback.MyNetCallback;
import com.hss01248.http.response.ResponseBean;
import com.hss01248.httpdemo.threadpool.ThreadPoolFactory;
import com.hss01248.httpdemo.wanandroid.ReqUrls;
import com.hss01248.httpdemo.wanandroid.bean.HomeTabsBean;
import com.orhanobut.logger.MyLog;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by hss on 2018/12/1.
 */

public class WanandroidActivity extends AppCompatActivity {


    @BindView(R.id.get_string)
    Button getString;
    @BindView(R.id.post_string)
    Button postString;
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
        HttpUtil.requestAsJsonArray(ReqUrls.HOME_TABS, HomeTabsBean.class)
                .callback(new MyNetCallback<ResponseBean<List<HomeTabsBean>>>(true,null) {
                    @Override
                    public void onSuccess(ResponseBean<List<HomeTabsBean>> response) {
                        Tool.logJson(response.bean);
                    }

                    @Override
                    public void onError(String msgCanShow) {
                        Tool.logw(msgCanShow);

                    }
                });

    }

    @OnClick(R.id.post_string)
    public void onPostStringClicked() {
    }

    @OnClick(R.id.get_json)
    public void onGetJsonClicked() {

        ThreadPoolFactory.getDownLoadPool().execute(new Runnable() {
            @Override
            public void run() {
                MyLog.e("before.....");
                HttpUtil.requestAsJsonArray(ReqUrls.HOME_TABS, HomeTabsBean.class)
                        .setSync(true)
                        .callback(new MyNetCallback<ResponseBean<List<HomeTabsBean>>>() {
                            @Override
                            public void onSuccess(ResponseBean<List<HomeTabsBean>> response) {
                                Tool.logJson(response.bean);
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
}
