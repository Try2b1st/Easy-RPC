package org.wgz.provider;

import org.wgz.RpcApplication;
import org.wgz.common.service.UserService;
import org.wgz.config.RegistryConfig;
import org.wgz.config.RpcConfig;
import org.wgz.constant.RpcConstant;
import org.wgz.model.ServiceMetaInfo;
import org.wgz.registry.LocalRegistry;
import org.wgz.registry.Registry;
import org.wgz.registry.RegistryFactory;
import org.wgz.servicie.impl.VertxHttpService;

import java.util.concurrent.ExecutionException;

public class EasyProviderExample {
    public static void main(String[] args) {
        // RPC 框架初始化
        RpcApplication.init();

        //注册服务
        String serviceName = UserService.class.getName();
        LocalRegistry.registryService(serviceName, UserServiceImpl.class);

        //注册服务到注册中心
        RpcConfig rpcConfig = RpcApplication.getInstance();
        RegistryConfig registryConfig = rpcConfig.getRegistryConfig();
        Registry registry = RegistryFactory.getInstance(registryConfig.getRegistry());

        ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
        serviceMetaInfo.setServiceName(serviceName);
        serviceMetaInfo.setServiceVersion(RpcConstant.DEFAULT_SERVICE_VERSION);
        serviceMetaInfo.setServiceHost(rpcConfig.getServerHost());
        serviceMetaInfo.setServicePort(rpcConfig.getPort());

        try {
            registry.register(serviceMetaInfo);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        //启动服务器
        VertxHttpService vertxHttpService = new VertxHttpService();
        vertxHttpService.doStart(RpcApplication.getInstance().getPort());
    }
}
