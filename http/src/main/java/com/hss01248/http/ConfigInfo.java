package com.hss01248.http;

import android.util.Log;

import com.hss01248.http.callback.MyNetCallback;
import com.hss01248.http.callback.ProgressCallback;
import com.hss01248.http.config.DataCodeMsgJsonConfig;
import com.hss01248.http.config.FileDownlodConfig;
import com.hss01248.http.executer.Runner;
import com.hss01248.http.response.ResponseBean;
import com.hss01248.http.utils.HttpHeaders;
import com.hss01248.http.utils.HttpMethod;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import io.reactivex.Observable;
import okhttp3.Interceptor;

/**
 * Created by Administrator on 2016/9/3.
 */
public class ConfigInfo<T> {

    public ConfigInfo() {
    }
    private Object extraFromOut;//外面传递进来,一直沿着rx链一直传递下去的

    private boolean download;
    private boolean uploadMultipart;
    private boolean uploadBinary;


    //请求相关
    private int method = HttpMethod.GET;
    private String url;
    private Map<String, String> params = new HashMap<>();
    //请求头  http://tools.jb51.net/table/http_header
    private Map<String, String> headers = new HashMap<>();
    private Set<String> paramKeysSetNotForCacheKey;
    private String paramsStr;
    private boolean paramsAsJson = false;

    //上传的文件路径
    private Map<String, String> files;
    private Map<String, List<String>> files2;//一个key接收多个文件


    //响应相关
    Class clazz;
    boolean responseAsString;
    private boolean responseAsDownload;
    private boolean responseAsNormalJson;
    private boolean responseAsDataCodeMsgInJson = true;


    private boolean responseAsJsonArray;
    private boolean treatEmptyDataAsSuccess;//response或者string为空,data字段为空时,是否当做成功来处理
    private DataCodeMsgJsonConfig dataCodeMsgJsonConfig = GlobalConfig.get().dataCodeMsgJsonConfig;
    private FileDownlodConfig downlodConfig = GlobalConfig.get().downlodConfig;


    String responseBodyStr;
    //public ResponseBean<T> response;


    public Object getExtraFromOut() {
        return extraFromOut;
    }

    public ConfigInfo<T> setExtraFromOut(Object extraFromOut) {
        this.extraFromOut = extraFromOut;
        return this;
    }

    public ConfigInfo<T> setUrl(String url) {
        if (url.startsWith("http")) {
            this.url = url;
        } else {
            this.url = GlobalConfig.get().getBaseUrl() + url;
        }
        return this;
    }

    public ConfigInfo<T> setUrlWithHost(String url,String hostTag) {
        if (url.startsWith("http")) {
            this.url = url;
        } else {
            this.url = GlobalConfig.get().getBaseUrl(hostTag) + url;
        }
        return this;
    }

    public ConfigInfo<T> get() {
        method = HttpMethod.GET;
        return this;
    }

    public ConfigInfo<T> post() {
        method = HttpMethod.POST;
        return this;
    }

    public ConfigInfo<T> put() {
        method = HttpMethod.PUT;
        return this;
    }

    public ConfigInfo<T> delete() {
        method = HttpMethod.DELETE;
        return this;
    }

    public ConfigInfo<T> download() {
        method = HttpMethod.GET;
        download = true;
        totalTimeOut = GlobalConfig.get().getReadTimeout();
        return this;
    }

    public ConfigInfo<T> uploadMultipart() {
        method = HttpMethod.POST;
        uploadMultipart = true;
        totalTimeOut = GlobalConfig.get().getWriteTimeout();
        return this;
    }

    public ConfigInfo<T> uploadBinary(String filePath) {
        uploadBinary = true;
        treatEmptyDataAsSuccess = true;
        totalTimeOut = GlobalConfig.get().getWriteTimeout();
        if (files == null) {
            files = new HashMap<>();
        }
        files.put(RetrofitHelper.UPLOAD_BINARY_KEY, filePath);//内部取值用,key不会传到http中
        //添加类型头
        headers.put(HttpHeaders.HEAD_KEY_CONTENT_TYPE, Tool.getMimeType(filePath));
        return this;
    }


    public ConfigInfo<T> addHeader(String key, String value) {
        if (value == null) {
            Log.w("configinfo", "addHeader: value of " + key + " is null");
           // headers.put(key, null);
        } else {
            headers.put(key, value);
        }
        return this;
    }

    public ConfigInfo<T> addHeaderIf(String key, String value,boolean condition) {
        if(condition){
            addHeader(key,value);
        }
        return this;
    }

    /**
     * 添加必须的参数
     *
     * @param key
     * @param value 内部调用value.toString()转成字符串
     *              如果为null,则直接在校验时拦截,调用onError的回调.
     * @return
     */
    public ConfigInfo<T> addParam(String key, Object value) {
        if(params == null){
            params = new HashMap<>();
        }
        params.put(key, value.toString());
        return this;
    }

