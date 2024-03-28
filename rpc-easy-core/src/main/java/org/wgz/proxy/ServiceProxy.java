package org.wgz.proxy;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import org.wgz.RpcApplication;
import org.wgz.config.RpcConfig;
import org.wgz.constant.RpcConstant;
import org.wgz.model.RpcRequest;
import org.wgz.model.RpcResponse;
import org.wgz.model.ServiceMetaInfo;
import org.wgz.registry.Registry;
import org.wgz.registry.RegistryFactory;
import org.wgz.serializer.Serializer;
import org.wgz.serializer.SerializerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.List;

/**
 * 服务代理
 */
public class ServiceProxy implements InvocationHandler {

    /**
     * 调用代理
     *
     * @param proxy  the proxy instance that the method was invoked on
     * @param method the {@code Method} instance corresponding to
     *               the interface method invoked on the proxy instance.  The declaring
     *               class of the {@code Method} object will be the interface that
     *               the method was declared in, which may be a superinterface of the
     *               proxy interface that the proxy class inherits the method through.
     * @param args   an array of objects containing the values of the
     *               arguments passed in the method invocation on the proxy instance,
     *               or {@code null} if interface method takes no arguments.
     *               Arguments of primitive types are wrapped in instances of the
     *               appropriate primitive wrapper class, such as
     *               {@code java.lang.Integer} or {@code java.lang.Boolean}.
     * @return
     * @throws Throwable
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        //指定序列化器
        Serializer serializer = SerializerFactory.getInstance(RpcApplication.getInstance().getSerializerKey());

        //构造请求
        String serviceName = method.getDeclaringClass().getName();
        RpcRequest rpcRequest = RpcRequest.builder()
                .serviceName(serviceName)
                .methodName(method.getName())
                .parameterTypes(method.getParameterTypes())
                .args(args).build();

        try {
            //序列化
            byte[] bytes = serializer.serialize(rpcRequest);
            byte[] result;

            //从服务中心获取服务提供者请求地址
            RpcConfig rpcConfig = RpcApplication.getInstance();
            Registry registry = RegistryFactory.getInstance(rpcConfig.getRegistryConfig().getRegistry());
            ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
            serviceMetaInfo.setServiceName(serviceName);
            serviceMetaInfo.setServiceVersion(RpcConstant.DEFAULT_SERVICE_VERSION);
            List<ServiceMetaInfo> serviceMetaInfoList = registry.getServiceMetaInfoList(serviceMetaInfo.getServiceKey());
            if(CollUtil.isEmpty(serviceMetaInfoList)){
               throw new RuntimeException("暂无服务地址");
            }

            //目前默认取第一个
            serviceMetaInfo = serviceMetaInfoList.get(0);


            //发起请求
            try (HttpResponse httpResponse = HttpRequest.post(serviceMetaInfo.getServiceAddress())
                    .body(bytes)
                    .execute()) {
                result = httpResponse.bodyBytes();

                //反序列化
                RpcResponse rpcResponse = serializer.deserialize(result, RpcResponse.class);
                return rpcResponse.getData();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
