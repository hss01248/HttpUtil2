# HttpUtil
 http engine for android,power by retrofit ,rxjava and rxcache



# 特性

* 支持callback,callback既可以基于rxjava,也可以基于livedata

* 支持返回Observable,无缝对接rxjava,

* 支持返回livedata

* 6种缓存模式,一行代码完成配置

* 3种cookie模式

* 类data-msg-code的json模式不用定义BaseResult<T>,直接指定各key的字符串即可,自行解析

* 链式调用

* 各种错误类型拆分细致,丰富的回调分支.

* 丰富的debug日志

* 可配置chuck,stecho等抓包工具

* 丰富的下载后处理配置

  

 # todo 
 * 同步返回javabean的方法

# 使用:

## gradle

**Step 1.** Add the JitPack repository to your build file

Add it in your root build.gradle at the end of repositories:

```
    allprojects {
        repositories {
            ...
            maven { url "https://jitpack.io" }
        }
    }
```

**Step 2.** Add the dependency

```
    dependencies {
            compile 'com.github.hss01248.HttpUtil:http:3.0.0'
    }
```



# 全局配置

```
HttpUtil.init(this,true,"http://api.qxinli.com:9005/api/",tool)
                .setDataCodeMsgJsonConfig(DataCodeMsgJsonConfig
                        .newBuilder()
                        .key_data("data")
                        .key_code("code")
                        .key_msg("message")
                        .successJudge(new DataCodeMsgJsonConfig.DataSuccessJudge() {
                            @Override
                            public boolean isResponseSuccess(JSONObject object) {
                                int code = object.optInt("code");
                                return code==0;
                            }
                        })

                        .build())
                .setDefaultLoadingDialog(new LoadingDialogConfig.ILoadingDialog() {
                    @Override
                    public Dialog showLoadingDialog(Context context,String msg) {
                        ProgressDialog dialog =  new ProgressDialog(context);
                        dialog.setContentView(R.layout.toast_layout);
                        dialog.setMessage(msg);
                        dialog.show();
                        return dialog;
                    }
                })
                // .addCrtificateRaw(R.raw.srca)
                //.addCrtificateAssert("srca.cer")
                .setLogTag("okhttp")
                .setCacheMode(CacheMode.NO_CACHE)
                .setCookieMode(GlobalConfig.COOKIE_DISK)
                .setDefaultUserAgent(System.getProperty("http.agent"))
                .setIgnoreCertificateVerify(true)
                //.setReadTimeout(15000)
                .setConnectTimeout(10000)
                //.setWriteTimeout(10000)
                .setTotalTimeOut(15000)
                .setRetryCount(0)
                .setRetryOnConnectionFailure(false)
                .addCommonHeader("clienttype","android");
```



# 结构

## 各种请求方法

直接调用方法即可:

```
.get()
.post()
.put()
.delete()
...

```

其中:

#### 添加请求头

```
.addHeader(String key, String value)
```

#### 添加参数

```
.addParam(String key, Object value)//value不能为null,否则直接拦截掉. value后续会调用tostring方法转成字符串.
或者:
.addParamStr(String paramsStr)//格式: xxx=rrr&iii=888 或者序列化的{} 或者[]
addParamIf(String key, Object value,boolean shouldAdd)//shouldAdd是否添加此参数
//对应服务端spring标识reqired = false的字段,通过这个添加,value为null时才不会被拦截.而是自动过滤掉
addParamOptional(String key,Object value)

//全量方法:
addParamWith(String key, Object value,boolean shouldAdd,boolean isOptional,boolean notAsCacheKey)
```

如果参数是要json化后传输,那么:

```
.postParamsAsJson()
```

## 几大常见的请求响应类型

### 1.string类型

#### 1.1纯string类型: 

* 回调中直接返回一个string

```
HttpUtil.requestString("https://kyfw.12306.cn/otn/regist/init")
                        .callback(new MyNetCallback<ResponseBean<String>>(true,null) {});
                      
```

```

```

#### 1.2普通json类型: 

回调返回一个解析好的Bean