    public ConfigInfo<T> addParamIf(String key, Object value,boolean shouldAdd){
        return addParamWith(key,value,shouldAdd,false,false);
    }

    /**
     * 对应服务端spring标识reqired = false的字段,通过这个添加,value为null时才不会被拦截.而是自动过滤掉
     * @param key
     * @param value
     * @return
     */
    public ConfigInfo<T> addParamOptional(String key,Object value) {
        if(value !=null){
            addParam(key,value);
        }
        return this;
    }

    /**
     * @param key
     * @param value
     * @param shouldAdd 是否添加这个参数
     * @param isOptional 对于后台是否可选
     * @param notAsCacheKey 是否不用于cachekey的生成
     * @return
     */
    public ConfigInfo<T> addParamWith(String key, Object value,boolean shouldAdd,boolean isOptional,boolean notAsCacheKey){
       if(!shouldAdd){
            return this;
        }
        if(isOptional){
           addParam(key,value);
        }else {
            addParamOptional(key,value);
        }
        if(notAsCacheKey){
            setParamKeyNotUsedForCacheKey(key);
        }
        return this;

    }







    /**
     * 直接将拼接好的参数或者json化的参数塞进来
     * 常用于从抓包工具拷过来的,进行模拟请求
     *
     * @param paramsStr 格式: xxx=rrr&iii=888 或者序列化的{} 或者[]
     * @return
     */
    public ConfigInfo<T> addParamStr(String paramsStr) {
        this.paramsStr = Tool.urlDecode(paramsStr);
        return this;
    }


    /**
     * 参数序列化为json后传输,常用于post请求
     *
     * @return
     */
    public ConfigInfo<T> postParamsAsJson() {
        this.paramsAsJson = true;
        return this;
    }

    /**
     * 如果某个参数不应该作为缓存的一份子,则调用此方法,那么
     *
     * @return
     */
    public ConfigInfo<T> setParamKeyNotUsedForCacheKey(String paramKey) {
        if (paramKeysSetNotForCacheKey == null) {
            paramKeysSetNotForCacheKey = new HashSet<>();
        }
        paramKeysSetNotForCacheKey.add(paramKey);
        return this;
    }

    /**
     * 添加文件
     * 一般用于上传文件,一个key对应一个文件
     *
     * @param key
     * @param path
     * @return
     */
    public ConfigInfo<T> addFile(String key, String path) {
        if (files == null) {
            files = new HashMap<>();
        }
        files.put(key, path);
        return this;
    }

    /**
     * 常用于上传文件
     * 一个key接收多个文件
     *
     * @param key
     * @param paths
     * @return
     */
    public ConfigInfo<T> addFiles(String key, List<String> paths) {
        if (files2 == null) {
            files2 = new HashMap<>();
        }
        files2.put(key, paths);
        return this;
    }

    public ConfigInfo<T> setResponseAsJsonArray(boolean responseAsJsonArray) {
        this.responseAsJsonArray = responseAsJsonArray;
        return this;
    }


    public ConfigInfo<T> treatEmptyDataAsSuccess() {
        treatEmptyDataAsSuccess = true;
        return this;
    }

    /**
     * 响应作为普通json来解析,而不是按data-code-msg这种来解析
     *
     * @return
     */
    public ConfigInfo<T> responseAsNormalJson() {
        responseAsDataCodeMsgInJson = false;
        responseAsNormalJson = true;
        return this;
    }

    public ConfigInfo<T> responseAsDataCodeMsgJson() {
        responseAsDataCodeMsgInJson = true;
        return this;
    }

    /**
     * 设置解析DataCodeMsg格式的一些自定义配置.
     * 优先级: 这里的设置> 初始化时GlobalConfig的配置
     *
     * @param config
     * @return
     */
    public ConfigInfo<T> setDataCodeMsgJsonConfig(DataCodeMsgJsonConfig config) {
        dataCodeMsgJsonConfig = config;
        responseAsDataCodeMsgInJson = true;
        return this;
    }

    /**
     * 文件下载前后的一些配置
     *
     * @param config
     * @return
     */
    public ConfigInfo<T> setFileDownlodConfig(FileDownlodConfig config) {
        downlodConfig = config;
        responseAsDownload = true;
        return this;
    }


    public ConfigInfo<T> responseAsString() {
        responseAsString = true;
        return this;
    }


    //http协议相关
    private int cacheMode = GlobalConfig.get().getCacheMode();
    private int cookieMode;

    /**
     * 是否忽略证书校验
     *
     * @param ignoreCer
     * @return
     */
    public ConfigInfo<T> setIgnoreCer(boolean ignoreCer) {
        this.ignoreCer = ignoreCer;
        return this;
    }

