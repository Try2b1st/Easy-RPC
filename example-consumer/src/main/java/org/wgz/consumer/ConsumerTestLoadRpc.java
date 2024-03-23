package org.wgz.consumer;

import org.wgz.config.RpcConfig;
import org.wgz.constant.RpcConstant;
import org.wgz.utils.ConfigUtils;

public class ConsumerTestLoadRpc {
    public static void main(String[] args) {
        RpcConfig rpcConfig = ConfigUtils.loadConfig(RpcConfig.class, RpcConstant.DEFAULT_CONFIG_PREFIX);
        System.out.println(rpcConfig.toString());
    }
}
