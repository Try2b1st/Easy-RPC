package org.wgz.model;

import cn.hutool.core.util.StrUtil;
import io.netty.util.internal.StringUtil;
import lombok.Data;

/**
 * 服务的原信息
 */
@Data
public class ServiceMetaInfo {

    /**
     * 服务的名称
     */
    private String serviceName;

    /**
     * 服务的版本
     */
    private String serviceVersion;

    /**
     * 服务域名
     */
    private String serviceHost;

    /**
     * 服务端口号
     */
    private Integer servicePort;

    /**
     * 服务分组
     */
    private String serviceGroup = "default";

    /**
     * 获取服务的键
     *
     * @return
     */
    public String getServiceKey() {
        return String.format("%s:%s", serviceName, serviceVersion);
    }

    /**
     * 获取服务节点的键
     *
     * @return
     */
    public String getServiceNodeKey() {
        return String.format("%s/%s:%s", getServiceKey(), serviceHost, servicePort);
    }


    /**
     * 获取完整的服务请求地址
     *
     * @return
     */
    public String getServiceAddress() {
        if (!StrUtil.contains(serviceHost, "http")) {
            return String.format("http://%s:%s", serviceHost, servicePort);
        }
        return String.format("%s:%s", serviceHost, servicePort);
    }
}