```
HttpUtil.request("version/latestVersion/v1.json",GetCommonJsonBean.class)
                        .responseAsNormalJson()
                        .callback(new MyNetCallback<ResponseBean<GetCommonJsonBean>>(true,this){})
```









#### 1.3类data-msg-code的json模式: 

回调返回data对应的解析好的bean,并且其他字段同时也包装在一个JsonObject中返回.

```
调用以下即可. 
.responseAsDataCodeMsgJson()
```

如果需要自定义data-msg-code的各个key,全局配置和单个请求配置均可.

```
.setDataCodeMsgJsonConfig(DataCodeMsgJsonConfig config)
```



#### 以上两种json模式

* 如果返回的是一个jsonArray,那么调用下方api,回调自动转换为List<T>

```
.setResponseAsJsonArray(boolean responseAsJsonArray) 
```

* 如果在返回为空时也需要判断为请求成功,走成功的回调,那么:

  ```
  .treatEmptyDataAsSuccess()
  ```


### 2.上传

```
.addFile(String key, String path)//服务端一个key接收一个文件
.addFiles(String key, List<String> paths)//服务端一个key接收多个文件
.uploadMultipart()
```

或者

```
.uploadBinary(String filePath)
```

注: 上传的请求,其响应也有可能是上面的string类型中的一种,不冲突.

### 3.下载

```
.setFileDownlodConfig(FileDownlodConfig config)
.download()
```

上传/下载的进度显示直接传入回调即可:

```
setProgressCallback(ProgressCallback callback)
```

#### 丰富的下载后处理:FileDownlodConfig: 

```
public class FileDownlodConfig {

    //下載文件的保存路徑,包括文件名
    public String filePath;
    //默认只有fileDir,如果没有设置文件名,则采用下载文件本身的文件名
    public String fileDir = DownloadParser.mkDefaultDownloadDir();
    public String fileName;
    /**
     * 文件后缀,优先级最低
     * 仅在没有配置filename时,且解析url失败时采用
     */
    public String subffix = "unknown";
    //是否打開,是否讓媒体库扫描,是否隐藏文件夹
    public boolean isOpenAfterSuccess ;//默认不扫描
    public boolean isHideFolder ;
    public boolean isNotifyMediaCenter =true;//媒体文件下载后,默认:通知mediacenter扫描


    //文件校验相关设置(默认不校验)
    public String verifyStr;//为空则校驗文件
    public boolean verfyByMd5OrShar1 ;

    //下载后赋值,用于携带数据
    public String mimeType;
}
```



# 回调/Observer

