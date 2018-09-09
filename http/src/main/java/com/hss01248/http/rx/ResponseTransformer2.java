package com.hss01248.http.rx;


import com.hss01248.http.ConfigInfo;
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
                return upstream
                        .subscribeOn(SchedulerProvider.getInstance().io())
                        .onErrorResumeNext(new ErrorResumeFunction<>(configInfo))
                        .flatMap(new ResponseFunction<T>(configInfo, false))
                        .subscribeOn(SchedulerProvider.getInstance().io());
            }
        };
    }


    /**
     * 非服务器产生的异常，比如本地无无网络请求，Json数据解析错误,HttpException,socket超时等等。
     *
     * @param <T>
     */
    private static class ErrorResumeFunction<T> implements Function<Throwable, ObservableSource<? extends ResponseBody>> {

        public ErrorResumeFunction(T configInfo) {


        }

        @Override
        public ObservableSource<? extends ResponseBody> apply(Throwable throwable) throws Exception {
            return Observable.error(throwable);
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
            /*boolean success = tResponse.success;
            String message = tResponse.getMsg();
            if (success) {
                return Observable.just(tResponse.data);
            } else {
                return Observable.error(new ApiException(tResponse.errCode, message));
            }*/
            /*String str = responseBody.string();
           MyLog.json(str);
            JSONObject object = new JSONObject(str);
            //String data = object.optString("data");//测试解析json
            String data = object.getJSONObject("data").optString("menuList");//测试解析jsonarr
            T t = JSON.parseObject(data, new TypeReference<T>() {});*/

            String str = responseBody.string();
            ResponseBean<T> t = StringParser.parseString(str, info, fromCache);
            if (t == null) {
                return Observable.error(new Throwable("response type is wrong"));
            }
            return Observable.just(t);


           /* try {
                String str = responseBody.string();
                ResponseBean<T> t = StringParser.parseString(str,info,fromCache);
                if(t ==null){
                    return Observable.error(new Throwable("response type is wrong"));
                }
                return Observable.just(t);
            }catch (Exception e){
                //e.printStackTrace();
                return Observable.error(e);
            }*/


            /*public Result<List<UserCourseDto>> getUserCourse(){
        String result = restTemplate.getForObject(MOCK_JSON_URL, String.class);
        return JSONObject.parseObject(result, new TypeReference<Result<List<UserCourseDto>>>() {});
    }*/
        }
    }


}
