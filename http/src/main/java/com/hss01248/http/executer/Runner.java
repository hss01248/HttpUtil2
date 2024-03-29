package com.hss01248.http.executer;

import android.text.TextUtils;

import androidx.annotation.NonNull;


import com.hss01248.http.ConfigInfo;
import com.hss01248.http.GlobalConfig;
import com.hss01248.http.HttpUtil;
import com.hss01248.http.RetrofitHelper;
import com.hss01248.http.StringParser;
import com.hss01248.http.Tool;
import com.hss01248.http.cache.CacheKeyHandler;
import com.hss01248.http.cache.CacheMode;
import com.hss01248.http.callback.BaseSubscriber2;
import com.hss01248.http.config.ConfigChecker;
import com.hss01248.http.config.LoadingDialogConfig;
import com.hss01248.http.exceptions.ExceptionWrapper;
import com.hss01248.http.exceptions.RequestConfigCheckException;
import com.hss01248.http.response.DownloadParser;
import com.hss01248.http.response.ResponseBean;
import com.hss01248.http.rx.ResponseTransformer2;
import com.hss01248.http.rx.SchedulerProvider;
import com.zchu.rxcache.RxCache;
import com.zchu.rxcache.data.CacheResult;
import com.zchu.rxcache.data.ResultFrom;
import com.zchu.rxcache.stategy.CacheStrategy;
import com.zchu.rxcache.stategy.IObservableStrategy;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Function;
import okhttp3.ResponseBody;

/**
 * Created by hss on 2018/7/22.
 */

public class Runner {

    public static <T> void asCallback(final ConfigInfo<T> info) {
        Observable<ResponseBean<T>> observable = asObservable(info);
        if (!info.isSync()) {
            observable = observable.observeOn(SchedulerProvider.getInstance().ui());
        }
       /* observable.subscribeOn(SchedulerProvider.getInstance().io())
        .compose(SchedulerProvider.getInstance().toUI())*/
        if (info.showLoading) {
            info.getCallback().dialogConfig = LoadingDialogConfig.newInstance();
        }
        observable.subscribe(new BaseSubscriber2<>(info.getCallback()));

    }

    //@RxLogObservable
    public static <T> Observable<ResponseBean<T>> asObservable(ConfigInfo<T> info) {

        ConfigChecker.convertParams(info);
        String checkedStr = ConfigChecker.check(info);
        Tool.logd(info.toString());
        if (!TextUtils.isEmpty(checkedStr)) {
            //todo 定义一个异常类型
            return Observable.error(new RequestConfigCheckException(info, checkedStr));
        }


        //下载
        if (info.isDownload()) {
            Observable<ResponseBody> net = RetrofitHelper.getResponseObservable(info);
            if (!info.isSync()) {
                net = net.subscribeOn(SchedulerProvider.getInstance().io());//修改上面的线程  //todo 处理同步请求
            }
            return net
                    .map(new Function<ResponseBody, ResponseBean<T>>() {
                        @Override
                        public ResponseBean<T> apply(ResponseBody responseBody) throws Exception {
                            return DownloadParser.receiveInputStream(info, responseBody);
                        }
                    }).onErrorResumeNext(ExceptionWrapper.wrapperException(info, false));
            //.compose(SchedulerProvider.getInstance().toUI())//todo 为何一定要有这个才不报错:networkonmainthread?compose才能转换整个的线程
                   /* .doOnNext(new Consumer<ResponseBean<T>>() {
                        @Override
                        public void accept(ResponseBean<T> tResponseBean) throws Exception {
                            if (tResponseBean.data instanceof FileDownlodConfig) {
                                DownloadParser.handleMedia((FileDownlodConfig) tResponseBean.data);
                            }

                        }
                    });*/
        }

        //上传
        if (info.isUploadBinary() || info.isUploadMultipart()) {
            Observable<ResponseBody> net = RetrofitHelper.getResponseObservable(info)
                    .subscribeOn(SchedulerProvider.getInstance().io());
            return net.timeout(info.getTotalTimeOut(), TimeUnit.MILLISECONDS)
                    .compose(ResponseTransformer2.handleResult(info))
                    .retry(info.getRetryCount());
        } else {
            return handleStringRequst(info);
        }

    }

