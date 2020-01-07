package com.hss01248.akuhttpdemo;

import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.hss01248.http.Tool;
import com.hss01248.http.callback.BaseSubscriber;
import com.hss01248.http.response.ResponseBean;
import com.hss01248.http.rx.SchedulerProvider;
import io.silvrr.installment.common.utils.XLogUtil;
import com.silvrr.akuhttp.NetUtil;
import com.silvrr.akuhttp.S3Info;
import com.silvrr.akuhttp.other.HttpReqUrl;
import com.silvrr.akuhttp.other.LoginModel;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.btn_1)
    Button btn1;
    @BindView(R.id.btn_2)
    Button btn2;
    @BindView(R.id.btn_3)
    Button btn3;
    @BindView(R.id.btn_4)
    Button btn4;


    Profile profile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

    }

    @OnClick({R.id.btn_1, R.id.btn_2, R.id.btn_3, R.id.btn_4})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_1:
                LoginModel.login();
                break;
            case R.id.btn_2:{
                NetUtil.request(HttpReqUrl.PROFILE_DETAIL_URL,Profile.class)
                        //.setCacheMode(CacheMode.REQUEST_FAILED_READ_CACHE)
                        .asObservable()
                        .observeOn(SchedulerProvider.getInstance().ui())
                        .subscribe(new BaseSubscriber<ResponseBean<Profile>>(true,this) {

                            @Override
                            public void onSuccess(ResponseBean<Profile> response) {
                                MainActivity.this.profile = response.bean;
                                Tool.logw("onSuccess:from cache:"+response.isFromCache);
                            }

                            @Override
                            public void onError(String msgCanShow) {
                                Tool.logw(msgCanShow);

                            }

                        });
                        /*.callback(new MyNetCallback<ResponseBean<Profile>>() {
                            @Override
                            public void onSuccess(ResponseBean<Profile> response) {
                                MainActivity.this.profile = response.bean;
                            }

                            @Override
                            public void onError(String msgCanShow) {
                                XLogUtil.w(msgCanShow);
                            }
                        });*/
            }
                break;
            case R.id.btn_3: {
                String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM) + "/" + "安徽省合肥市肥西县丰乐镇-2018年2月4日 下午2:29.jpg";
                NetUtil.uploadSingleImg("user:face", path, null)
                        .observeOn(SchedulerProvider.getInstance().ui())
                        .subscribe(new BaseSubscriber<ResponseBean<S3Info>>(true,this) {
                            @Override
                            public void onSuccess(ResponseBean<S3Info> response) {
                                response.bean = (S3Info) response.extraFromOut;
                                XLogUtil.objAsJson(response);
                            }

                            @Override
                            public void onError(String msgCanShow) {
                                XLogUtil.e(msgCanShow);
                            }
                        });

            }

                break;
            case R.id.btn_4:{
                String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)+"/"+"安徽省合肥市肥西县丰乐镇-2018年2月4日 下午2:29.jpg";
                String path2 = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)+"/"+"安徽省合肥市肥西县严店乡-2018年2月4日 下午2:29.jpg";

                List<String> paths = new ArrayList<>();
                paths.add(path);
                paths.add(path2);

                NetUtil.uploadImgs("user:chat",paths)
                        .timeout(2, TimeUnit.MINUTES)
                        .observeOn(SchedulerProvider.getInstance().ui())
                        .subscribe(new BaseSubscriber<ResponseBean<S3Info>>(true,this) {
                            @Override
                            public void onSuccess(ResponseBean<S3Info> response) {

                            }

                            @Override
                            public void onError(String msgCanShow) {
                                XLogUtil.e(msgCanShow);

                            }
                        });
            }



                break;
        }
    }


}
