package com.hss01248.http;

import android.app.Application;

import com.hss01248.friendlymsg.ExceptionFriendlyMsg;
import com.hss01248.friendlymsg.IFriendlyMsg;
import com.hss01248.http.cache.CacheMode;
import com.hss01248.http.config.DataCodeMsgJsonConfig;
import com.hss01248.http.config.FileDownlodConfig;
import com.hss01248.http.config.LoadingDialogConfig;
import com.hss01248.http.interceptors.OkHttpExceptionInterceptor;
import com.hss01248.http.interceptors.SameRequestInterceptor;
import com.hss01248.http.response.DownloadParser;
import com.hss01248.http.utils.HttpHeaders;
import com.hss01248.http.https.SslUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.functions.Consumer;
import okhttp3.Interceptor;

/**
 * Created by Administrator on 2017/3/9 0009.
 */

public class GlobalConfig {

    public boolean isDebug() {
        return debug;
    }

    public GlobalConfig setDebug(boolean debug) {
        this.debug = debug;
        return this;
    }

    private boolean debug;

    public LoadingDialogConfig.ILoadingDialog getDefaultLoadingDialog() {
        return defaultLoadingDialog;
    }

    public GlobalConfig setDefaultLoadingDialog(LoadingDialogConfig.ILoadingDialog defaultLoadingDialog) {
        this.defaultLoadingDialog = defaultLoadingDialog;
        return this;
    }

    private LoadingDialogConfig.ILoadingDialog defaultLoadingDialog;

    public Application getContext() {
        return context;
    }

    public GlobalConfig setContext(Application context) {
        this.context = context;
        return this;
    }

    private  Application context;

    public GlobalConfig setDownloadDir(String downloadDir) {
        this.downloadDir = downloadDir;
        return this;
    }

    public boolean isRetryOnConnectionFailure() {
        return retryOnConnectionFailure;
    }

    public GlobalConfig setRetryOnConnectionFailure(boolean retryOnConnectionFailure) {
        this.retryOnConnectionFailure = retryOnConnectionFailure;
        return this;
    }

    private boolean retryOnConnectionFailure = false;//默认不重试
    private String downloadDir;
    //private static GlobalConfig instance;




    public GlobalConfig setDataCodeMsgJsonConfig(DataCodeMsgJsonConfig dataCodeMsgJsonConfig) {
        this.dataCodeMsgJsonConfig = dataCodeMsgJsonConfig;
        return this;
    }

    public GlobalConfig setDownlodConfig(FileDownlodConfig downlodConfig) {
        this.downlodConfig = downlodConfig;
        return this;
    }

    DataCodeMsgJsonConfig dataCodeMsgJsonConfig = DataCodeMsgJsonConfig.newBuilder().build();

    FileDownlodConfig downlodConfig ;


    public INetTool getTool() {
        return iNetTool;
    }

     GlobalConfig setTool(INetTool tool) {
        iNetTool = tool;
        if(isOpenLog){
            tool.initialStetho(context);
        }
        return this;
    }

    private INetTool iNetTool;


    private GlobalConfig() {
        commonInterceptors = new ArrayList<>();
        commonInterceptors.add(new OkHttpExceptionInterceptor());
        commonInterceptors.add(new SameRequestInterceptor());
        //commonHeaders = new HashMap<>();
        //commonHeaders.put(SameRequestFilterInterceptor.HEAD_REQUEST,"YES");

    }

    public String getDownloadDir() {
        if (downloadDir == null) {
            downloadDir = DownloadParser.mkDefaultDownloadDir();
        }
        return downloadDir;
    }

    private static class SingletonHolder {
        private static final GlobalConfig INSTANCE = new GlobalConfig();
    }

    /**
     * 仅在
     *
     * @return
     */
    public static synchronized GlobalConfig get() {
        return SingletonHolder.INSTANCE;
    }

    //private  long PROGRESS_INTERMEDIATE = 300;//进度条更新间隔,默认300ms


    /*public Map<Integer, StringParseStrategy> getParseStrategyList() {
        return parseStrategyList;
    }

    public Map<Integer,StringParseStrategy> parseStrategyList = initStrategyList();
    public int customJsonType;

    private Map<Integer, StringParseStrategy> initStrategyList() {
        parseStrategyList = new HashMap<>();
        parseStrategyList.put(ConfigInfo.TYPE_STRING,new SimpleStringParser());
        parseStrategyList.put(ConfigInfo.TYPE_JSON,new CommonJsonStrategy());
        parseStrategyList.put(ConfigInfo.TYPE_JSON_FORMATTED,new StandardJsonParseStrategy());
        return parseStrategyList;
    }

    public GlobalConfig addJsonParseStragegy(int customJsonType,StringParseStrategy stringParseStrategy){
        this.customJsonType = customJsonType;
        parseStrategyList.put(customJsonType,stringParseStrategy);
        return this;
    }*/


