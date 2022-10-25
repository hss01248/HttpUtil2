# 利用服务器时间校准本地时间

```
* 基于http 响应头的的date UMT格式时间,
* 获取经服务端校准后的本地真实时间，
* 替代System.currentTimeMillis(),免受用户调整手机本地时间的影响. 
* 绝对时间精确到秒
* 计算时间间隔 则精度和SystemClock.elapsedRealtime()以及System.currentTimeMillis()一致
```

# 使用

引入:

```groovy
api 'com.github.hss01248.HttpUtil2:serverclock:3.0.6'
```

使用

```java
// 1 添加到okhttp拦截器
.addInterceptor(new ClockSynchronizerInterceptor().setDebug(true))

//2 获取时间
  ClockSynchronizer.currentTimeMillis()
  ClockSynchronizer.currentTimeMillisGMT()
  
```

# log

```shell
2022-07-22 09:46:41.597 9607-9916/com.xxx.dev V/time: GMT+08:00   08:00
2022-07-22 09:46:41.599 9607-9916/com.xxx.dev V/time: 根据服务端时间校准:2022-07-22 09:46:41, 手机系统设置的时间:2022-07-22 09:46:41
```