    private static <T> Observable<ResponseBean<T>> handleStringRequst(ConfigInfo<T> info) {

        //不需要自定义缓存时，采用okhttp本身的缓存
        if (info.getCacheMode() == CacheMode.NO_CACHE || info.getCacheMode() == CacheMode.DEFAULT) {
            Observable<ResponseBody> observable = RetrofitHelper.getResponseObservable(info);
            if (!info.isSync()) {
                observable = observable.subscribeOn(SchedulerProvider.getInstance().io());//修改上面的线程
            }
            return observable.timeout(info.getTotalTimeOut(), TimeUnit.MILLISECONDS)
                    .compose(ResponseTransformer2.handleResult(info))
                    .retry(info.getRetryCount());
        }

        //需要用自定义缓存模式时，采用rxcache
        Observable<ResponseBody> observable = RetrofitHelper.getResponseObservable(info);
        if (!info.isSync()) {
            observable = observable.subscribeOn(SchedulerProvider.getInstance().io());//修改上面的线程
        }
        //.observeOn(SchedulerProvider.getInstance().io())//修改下面的线程
        Observable<ResponseBean<T>> net = observable.compose(ResponseTransformer2.handleResult(info));

        //根据缓存策略处理,缓存库采用https://github.com/z-chu/RxCache
        RxCache rxCache = HttpUtil.getRxCache();
        String cacheKey = CacheKeyHandler.getCacheKey(info);
        IObservableStrategy strategy = getStrategy(info);
        final ResponseBean<T>[] responseBean = new ResponseBean[1];
        Type type = info.getClazz();
        if (info.isResponseAsJsonArray()) {
            //type = new TypeToken<T>(){}.getType();
            type = com.google.gson.internal.$Gson$Types.newParameterizedTypeWithOwner(null, ArrayList.class, info.getClazz());

        }

        Observable<ResponseBean<T>> all = net
                .map(new Function<ResponseBean<T>, T>() {
                    @Override
                    public T apply(ResponseBean<T> tResponseBean) throws Exception {
                        responseBean[0] = tResponseBean;
                        return tResponseBean.data;
                    }
                })
                //new TypeToken<T>(){}.getType()

                .compose(rxCache.transformObservable(cacheKey, type, strategy))
                .map(new Function<CacheResult<T>, ResponseBean<T>>() {
                    @Override
                    public ResponseBean<T> apply(CacheResult<T> cacheResult) throws Exception {
                        if (cacheResult == null) {
                            Tool.logw("cacheResult == null, cache is empty");
                            if (info.getCallback() != null) {
                                info.getCallback().onCache(false);
                            }
                            return null;
                        }
                        Tool.logObj(cacheResult);
                        boolean isFromCache = !ResultFrom.Remote.equals(cacheResult.getFrom());
                        if(isFromCache){
                            Tool.logw("cacheResult read success");
                            ResponseBean<T> bean = getResponseBeanOfCache(cacheResult);
                            return bean;
                        }

                        if (responseBean[0] != null) {
                            return responseBean[0];
                        }
                        ResponseBean<T> bean = new ResponseBean<>();
                        bean.data = cacheResult.getData();
                        bean.isFromCache = false;
                        //在外层解析bodyStr
                        return StringParser.parseString(bean.bodyStr, info, false);
                    }

                    @NonNull
                    private ResponseBean<T> getResponseBeanOfCache(CacheResult<T> cacheResult) throws Exception {
                        ResponseBean<T> bean = responseBean[0] == null ? new ResponseBean() : responseBean[0];
                        if (info.getCallback() != null) {
                            info.getCallback().onCache(true);
                        }
                        bean.data = cacheResult.getData();
                        bean.isFromCache = !ResultFrom.Remote.equals(cacheResult.getFrom());
                        bean.setInfo(info);
                        bean.url = info.getUrl();
                        return bean;
                    }
                }).timeout(info.getTotalTimeOut(), TimeUnit.MILLISECONDS)
                .onErrorResumeNext(ExceptionWrapper.wrapperException(info, false));
        return all.retry(info.getRetryCount());
    }

    /**
     * @param info
     * @param <T>
     * @return
     */
    private static <T> IObservableStrategy getStrategy(ConfigInfo<T> info) {
        switch (info.getCacheMode()) {
            case CacheMode.NO_CACHE:
                if (info.isSync()) {
                    return CacheStrategy.onlyRemoteSync();
                } else {
                    return CacheStrategy.onlyRemote();
                }
            case CacheMode.ONLY_CACHE:
                if (info.isSync()) {
                    return CacheStrategy.onlyCache();
                } else {
                    return CacheStrategy.onlyCache();
                }
            case CacheMode.IF_NONE_CACHE_REQUEST:
                if (info.isSync()) {
                    return CacheStrategy.firstCacheSync();
                } else {
                    return CacheStrategy.firstCache();
                }
            case CacheMode.FIRST_CACHE_THEN_REQUEST:
                if (info.isSync()) {
                    return CacheStrategy.cacheAndRemoteSync();
                } else {
                    return CacheStrategy.cacheAndRemote();
                }
            case CacheMode.DEFAULT:
                return CacheStrategy.none();
            case CacheMode.REQUEST_FAILED_READ_CACHE:
                if (info.isSync()) {
                    return CacheStrategy.firstRemoteSync();
                } else {
                    return CacheStrategy.firstRemote();
                }
            default:
                if (info.isSync()) {
                    return CacheStrategy.onlyRemoteSync();
                } else {
                    return CacheStrategy.onlyRemote();
                }
        }

    }


}
