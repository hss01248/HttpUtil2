package com.hss01248.http.rx;


import com.hss01248.http.ConfigInfo;
import com.hss01248.http.Tool;
import com.hss01248.http.exceptions.ExceptionWrapper;
import com.hss01248.http.response.ResponseBean;
import com.hss01248.http.StringParser;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.functions.Function;
import okhttp3.ResponseBody;

/**
 * Created by Zaifeng on 2018/2/28.
 * 对返回的数据进行处理，区分异常的情况。
 */

public class ResponseTransformer2 {

    public static <T> ObservableTransformer<ResponseBody, ResponseBean<T>> handleResult(ConfigInfo<T> configInfo) {
        return new ObservableTransformer<ResponseBody, ResponseBean<T>>() {
            @Override
            public ObservableSource<ResponseBean<T>> apply(Observable<ResponseBody> upstream) {
                if (!configInfo.isSync()) {
                    upstream = upstream.subscribeOn(SchedulerProvider.getInstance().io());
                }
                //upstream = upstream.onErrorResumeNext(new ErrorResumeFunction<T>(configInfo, false));
                upstream = upstream.onErrorResumeNext(ExceptionWrapper.wrapperException(configInfo,false));
                Observable<ResponseBean<T>> bean = upstream
                        .flatMap(new ResponseFunction<T>(configInfo, false))
                        .onErrorResumeNext(ExceptionWrapper.wrapperException(configInfo,false));
                if (!configInfo.isSync()) {
                    return bean.subscribeOn(SchedulerProvider.getInstance().io());
                }
                return bean;

            }
        };
    }


    /**
     * 非服务器产生的异常，比如本地无无网络请求，Json数据解析错误,HttpException,socket超时等等。
     *
     * 无法捕获rxjava内部产生的异常,比如设置的超时异常:
     * ObservableTimeoutTimed$TimeoutObserver.onTimeout(ObservableTimeoutTimed.java:132)
     *
     * @param <T>
     */
    private static class ErrorResumeFunction<T> implements Function<Throwable, ObservableSource<? extends ResponseBody>> {

        ConfigInfo<T> info;
        boolean fromCache;

        public ErrorResumeFunction(ConfigInfo<T> configInfo, boolean fromCache) {
            this.info = configInfo;
            this.fromCache = fromCache;
        }

        @Override
        public ObservableSource<? extends ResponseBody> apply(Throwable throwable) throws Exception {
            if (throwable instanceof ExceptionWrapper) {
                return Observable.error(throwable);
            } else {
                return Observable.error(new ExceptionWrapper(throwable, info, fromCache));
            }
        }
    }

    /**
     * 服务其返回的数据解析
     * 正常服务器返回数据和服务器可能返回的exception
     *
     * @param <T>
     */
    private static class ResponseFunction<T> implements Function<ResponseBody, ObservableSource<ResponseBean<T>>> {

        ConfigInfo<T> info;
        boolean fromCache;

        public ResponseFunction(ConfigInfo<T> info, boolean fromCache) {
            this.info = info;
            this.fromCache = fromCache;
        }

        @Override
        public ObservableSource<ResponseBean<T>> apply(ResponseBody responseBody) throws Exception {
            Tool.logd("StringParser.parseString....");
            String str = responseBody.string();
            ResponseBean<T> t = StringParser.parseString(str, info, fromCache);
            if (t == null) {
                return Observable.error(new Throwable("response type is wrong"));
            }
            return Observable.just(t);
        }
    }


}
