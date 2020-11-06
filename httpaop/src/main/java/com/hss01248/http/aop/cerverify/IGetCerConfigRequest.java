package com.hss01248.http.aop.cerverify;

import java.util.Map;

public interface IGetCerConfigRequest {

    /**
     * 拉取证书锁定的配置的请求
     * 需要同步实现
     * @return 返回域名-公钥sha256指纹/证书sha256指纹  的键值对
     *
     * 初始化时调用一次,
     * 证书错误时调用一次
     */
    Map<String, String> requestConfig();

    /**
     * 默认配置
     * @return
     */
    Map<String, String> defaultConfig();
}
