# 关于dns解析的坑:

由于okhttp对dns的解析是直接调用java底层代码,所以会有两个问题:



# 1.超时时间

okhttp有三个超时时间设置,但无法设置dns的超时时间,所以,最好是通过rxjava的超时时间来控制:

```
	//此处默认值的策略: 设一个比较大的值,让底层的超时时间设置不成为瓶颈.
    // 通过上层的rxjava控制总的超时时间.因为okhttp无法控制系统层面的dns解析的超时时间.
    //下方三个超时时间都是在dns解析成功的基础上设置才有意义.
    private int connectTimeout = 15000;//tcp连接的超时时间,单位为ms,默认15s
    private int readTimeout = 60000;//已连接后台时,后台响应的超时时间,单位为ms,默认1min.适用于服务端接口慢的情况(内部阻塞)
    private int writeTimeout = 60000;//已连接后台时,client往sever写数据的超时时间,单位为ms,默认1min(适用于客户端阻塞式的情况)
```





# 2.exception

https://github.com/square/okhttp/issues/3477

okhttp层没有对dns解析的exception进行处理,所以会引起crash,需要我们写一个拦截器进行转换:

```
public class OkHttpOutCrashInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        try {
            return chain.proceed(chain.request());
        } catch (Throwable e) {
            if (e instanceof IOException) {
                throw e;
            } else {
                Tool.logw(e.getMessage());
                throw new IOException(e);
            }
        }
    }
}
```



