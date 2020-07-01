package com.hss01248.http.request;


import com.fernandocejas.frodo2.annotation.RxLogObservable;

import java.util.Map;

import io.reactivex.Observable;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.http.Body;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.HeaderMap;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.PartMap;
import retrofit2.http.QueryMap;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

/**
 * Created by Zaifeng on 2018/2/28.
 * 封装请求的接口
 */

public interface ApiService {

    @RxLogObservable
    @GET()
    Observable<ResponseBody> get(@Url String url,
                                 @QueryMap Map<String, String> params,
                                 @HeaderMap Map<String, String> headers);

    /**
     * 注意:
     * 1.如果方法的泛型指定的类不是ResonseBody,retrofit会将返回的string成用json转换器该类的一个对象,
     * 如果不需要gson转换,那么就指定泛型为ResponseBody,
     * 只能是ResponseBody,子类都不行,同理,下载上传时,也必须指定泛型为ResponseBody
     * 2. map不能为null,否则该请求不会执行,但可以size为空.
     * 3.使用@url,而不是@Path注解,后者放到方法体上,会强制先urlencode,然后与baseurl拼接,请求无法成功
     *
     * @param url
     * @param map
     * @return
     */
    @RxLogObservable
    @FormUrlEncoded
    @POST()
    Observable<ResponseBody> post(@Url String url,
                                  @FieldMap Map<String, String> map,
                                  @HeaderMap Map<String, String> headers);

    /**
     * 直接post体为一个json格式时,使用这个方法.注意:@Body 不能与@FormUrlEncoded共存
     *
     * @param url
     * @param body
     * @return
     */
    @RxLogObservable
    @POST()
    Observable<ResponseBody> jsonPost(@Url String url,
                                      @Body RequestBody body,
                                      @HeaderMap Map<String, String> headers);

    @RxLogObservable
    @Streaming //流式下载,不加这个注解的话,会整个文件字节数组全部加载进内存,可能导致oom
    @GET
    Observable<ResponseBody> download(@Url String fileUrl,
                                      @QueryMap Map<String, String> params,
                                      @HeaderMap Map<String, String> headers);
    @RxLogObservable
    @POST()
    @Multipart
    Observable<ResponseBody> uploadMultipart(@Url String url, @PartMap Map<String, RequestBody> params,
                                             @PartMap Map<String, RequestBody> files,
                                             @HeaderMap Map<String, String> headers);
    @RxLogObservable
    @PUT()
    Observable<ResponseBody> uploadRawByPut(@Url String url, @Body RequestBody file, @HeaderMap Map<String, String> headers);
    @RxLogObservable
    @POST()
    Observable<ResponseBody> uploadRawByPost(@Url String url, @Body RequestBody file, @HeaderMap Map<String, String> headers);


}
