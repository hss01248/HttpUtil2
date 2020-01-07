package com.hss01248.akuhttpdemo;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import com.hss01248.http.HttpUtil;
import com.silvrr.akuhttp.NetUtil;
import com.silvrr.akuhttp.other.HttpReqUrl;

/**
 * Created by hss on 2018/8/18.
 */

public class AkuAp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        NetUtil.init(this, true, "https://test-id-app.akulaku.com",
                "http://test.risk.akulaku.com", HttpReqUrl.UPLOAD_FILE_URL);
       // GlobalConfig.get().addCommonHeader("cv",181+"").addCommonHeader("dt","4");

        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

            }

            @Override
            public void onActivityStarted(Activity activity) {

            }

            @Override
            public void onActivityResumed(Activity activity) {

            }

            @Override
            public void onActivityPaused(Activity activity) {

            }

            @Override
            public void onActivityStopped(Activity activity) {

            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

            }

            @Override
            public void onActivityDestroyed(Activity activity) {
                HttpUtil.cancelByTag(activity);

            }
        });
    }
}