    public Map<String, String> getCommonHeaders() {
        return commonHeaders;
    }

    public List<Interceptor> commonInterceptors = new ArrayList<>();

    public GlobalConfig addInterceptor(Interceptor interceptor) {
        commonInterceptors.add(interceptor);
        return this;
    }

    public GlobalConfig addCommonHeader(String key, String value) {
        this.commonHeaders.put(key, value);
        return this;
    }

    public Map<String, Object> getCommonParams() {
        return commonParams;
    }

    public GlobalConfig addCommonParam(String key, Object value) {
        this.commonParams.put(key, value);
        return this;
    }


    private Map<String, String> commonHeaders = new HashMap<>();
    private Map<String, Object> commonParams = new HashMap<>();
    private Map<String, String> baseUrlMap = new HashMap<>();
    private boolean isAppendCommonHeaders = true;
    private boolean isAppendCommonParams = true;

    public boolean isAppendCommonHeaders() {
        return isAppendCommonHeaders;
    }

    public GlobalConfig setAppendCommonHeaders(boolean appendCommonHeaders) {
        isAppendCommonHeaders = appendCommonHeaders;
        return this;
    }



    public boolean isAppendCommonParams() {
        return isAppendCommonParams;
    }

    public GlobalConfig setAppendCommonParams(boolean appendCommonParams) {
        isAppendCommonParams = appendCommonParams;
        return this;
    }


    //@CacheMode.Mode
    private int cacheMode = CacheMode.DEFAULT;//严格遵循http协议

    /**
     * 设置缓存策略 {@link CacheMode}
     */
    public GlobalConfig setCacheMode(int cacheMode) {
        this.cacheMode = cacheMode;
        return this;
    }

    public int getCacheMode() {
        return cacheMode;
    }

    public static final int COOKIE_NONE = 1;
    public static final int COOKIE_MEMORY = 2;
    public static final int COOKIE_DISK = 3;
    private int cookieMode = COOKIE_DISK;//默认是做持久化操作

    /**
     * 设置cookie管理策略
     */
    public GlobalConfig setCookieMode(int cookieMode) {
        this.cookieMode = cookieMode;
        return this;
    }

    public int getCookieMode() {
        return cookieMode;
    }

    /**
     * url前缀设置,格式类似
     */
    public GlobalConfig setBaseUrl(String url) {
        this.baseUrl = url;
         addBaseUrl(HOST_TAG_DEFAULT, url);
        return this;
    }

    private String baseUrl = "http://www.baidu.com/";
    public static final String HOST_TAG_DEFAULT = "default";

    public String getBaseUrl() {
        return baseUrl;
    }

    public String getBaseUrl(String hostTag) {
        if(baseUrlMap !=null && baseUrlMap.containsKey(hostTag)){
            return baseUrlMap.get(hostTag);
        }
        return baseUrl;
    }

    public Map<String, String> getBaseUrlMap() {
        return baseUrlMap;
    }

    public GlobalConfig addBaseUrl(String hostTag,String baseUrl){
        if(baseUrlMap == null){
            baseUrlMap = new HashMap<>();
        }
       /* if(baseUrlMap.isEmpty()){
            if(TextUtils.isEmpty(this.baseUrl)){
                setBaseUrl(baseUrl);
            }
        }*/
        baseUrlMap.put(hostTag,baseUrl);
        return this;
    }

    private String key_data = "data";
    private String key_code = "code";
    private String key_msg = "msg";
    public String key_isSuccess = "";//如果不为空,则通过这个key对应的boolean值来判断一个请求是否成功.
    //public boolean isKeyCodeInt = true;//code对应的字段是int还是String
    public String key_extra1 = "";//json外层额外的字段,如果为空就说明没有
    public String key_extra2 = "";
    public String key_extra3 = "";


    public boolean isTreatEmptyDataAsSuccess;

    public GlobalConfig setTreatEmptyDataStrAsSuccess(boolean treatEmptyDataAsSuccess) {
        this.isTreatEmptyDataAsSuccess = treatEmptyDataAsSuccess;
        return this;
    }






    private String userAgent = Tool.getDefalutUserAgent();

    /**
     * 设置useragent,可能需要欺骗服务器什么的
     *
     * @param userAgent
     * @return
     */
    public GlobalConfig setDefaultUserAgent(String userAgent) {
        this.userAgent = userAgent;
        commonHeaders.put(HttpHeaders.HEAD_KEY_USER_AGENT, userAgent);
        return this;
    }

