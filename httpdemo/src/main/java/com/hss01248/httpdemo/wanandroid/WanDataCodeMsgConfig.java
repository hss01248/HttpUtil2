package com.hss01248.httpdemo.wanandroid;

import com.hss01248.http.config.DataCodeMsgJsonConfig;

import org.json.JSONObject;

/**
 * Created by hss on 2018/12/1.
 */

public class WanDataCodeMsgConfig {

    public static DataCodeMsgJsonConfig build(){
        return DataCodeMsgJsonConfig
                .newBuilder()
                .key_data("data")
                .key_code("errorCode")
                .key_msg("errorMsg")
                .successJudge(new DataCodeMsgJsonConfig.DataSuccessJudge() {
                    @Override
                    public boolean isResponseSuccess(JSONObject object) {
                        int code = object.optInt("errorCode");
                        return code==0;
                    }
                })
                .build();
    }
}
