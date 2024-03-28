package org.wgz.registry;

import org.wgz.config.RegistryConfig;
import org.wgz.model.ServiceMetaInfo;

import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * 注册中心
 */
public interface Registry {

    /**
     * 初始化
     *
     * @param registryConfig
     */
    void init(RegistryConfig registryConfig);


    /**
     * 注册服务（服务端）
     *
     * @param serviceMetaInfo
     */
    void register(ServiceMetaInfo serviceMetaInfo) throws ExecutionException, InterruptedException;


    /**
     * 注销服务（服务端）
     *
     * @param serviceMetaInfo
     */
    void unRegister(ServiceMetaInfo serviceMetaInfo);


    /**
     * 服务发现 - 获取某个服务的所有节点（消费端）
     *
     * @return
     */
    List<ServiceMetaInfo> getServiceMetaInfoList(String serviceKey) ;

    /**
     * 服务销毁
     */
    void destroy();
}