    public String getUserAgent() {
        return userAgent;
    }

    //此处默认值的策略: 设一个比较大的值,让底层的超时时间设置不成为瓶颈.
    // 通过上层的rxjava控制总的超时时间.因为okhttp无法控制系统层面的dns解析的超时时间.
    //现今有calltimeout,但依然推荐使用rxjava来控制整体耗时
    //下方三个超时时间都是在dns解析成功的基础上设置才有意义.
    private int connectTimeout = 15000;//tcp连接的超时时间,单位为ms,默认15s
    private int readTimeout = 60000;//已连接后台时,后台响应的超时时间,单位为ms,默认1min.适用于服务端接口慢的情况(内部阻塞)
    private int writeTimeout = 60000;//已连接后台时,client往sever写数据的超时时间,单位为ms,默认1min(适用于客户端阻塞式的情况)

    /**
     * 设置超时时间
     *
     * @param connectTimeout
     * @return
     */
    public GlobalConfig setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
        return this;
    }

    public GlobalConfig setReadTimeout(int readTimeout) {
        this.readTimeout = readTimeout;
        return this;
    }

    public GlobalConfig setWriteTimeout(int writeTimeout) {
        this.writeTimeout = writeTimeout;
        return this;
    }

    public Consumer<Throwable> getErrorHandler() {
        return errorHandler;
    }

    public GlobalConfig setErrorHandler(Consumer<Throwable> errorHandler) {
        this.errorHandler = errorHandler;
        return this;
    }

    Consumer<Throwable> errorHandler;

    /**
     * 网络链接的超时时间 tcp的配置
     *
     * @return
     */
    public int getConnectTimeout() {
        return connectTimeout;
    }

    /**
     * 下载时的超时时间,多少ms内下载完
     *
     * @return
     */
    public int getReadTimeout() {
        return readTimeout;
    }

    /**
     * 上传时的超时时间,多少ms内上传完
     *
     * @return
     */
    public int getWriteTimeout() {
        return writeTimeout;
    }

    private int retryCount = 0;

    public int getTotalTimeOut() {
        return timeOut;
    }

    public GlobalConfig setTotalTimeOut(int timeOut) {
        this.timeOut = timeOut;
        return this;
    }

    private int timeOut = 10000;//10s

    /**
     * 设置重试次数,默认为0
     */
    public int getRetryCount() {
        return retryCount;
    }

    public GlobalConfig setRetryCount(int retryCount) {
        this.retryCount = retryCount;
        return this;
    }

    public GlobalConfig setIgnoreCertificateVerify(boolean ignoreCertificateVerify) {
        this.ignoreCertificateVerify = ignoreCertificateVerify;
        return this;
    }

    /**
     * https相关设置
     */
    private boolean ignoreCertificateVerify = false;//是否忽略证书校验,也就是信任所有证书,默认关闭,因为会导致安全问题


    //预埋证书
    //private List<String> certificateFiles = new ArrayList<>();


    public boolean isIgnoreCertificateVerify() {
        return ignoreCertificateVerify;
    }

    //private List<String> certificateAsserts= new ArrayList<>();

    @Deprecated  //只给java使用,android使用assert内置
    public GlobalConfig addCrtificateFile(String filePath) {
        SslUtil.addCrtificateFile(filePath);
        return this;
    }

    public GlobalConfig addCrtificateAssert(String fileName) {
        SslUtil.addCrtificateAsserts(fileName);
        return this;
    }

    public GlobalConfig addCrtificateRaw(int rawId) {
        SslUtil.addCrtificateRaws(rawId);
        return this;
    }


    /**
     * log相关设置
     */
    private boolean isOpenLog = false;

    public String getLogTag() {
        return logTag;
    }

    public GlobalConfig setLogTag(String logTag) {
        this.logTag = logTag;
        return this;
    }

    public GlobalConfig setFriendMsgImpl(IFriendlyMsg friendlyMsg) {
        ExceptionFriendlyMsg.init(context,friendlyMsg);
       /* ExceptionFriendlyMsg.init(context, new IFriendlyMsg() {
            Map<String,Integer> errorMsgs = new HashMap<>();
            {
                errorMsgs.put("user.login.89899",R.string.httputl_unlogin_error);
            }
            @Override
            public String toMsg(String code) {
                Integer res = errorMsgs.get(code);
                if(res != null && res != 0){
                    return context.getResources().getString(res);
                }
                return "";

            }
        });*/
        return this;
    }

    private String logTag = "HttpUtil";

    public boolean isOpenLog() {
        return isOpenLog;
    }

    public GlobalConfig openLog(boolean openLog) {
        isOpenLog = openLog;
        return this;
    }


}
