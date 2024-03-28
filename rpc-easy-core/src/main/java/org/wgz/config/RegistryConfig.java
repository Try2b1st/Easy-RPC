package org.wgz.config;

import lombok.Data;

/**
 * RPC 框架注册中心配置
 */
@Data
public class RegistryConfig {

    /**
     * 注册中心类型
     */
    private String registry = "etcd";

    /**
     * 注册中心注册地址
     */
    private String address = "http://localhost:2380";

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 设置超时时间
     */
    private Long timeout = 10000L;
}
