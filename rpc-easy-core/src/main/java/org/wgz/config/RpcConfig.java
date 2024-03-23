package org.wgz.config;

import lombok.Data;

/**
 * Rpc基本配置
 */
@Data
public class RpcConfig {

    /**
     * 名称
     */
    private String name = "rpc-easy";

    /**
     * 版本号
     */
    private String version = "1.0";

    /**
     * 服务器主机名
     */
    private String serverHost = "localhost";

    /**
     * 服务器服务端口号
     */
    private Integer port = 8080;
}
