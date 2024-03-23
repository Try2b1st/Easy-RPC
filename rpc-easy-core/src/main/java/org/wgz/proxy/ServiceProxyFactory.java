package org.wgz.proxy;

import java.lang.reflect.Proxy;

public class ServiceProxyFactory {

    /**
     * 根据服务类获取到代理类
     *
     * public static Object newProxyInstance(ClassLoader loader,
     *                                           Class<?>[] interfaces,
     *                                           InvocationHandler h) {
     *
     * @param serviceClass
     * @return
     * @param <T>
     */
    public static <T> T getProxy(Class<T> serviceClass) {
        return (T) Proxy.newProxyInstance(serviceClass.getClassLoader(), new Class[]{serviceClass}, new ServiceProxy());
    }
}
