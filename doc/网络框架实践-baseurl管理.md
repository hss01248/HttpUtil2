# baseUrl管理

## 1.一个baseurl时:

globalconfig里设置:后面就不用管了.

```
GlobalConfig setBaseUrl(String url)
```

在构建一个请求时,调用setUrl(String url),内部判断,如果没有http,就自动拼接上baseUrl:

```
public ConfigInfo<T> setUrl(String url) {
        if (url.startsWith("http")) {
            this.url = url;
        } else {
            this.url = GlobalConfig.get().getBaseUrl() + url;
        }
        return this;
    }
```

## 2.多个baseUrl时:

globalconfig初始化时,或者后续随时动态添加或删除:

```
addBaseUrl(String hostTag,String baseUrl)
Map<String, String> getBaseUrlMap()
```

构建请求时,setUrl(String url)是采用默认baseurl,

如果是其他baseUrl,指定对应的hostTag即可:

```
setUrlWithHost(String url,String hostTag)
```

hostTag取名可参考:

```
addBaseUrl("zhihu","https://www.zhihu.com/")
```

