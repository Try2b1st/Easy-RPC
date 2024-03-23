package org.wgz.provider;

import org.wgz.RpcApplication;
import org.wgz.common.service.UserService;
import org.wgz.registry.LocalRegistry;
import org.wgz.servicie.impl.VertxHttpService;

public class EasyProviderExample {
    public static void main(String[] args) {
        // RPC 框架初始化
        RpcApplication.init();

        //注册服务
        LocalRegistry.registryService("UserService", UserServiceImpl.class);

        //启动服务器
        VertxHttpService vertxHttpService = new VertxHttpService();
        vertxHttpService.doStart(RpcApplication.getInstance().getPort());
    }
}