    //本次请求是否忽略证书校验--也就是通过所有证书.
// 这个属性没有全局配置,也不建议全局配置. 如果是自签名,放置证书到raw下,并在初始化前addCer方法,即可全局使用https
    private boolean ignoreCer = GlobalConfig.get().isIgnoreCertificateVerify();

    /**
     * 设置重试次数
     * 将直接设置到rxjava操作符上,所有抛到errorconsumer的都将重试x次,慎用.
     * 一般情况下,如果要重试,请调用setRetryOnConnectionFailure
     *
     * @param retryCount
     * @return
     */
    public ConfigInfo<T> setRetryCount(int retryCount) {
        this.retryCount = retryCount;
        return this;
    }

    /**
     * 设置给okhttpclient的连接错误重试.
     * 如果设置为true,将在ConnectionFailure时重试一次
     *
     * @param retryOnConnectionFailure
     * @return
     */
    public ConfigInfo<T> setRetryOnConnectionFailure(boolean retryOnConnectionFailure) {
        this.retryOnConnectionFailure = retryOnConnectionFailure;
        return this;
    }

    /**
     * 设置okhttpclient 的连接超时时间,实质上是设置tcp的超时时间.
     * 但无法控制dns解析的超时时间.
     *
     * @param connectTimeout
     * @return
     */
    public ConfigInfo<T> setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
        return this;
    }

    /**
     * 设置总的超时时间,将作用于rxjava的timeout操作符,把控整个流程的时间
     *
     * @param totalTimeOut
     * @return
     */
    public ConfigInfo<T> setTotalTimeOut(int totalTimeOut) {
        this.totalTimeOut = totalTimeOut;
        return this;
    }

    /**
     *
     * @param cacheMode 取值: CacheMode.xxx
     * @return
     */
    public ConfigInfo<T> setCacheMode(int cacheMode) {
        this.cacheMode = cacheMode;
        return this;
    }

    public ConfigInfo<T> setCookieMode(int cookieMode) {
        this.cookieMode = cookieMode;
        return this;
    }


    //重試次數
    private int retryCount = GlobalConfig.get().getRetryCount();
    private boolean retryOnConnectionFailure = GlobalConfig.get().isRetryOnConnectionFailure();
    private int totalTimeOut = GlobalConfig.get().getTotalTimeOut();


    private int connectTimeout = GlobalConfig.get().getConnectTimeout();
    /*public int readTimeout = GlobalConfig.get().getConnectTimeout();
    public int writeTimeout = GlobalConfig.get().getConnectTimeout();*/

    /**
     * 为取消请求设置的tag, 常用activity,fragment,
     * 然后统一在activity,fragment的ondestory中调用静态方法取消请求.
     *
     * @param tagForCancle
     * @return
     */
    public ConfigInfo<T> setTagForCancle(Object tagForCancle) {
        this.tagForCancle = tagForCancle;
        return this;
    }

    //用于取消请求用的
    private Object tagForCancle;


    /*public LoadingDialogConfig getLoadingDialogConfig() {
        return loadingDialogConfig;
    }

    public ConfigInfo<T> setLoadingDialogConfig(LoadingDialogConfig loadingDialogConfig) {
        this.loadingDialogConfig = loadingDialogConfig;
        return this;
    }
    public ConfigInfo<T> showLoadingDialog() {
        if(loadingDialogConfig == null){
            loadingDialogConfig =  LoadingDialogConfig.newInstance();
        }
        return this;
    }
    public ConfigInfo<T> showLoadingDialog(Activity activity) {
        if(loadingDialogConfig == null){
            loadingDialogConfig = LoadingDialogConfig.newInstance();
        }
        loadingDialogConfig.setActivity(activity);
        return this;
    }

    //ui相关
    private LoadingDialogConfig loadingDialogConfig;*/



    /*private boolean isWithProgress ;
    private boolean isShowNotify;
    private String loadingMsg;
    private int loadingMsgId;
    public boolean isSilently;
    boolean showLoading;


    public ConfigInfo<T> showLoading() {
        showLoading = true;
        return this;
    }

    public ConfigInfo<T> showLoading(String loadingMsg) {
        this.loadingMsg = loadingMsg;
        showLoading = true;
        return this;
    }

    public ConfigInfo<T> showLoading(int loadingMsgId){
        this.loadingMsgId = loadingMsgId;
        showLoading = true;
        return this;
    }*/



   /* public  <T> ConfigInfo<T> request(Class<T> clazz){
        ConfigInfo<T> configInfo = new ConfigInfo<>();
        configInfo.clazz = clazz;
        return configInfo;
    }

    public  <T> ConfigInfo< List<T>> requestAsJsonArray(String url,Class<T> clazz){
        ConfigInfo<List<T>> configInfo = new ConfigInfo<List<T>>();
        //configInfo.clazz = type.getClass().getGenericSuperclass()
        // Type typeOfListOfFoo = new TypeToken<List<T>>(){}.getRawType();
        //new TypeToken<List<T>>(){}.getType().getClass();

        return configInfo;
    }*/


    ////public boolean responseAsNormalJsonArray;
    //public boolean responseAsDataCodeMsgInJsonArray;

    public void callback(MyNetCallback<ResponseBean<T>> callback) {
        this.callback = callback;
        this.progressCallback = callback;
        Runner.asCallback(this);
    }


    public Observable<ResponseBean<T>> asObservable() {
        return Runner.asObservable(this);
    }


    private MyNetCallback<ResponseBean<T>> callback;
    private ProgressCallback progressCallback;

    public ConfigInfo<T> setProgressCallback(ProgressCallback callback) {
        this.progressCallback = callback;
        return this;
    }


    private List<Interceptor> interceptors;

   /* public Dialog loadingDialog;
    public boolean isLoadingDialogHorizontal;
    public boolean updateProgress ;*/


    //緩存控制
    // public boolean forceGetNet = true;
   /* public boolean shouldReadCache = false;
    public boolean shouldCacheResponse = false;
    public int cacheMaxAge = Integer.MAX_VALUE/2; //单位秒*/




   /* public boolean fromCache = false;//内部控制,不让外部设置
    public boolean fromCacheSuccess = false;//内部控制,不让外部设置*/

    //優先級,备用 volley使用
    //public int priority = Priority_NORMAL;

    public ConfigInfo<T> setAppendCommonHeaders(boolean isAppendCommonHeaders) {
        this.appendCommonHeaders = isAppendCommonHeaders;
        return this;
    }

    public ConfigInfo<T> setAppendCommonParams(boolean isAppendCommon) {
        this.appendCommonParams = isAppendCommon;
        return this;
    }

    private boolean appendCommonHeaders;
    private boolean appendCommonParams;


    private boolean sync;

    /**
     * 设置为同步请求
     * 此时,发起处的线程必须为后台线程
     *
     * @param sync
     * @return
     */
    public ConfigInfo<T> setSync(boolean sync) {
        this.sync = sync;
        return this;
    }










    /*
    get方法
     */

    public boolean isDownload() {
        return download;
    }

    public boolean isUploadMultipart() {
        return uploadMultipart;
    }

    public boolean isUploadBinary() {
        return uploadBinary;
    }

    public int getMethod() {
        return method;
    }

    public String getUrl() {
        return url;
    }

    public Map<String, String> getParams() {
        return params;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public Set<String> getParamKeysSetNotForCacheKey() {
        return paramKeysSetNotForCacheKey;
    }

    public String getParamsStr() {
        return paramsStr;
    }

    public boolean isParamsAsJson() {
        return paramsAsJson;
    }

    public Map<String, String> getFiles() {
        return files;
    }

    public Map<String, List<String>> getFiles2() {
        return files2;
    }

    public Class getClazz() {
        return clazz;
    }

    public boolean isResponseAsString() {
        return responseAsString;
    }

    public boolean isResponseAsDownload() {
        return responseAsDownload;
    }

    public boolean isResponseAsNormalJson() {
        return responseAsNormalJson;
    }

    public boolean isResponseAsDataCodeMsgInJson() {
        return responseAsDataCodeMsgInJson;
    }

    public boolean isResponseAsJsonArray() {
        return responseAsJsonArray;
    }

    public boolean isTreatEmptyDataAsSuccess() {
        return treatEmptyDataAsSuccess;
    }

    public DataCodeMsgJsonConfig getDataCodeMsgJsonConfig() {
        return dataCodeMsgJsonConfig;
    }

    public FileDownlodConfig getDownlodConfig() {
        return downlodConfig;
    }

    public String getResponseBodyStr() {
        return responseBodyStr;
    }

    public int getCacheMode() {
        return cacheMode;
    }

    public int getCookieMode() {
        return cookieMode;
    }

    public boolean isIgnoreCer() {
        return ignoreCer;
    }

    public int getRetryCount() {
        return retryCount;
    }

    public boolean isRetryOnConnectionFailure() {
        return retryOnConnectionFailure;
    }

    public int getTotalTimeOut() {
        return totalTimeOut;
    }

    public int getConnectTimeout() {
        return connectTimeout;
    }

    public Object getTagForCancle() {
        return tagForCancle;
    }

    public MyNetCallback<ResponseBean<T>> getCallback() {
        return callback;
    }

    public ProgressCallback getProgressCallback() {
        return progressCallback;
    }

    public List<Interceptor> getInterceptors() {
        return interceptors;
    }

    public boolean isAppendCommonHeaders() {
        return appendCommonHeaders;
    }

    public boolean isAppendCommonParams() {
        return appendCommonParams;
    }

    public boolean isSync() {
        return sync;
    }


}
