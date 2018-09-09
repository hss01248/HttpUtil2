package com.hss01248.http.executer;

import android.text.TextUtils;

import com.hss01248.http.ConfigInfo;
import com.hss01248.http.HttpUtil;
import com.hss01248.http.RetrofitHelper;
import com.hss01248.http.StringParser;
import com.hss01248.http.Tool;
import com.hss01248.http.cache.CacheKeyHandler;
import com.hss01248.http.cache.CacheMode;
import com.hss01248.http.config.ConfigChecker;
import com.hss01248.http.config.FileDownlodConfig;
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

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import okhttp3.ResponseBody;

/**
 * Created by hss on 2018/7/22.
 */

public class Runner {

    public static <T> void asCallback(final ConfigInfo<T> info) {
        Observable<ResponseBean<T>> observable = asObservable(info);
        observable.observeOn(SchedulerProvider.getInstance().ui());
        observable.subscribe(info.getCallback());

    }

    public static <T> Observable<ResponseBean<T>> asObservable(ConfigInfo<T> info) {

        ConfigChecker.convertParams(info);
        String checkedStr = ConfigChecker.check(info);

        Tool.logJson(info);
        if (!TextUtils.isEmpty(checkedStr)) {
            //todo 定义一个异常类型
            return Observable.error(new RequestConfigCheckException(info, checkedStr));
        }

        /*if(info.isDownload){
            return DownloadRunner.executeDownload(info);
        }*/

        //下载
        if (info.isDownload()) {
            Observable<ResponseBody> net = RetrofitHelper.getResponseObservable(info);
            net.subscribeOn(SchedulerProvider.getInstance().io())//修改上面的线程  //todo 处理同步请求
                    .observeOn(SchedulerProvider.getInstance().io());//修改下面的线程
            return net
                    .map(new Function<ResponseBody, ResponseBean<T>>() {
                        @Override
                        public ResponseBean<T> apply(ResponseBody responseBody) throws Exception {
                            return DownloadParser.receiveInputStream(info, responseBody);
                        }
                    }).compose(SchedulerProvider.getInstance().io2UI())//todo 为何一定要有这个才不报错:networkonmainthread?compose才能转换整个的线程
                    .doOnNext(new Consumer<ResponseBean<T>>() {
                        @Override
                        public void accept(ResponseBean<T> tResponseBean) throws Exception {
                            if (tResponseBean.bean instanceof FileDownlodConfig) {
                                DownloadParser.handleMedia((FileDownlodConfig) tResponseBean.bean);
                            }

                        }
                    });
        }

        //上传
        if (info.isUploadBinary() || info.isUploadMultipart()) {
            Observable<ResponseBody> net = RetrofitHelper.getResponseObservable(info);
            net.subscribeOn(SchedulerProvider.getInstance().io())//修改上面的线程  //todo 处理同步请求
                    .observeOn(SchedulerProvider.getInstance().io());//修改下面的线程
            return net.compose(ResponseTransformer2.handleResult(info))
                    .timeout(info.getTotalTimeOut(), TimeUnit.MILLISECONDS)
                    .retry(info.getRetryCount())
                    .compose(SchedulerProvider.getInstance().io2UI());
        } else {
            return handleStringRequst(info);
        }

    }

    private static <T> Observable<ResponseBean<T>> handleStringRequst(ConfigInfo<T> info) {

        if(info.getCacheMode() == CacheMode.NO_CACHE || info.getCacheMode() == CacheMode.DEFAULT){

            return RetrofitHelper.getResponseObservable(info)
                    .subscribeOn(SchedulerProvider.getInstance().io())//修改上面的线程
                    .observeOn(SchedulerProvider.getInstance().io())//修改下面的线程
                    .compose(ResponseTransformer2.handleResult(info))
                    .timeout(info.getTotalTimeOut(), TimeUnit.MILLISECONDS)
                    .retry(info.getRetryCount());
        }





        Observable<ResponseBean<T>> net = RetrofitHelper.getResponseObservable(info)
                    .subscribeOn(SchedulerProvider.getInstance().io())//修改上面的线程
                .observeOn(SchedulerProvider.getInstance().io())//修改下面的线程
                .compose(ResponseTransformer2.handleResult(info));


        //根据缓存策略处理,缓存库采用https://github.com/z-chu/RxCache
        RxCache rxCache = HttpUtil.getRxCache();
        String cacheKey = CacheKeyHandler.getCacheKey(info);
        IObservableStrategy strategy = getStrategy(info);
        Observable<ResponseBean<T>> all =  net.compose(rxCache.transformObservable(cacheKey, ResponseBean.class, strategy))
        .map(new Function<CacheResult<ResponseBean<T>>, ResponseBean<T>>() {
            @Override
            public ResponseBean<T> apply(CacheResult<ResponseBean<T>> cacheResult) throws Exception {
                if(cacheResult == null){
                    return null;
                }
                Tool.logObj(cacheResult);
                ResponseBean<T> bean = cacheResult.getData();
                bean.isFromCache = !ResultFrom.Remote.equals(cacheResult.getFrom());
                //在外层解析bodyStr
                return StringParser.parseString(bean.bodyStr,info,bean.isFromCache);
            }
        });

        return all.timeout(info.getTotalTimeOut(), TimeUnit.MILLISECONDS)
                .retry(info.getRetryCount());
    }

    private static <T> IObservableStrategy getStrategy(ConfigInfo<T> info) {
        switch (info.getCacheMode()){
            case CacheMode.NO_CACHE:
                return CacheStrategy.onlyRemote();
            case CacheMode.ONLY_CACHE:
                return CacheStrategy.onlyCache();
            case CacheMode.IF_NONE_CACHE_REQUEST:
                return CacheStrategy.firstCache();
            case CacheMode.FIRST_CACHE_THEN_REQUEST:
                return CacheStrategy.cacheAndRemote();
            case CacheMode.DEFAULT:
                return CacheStrategy.none();
            case CacheMode.REQUEST_FAILED_READ_CACHE:
                return CacheStrategy.firstRemote();
        }
        return CacheStrategy.onlyRemote();
    }


}
