package com.hss01248.http;

import android.app.Application;

import com.hss01248.http.cache.CacheMode;
import com.hss01248.http.config.DataCodeMsgJsonConfig;
import com.hss01248.http.config.FileDownlodConfig;
import com.hss01248.http.config.LoadingDialogConfig;
import com.hss01248.http.response.DownloadParser;
import com.hss01248.http.utils.HttpHeaders;
import com.hss01248.http.utils.SslUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public Map<String, String> getCommonParams() {
        return commonParams;
    }

    public GlobalConfig addCommonParam(String key, String value) {
        this.commonParams.put(key, value);
        return this;
    }


    private Map<String, String> commonHeaders = new HashMap<>();
    private Map<String, String> commonParams = new HashMap<>();
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
    private int cookieMode = COOKIE_DISK;//默认是会话cookie,不做持久化操作

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
        return this;
    }

    private String baseUrl = "http://www.qxinli.com/";

    public String getBaseUrl() {
        return baseUrl;
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






    private String userAgent = System.getProperty("http.agent");

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

    private int connectTimeout = 15000;//单位为ms,默认15s


    private int readTimeout = 240000;//下载文件的超时时间,单位为ms,默认4min
    private int writeTimeout = 240000;//单位为ms,默认4min

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

    private String logTag = "HttpUtil";

    public boolean isOpenLog() {
        return isOpenLog;
    }

    public GlobalConfig openLog(boolean openLog) {
        isOpenLog = openLog;
        return this;
    }


}
