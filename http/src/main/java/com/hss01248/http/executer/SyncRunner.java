package com.hss01248.http.executer;

import android.text.TextUtils;

import com.hss01248.http.ConfigInfo;
import com.hss01248.http.Tool;
import com.hss01248.http.cache.CacheMode;
import com.hss01248.http.config.ConfigChecker;
import com.hss01248.http.response.ResponseBean;

/**
 * Created by hss on 2018/12/1.
 */

public class SyncRunner {

    public static  <T> ResponseBean<T> execute(ConfigInfo<T> info){

        ConfigChecker.convertParams(info);
        String checkedStr = ConfigChecker.check(info);
        Tool.logJson(info);
        if (!TextUtils.isEmpty(checkedStr)) {
            return null;
        }

        if(info.getCacheMode() == CacheMode.NO_CACHE){
            return runByNetEngine(info);
        }
        return null;

    }

    private static <T> ResponseBean<T> runByNetEngine(ConfigInfo<T> info) {
        return null;
    }

}