类:

 [MyNetCallback](https://github.com/hss01248/HttpUtil/blob/c2e03e41c884b50729c938230ee9d90fabeb9de9/http/src/main/java/com/hss01248/http/callback/MyNetCallback.java)

不用每次都蛋疼的if/else判断,需要什么处理,直接复写相关方法即可,与as结合后,敲两三个字母就开出一个分支.

## 基于observable的回调:

```java
.callback(new MyNetCallback<ResponseBean<GetCommonJsonBean>>(true,null) {
    @Override
    public void onSuccess(ResponseBean<GetCommonJsonBean> response) {
        MyLog.i("---from cache-----listener: method:"+response.isFromCache);
    }

    @Override
    public void onError(String msgCanShow) {
        MyLog.e(msgCanShow);
    }
});
```

## 结合livedata:

```java
.callbackByLiveData(this, new MyNetCallback<ResponseBean<List<PostCommonJsonBean>>>() {
    @Override
    public void onSuccess(ResponseBean<List<PostCommonJsonBean>> response) {
        MyLog.json(response);
    }

    @Override
    public void onError(String msgCanShow) {
        MyLog.w(msgCanShow);
        MyToast.error(msgCanShow);
    }
});
```

# 内部设计

请求的所有相关参数和配置由ConfigInfo携带

响应的所有参数和配置由BaseResponseBean<T>携带.

两者均有日志打印,便于debug.

# 与LiveData的结合:

```java
//configInfo提供的api:
public void callbackByLiveData(LifecycleOwner lifecycleOwner,MyNetCallback<ResponseBean<T>> callback){
    callback.info = this;
    asLiveData().observe(lifecycleOwner,new BaseObserver<>(callback));
}

public LiveData<ResponseBean<T>> asLiveData() {
    Observable<ResponseBean<T>> observable =  Runner.asObservable(this);
    observable =  observable.onErrorResumeNext(new Function<Throwable, ObservableSource<? extends ResponseBean<T>>>() {
        @Override
        public ObservableSource<? extends ResponseBean<T>> apply(@NonNull Throwable throwable) throws Exception {
            ResponseBean<T> bean = new ResponseBean();
            bean.errorInfo = throwable;
            bean.success = false;
            if(throwable != null){
                bean.code = throwable.getClass().getSimpleName();
                bean.msg = throwable.getMessage();
            }
            return Observable.just(bean);
        }
    }).observeOn(AndroidSchedulers.mainThread());
     return   LiveDataReactiveStreams.fromPublisher(observable.toFlowable(BackpressureStrategy.LATEST));
}
```

# loadingdialog:

在构造回调时传入:

```
MyNetCallback(boolean showLoadingDialog,@Nullable Object tagForCancel)

或者:
MyNetCallback(LoadingDialogConfig dialogConfig,@Nullable Object tagForCancel)
```

也可以全局配置loadingdialog的样式:

在HttpUtil.init之后传入:

```
.setDefaultLoadingDialog(new LoadingDialogConfig.ILoadingDialog() {
                    @Override
                    public Dialog showLoadingDialog(Context context,String msg) {
                        ProgressDialog dialog =  new ProgressDialog(context);
                        dialog.setContentView(R.layout.toast_layout);
                        dialog.setMessage(msg);
                        dialog.show();
                        return dialog;
                    }
                })
```

#### LoadingDialogConfig可配置项有:'

```
public class LoadingDialogConfig {
    private Dialog dialog;
    private int stringResId;
    private String msg = "loading...";
    private Activity activity;
    private boolean showProgress;
    private boolean cancelable;

```



# 请求的取消

一种方式是将disposiable与tag映射后保存起来,后续取消tag(一般在activity的ondestory里)即可调用disposiable.dispose(),此方法在retrofit的实现中,可以直接关闭未完成的socket,及时性很强.

另一种方式是与rxLifecycle结合,也具有即时性.

此库采用的是前一种方法.

# 缓存模式

>  缓存的几种类型参考的是okgo,缓存的实现使用的是Rxcache.

注意: 缓存只针对上面的string类型,不对上传或者下载处理缓存逻辑.

得益于[Rxcache](https://github.com/z-chu/RxCache)的良好封装,实现缓存时十分便捷,仅添加了如下代码:

```
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
```

### 使用时更加便捷:

```
.setCacheMode(int cacheMode)
```

#### 缓存模式有:

```
//缓存策略,分类参考:https://github.com/jeasonlzy/okhttp-OkGo
    NO_CACHE = 1;//不使用缓存,该模式下,cacheKey,cacheMaxAge 参数均无效
    DEFAULT = 2;//完全按照HTTP协议的默认缓存规则，例如有304响应头时缓存。
    REQUEST_FAILED_READ_CACHE = 3;//先请求网络，如果请求网络失败，则读取缓存，如果读取缓存失败，本次请求失败。成功或失败的回调只有一次
    IF_NONE_CACHE_REQUEST = 4;//优先使用缓存,如果缓存不存在才请求网络,成功或失败的回调只有一次
    FIRST_CACHE_THEN_REQUEST = 5;//先使用缓存，不管是否存在，仍然请求网络,可能导致两次成功的回调或一次失败的回调.
    ONLY_CACHE = 6;//只读取缓存,不请求网络
```



# 代码示例:

## 请求string

```
//测试自签名/未被android系统承认的的https
                HttpUtil.requestString("https://kyfw.12306.cn/otn/regist/init")
                        .setIgnoreCer(true)
                        .callback(new MyNetCallback<ResponseBean<String>>(true,null) {
                            @Override
                            public void onSuccess(ResponseBean<String> response) {
                                MyLog.i(response.bean);
                            }

                            @Override
                            public void onError(String msgCanShow) {
                                MyLog.e(msgCanShow);
                            }
                        });
```



### post请求普通json,

返回一个jsonArray:

```
HttpUtil.requestAsJsonArray("article/getArticleCommentList/v1.json",PostCommonJsonBean.class)
                        .addParam("pageSize","30")
                        .addParam("articleId","1738")
                        //.setCacheMode(CacheStrategy.FIRST_CACHE_THEN_REQUEST)
                        .addParam("pageIndex","1")
                        .post()
                        .responseAsNormalJson()
                        .asObservable()
                        .subscribe(new BaseSubscriber<ResponseBean<List<PostCommonJsonBean>>>(true,null) {
                            @Override
                            public void onSuccess(ResponseBean<List<PostCommonJsonBean>> response) {

                            }

                            @Override
                            public void onError(String msgCanShow) {
                                MyLog.e(msgCanShow);
                            }
                        });
```

### 请求一个类data-code-msg的json,单独配置这几个key:

```
HttpUtil.request("http://japi.juhe.cn/joke/content/list.from",GetStandardJsonBean.class)
                        .addParam("sort","desc")
                        .addParam("page","1")
                        .addParam("pagesize","4")
                        .addParam("time",System.currentTimeMillis()/1000+"")
                        .addParam("key","fuck you")
                        .setDataCodeMsgJsonConfig(DataCodeMsgJsonConfig
                                .newBuilder()
                                .key_code("error_code")
                                .key_data("result")
                                .key_msg("reason")
                                .key_extra1("resultcode")
                                .successJudge(new DataCodeMsgJsonConfig.DataSuccessJudge() {
                                    @Override
                                    public boolean isResponseSuccess(JSONObject object) {
                                        int code = object.optInt("error_code");
                                        return code ==200;
                                    }
                                })
                                .build()
                        )
                        //.setCacheMode(CacheStrategy.FIRST_CACHE_THEN_REQUEST)
                        .callback(new MyNetCallback<ResponseBean<GetStandardJsonBean>>(true,null) {
                            @Override
                            public void onSuccess(ResponseBean<GetStandardJsonBean> response) {
                                MyLog.json(MyJson.toJsonStr(response.bean));
                            }

                            @Override
                            public void onError(String msgCanShow) {
                                MyLog.e(msgCanShow);
                            }
                        });
```

### 下载:

```
String url2 = "http://www.qxinli.com/download/qxinli.apk";
                        HttpUtil.download(url2)
                                .setFileDownlodConfig(
                                        FileDownlodConfig.newBuilder()
                                        .verifyBySha1("76DAB206AE43FB81A15E9E54CAC87EA94BB5B384")
                                        .isOpenAfterSuccess(true)
                                        .build())
                                .callback(new MyNetCallback<ResponseBean<FileDownlodConfig>>() {
                                    @Override
                                    public void onSuccess(ResponseBean<FileDownlodConfig> response) {
                                        MyLog.i("path:"+response.bean.filePath);
                                    }


                                    @Override
                                    public void onError(String msgCanShow) {
                                        MyLog.e(msgCanShow);
                                    }
                                });
```

### 上传:

```
HttpUtil.request("http://192.168.108.102:8080/uploadImgs",String.class)
                        .uploadMultipart()
                        .responseAsString()
                        .addFile("file1","/storage/emulated/0/Download/httpdemo/qxinli.apk")
                        .addFile("file2","/storage/emulated/0/Download/httpdemo/qxinli2.apk")
                        .addParam("name","898767hjk")
                        .callback(new MyNetCallback<ResponseBean<String>>() {
                            @Override
                            public void onSuccess(ResponseBean<String> response) {
                                MyLog.i(response.bean);
                            }

                            @Override
                            public void onError(String msgCanShow) {
                                MyLog.e(msgCanShow);
                            }
                        });
```





### 单张图片上传的封装:

```
public static io.reactivex.Observable<ResponseBean<S3Info>> uploadSingleImg(String type, final String filePath, ProgressCallback progressCallback){
        String imageType = IMAGE_JPEG;
        //todo 预先压缩
       return HttpUtil.requestAsJsonArray(getUploadTokenPath,S3Info.class)
                .get()
                .addParam("type", type)
                .addParam("contentType", IMAGE_JPEG)
                .addParam("cnt","1")
                .asObservable()
                .flatMap(new Function<ResponseBean<List<S3Info>>, ObservableSource<ResponseBean<S3Info>>>() {
                    @Override
                    public ObservableSource<ResponseBean<S3Info>> apply(ResponseBean<List<S3Info>> bean) throws Exception {

                        S3Info info = bean.bean.get(0);
                        //这个S3Info怎么传递出去?
                        return HttpUtil.request(info.getUrl(),S3Info.class)
                                .setExtraFromOut(info)
                                .uploadBinary(filePath)
                                .put()
                                .setProgressCallback(progressCallback)
                                .treatEmptyDataAsSuccess()
                                .responseAsString()
                                .asObservable();
                    }
                });
    }
```



### 多张图片上传的封装:

```
public static io.reactivex.Observable<ResponseBean<S3Info>> uploadImgs(String type, final List<String> filePaths){
        final List<S3Info> infos = new ArrayList<>();
        io.reactivex.Observable<ResponseBean<S3Info>> observable =
                HttpUtil.requestAsJsonArray(getUploadTokenPath,S3Info.class)
                .get()
                .addParam("type", type)
                .addParam("contentType", IMAGE_JPEG)
                .addParam("cnt",filePaths.size())
                .asObservable()
                .flatMap(new Function<ResponseBean<List<S3Info>>, ObservableSource<ResponseBean<S3Info>>>() {
                    @Override
                    public ObservableSource<ResponseBean<S3Info>> apply(ResponseBean<List<S3Info>> bean) throws Exception {

                        infos.addAll(bean.bean);
                        List<io.reactivex.ObservableSource<ResponseBean<S3Info>>> observables = new ArrayList<>();
                        for(int i = 0; i< bean.bean.size(); i++){
                            S3Info info = bean.bean.get(i);
                            String filePath = filePaths.get(i);
                            io.reactivex.Observable<ResponseBean<S3Info>> observable =
                                    HttpUtil.request(info.getUrl(),S3Info.class)
                                            .uploadBinary(filePath)
                                            .put()
                                            .setExtraFromOut(info)
                                            .responseAsString()
                                            .treatEmptyDataAsSuccess()
                                            .asObservable();
                            observables.add(observable);
                        }
                        return io.reactivex.Observable.merge(observables);
                    }
                });

        return observable;
    }
```

# 彩蛋

不断更新的开发/调试工具包:[TestTools](https://github.com/hss01248/TestTools)

# metrics

![image-20200723145617123](https://cdn.jsdelivr.net/gh/hss01248/picbed@master/uPic/image-20200723145617123.png)



# https的相关兼容配置

## Android7.0以上抓包

```xml
<application
		android:networkSecurityConfig="@xml/network_security_config_httputil">
```

## Android5以下访问不支持TLS1.0的服务端时握手失败

使用TLSCompactSocketFactory来作为SSLFactory构建SSLContext.

框架内已实现.

# 请求体的gzip压缩

非http协议,需要和后端一同实现. 对文本内容可压缩50%-80%不等.

### 客户端:

增加GzipRequestInterceptor,

且指定哪些请求使用gzip压缩请求体(白名单):

```
builder.addInterceptor(new GzipRequestInterceptor());(默认未添加,需手动添加)

GzipRequestInterceptor.useGzip(path)
```

### 后台

spring boot:  增加filter,对content-encoding=gzip的进行解压缩

可参考:https://blog.csdn.net/ifwinds/article/details/97243892



# thanks

[Rxcache](https://github.com/z-chu/RxCache)

[retrofit](https://github.com/square/retrofit)

[okhttp-OkGo](https://github.com/jeasonlzy/okhttp-OkGo)

[RxEasyHttp](https://github.com/zhou-you/RxEasyHttp)
