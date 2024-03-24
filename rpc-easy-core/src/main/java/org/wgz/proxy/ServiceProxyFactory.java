package org.wgz.proxy;

import org.wgz.RpcApplication;
import org.wgz.config.RpcConfig;

import java.lang.reflect.Proxy;

/**
 * 服务代理工厂（用于创建代理对象）
 */
public class ServiceProxyFactory {

    /**
     * 根据服务类获取到代理类
     * <p>
     * public static Object newProxyInstance(ClassLoader loader,
     * Class<?>[] interfaces,
     * InvocationHandler h) {
     *
     * @param serviceClass
     * @param <T>
     * @return
     */
    public static <T> T getProxy(Class<T> serviceClass) {
        if (RpcApplication.getInstance().getMock()) {
            return getMockProxy(serviceClass);
        }
        return (T) Proxy.newProxyInstance(serviceClass.getClassLoader(), new Class[]{serviceClass}, new ServiceProxy());
    }


    public static <T> T getMockProxy(Class<T> mockClass) {
        return (T) Proxy.newProxyInstance(mockClass.getClassLoader(), new Class[]{mockClass}, new MockServiceProxy());
    }
}
