package com.hss01248.httpdemo;

import com.hss01248.basecache.BaseCacher;
import com.hss01248.http.HttpUtil;
import com.hss01248.http.Tool;
import com.hss01248.http.callback.MyNetCallback;
import com.hss01248.http.response.ResponseBean;
import com.hss01248.httpdemo.wanandroid.ReqUrls;
import com.hss01248.httpdemo.wanandroid.WanDataCodeMsgConfig;
import com.hss01248.httpdemo.wanandroid.bean.HomeTabsBean;

import java.util.List;

public class TestCache extends BaseCacher<List<HomeTabsBean>> {
    static TestCache instance;

    public static TestCache getInstance(){
        if(instance == null){
            instance = new TestCache();
        }
        return instance;
    }

    @Override
    protected void doRequest(IRequestCallback<List<HomeTabsBean>> callBack) {
        HttpUtil.requestAsJsonArray(ReqUrls.BASE_URL+ReqUrls.HOME_TABS, HomeTabsBean.class)
                .setDataCodeMsgJsonConfig(WanDataCodeMsgConfig.build())
                .callback(new MyNetCallback<ResponseBean<List<HomeTabsBean>>>() {
                    @Override
                    public void onSuccess(ResponseBean<List<HomeTabsBean>> response) {
                        Tool.logJson(response.data);
                        callBack.onSuccess(response.data);
                    }

                    @Override
                    public void onError(String msgCanShow) {
                        Tool.logw(msgCanShow);
                        callBack.onError("99",msgCanShow);

                    }
                });
    }
}
