package org.wgz.config;

import lombok.Data;

/**
 * Rpc基本配置
 */
@Data
public class RpcConfig {

    private String name = "rpc-easy";
    private String version = "1.0";
    private String serverHost = "localhost";
    private Integer port = 8080;
}
