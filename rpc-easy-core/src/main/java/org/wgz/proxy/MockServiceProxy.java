package org.wgz.proxy;

import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

@Slf4j
public class MockServiceProxy implements InvocationHandler {
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Class<?> returnType = method.getReturnType();
        log.info("Mock service invoke {}", method.getName());
        return getDefaultObject(returnType);
    }

    /**
     * 返回 对应返回值类型的默认值
     *
     * @param type
     * @return
     */
    public Object getDefaultObject(Class<?> type) {
        //基本数据类型
        if (type.isPrimitive()) {
            if (type == boolean.class) {
                return true;
            } else if (type == int.class) {
                return 1;
            } else if (type == short.class) {
                return (short) 1;
            } else if (type == double.class || type == long.class) {
                return 1.0;
            }
        }

        //对象类型
        return null;
    }
}
