package org.wgz.utils;

import cn.hutool.core.io.resource.Resource;
import cn.hutool.core.io.resource.ResourceUtil;
import lombok.extern.slf4j.Slf4j;
import org.wgz.serializer.Serializer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * SPI 加载器（支持键值对映射）
 */
@Slf4j
public class SpiLoader {

    /**
     * 存储已经加载的类 （接口 -> (key -> 实现类)）
     */
    private static Map<String, Map<String, Class<?>>> loaderMap = new ConcurrentHashMap<>();

    /**
     * 对象实例缓存（避免重复 new ）类路径 -> 对象实例 单例模式
     */
    private static Map<String, Object> instanceCache = new ConcurrentHashMap<>();

    /**
     * 系统 SPI 目录
     */
    private static final String RPC_SYSTEM_SPI_DIR = "META-INF/rpc/system/";

    /**
     * 用户自定义 SPI 目录
     */
    private static final String RPC_CONSUMER_SPI_DIR = "META-INF/rpc/consumer/";

    /**
     * 扫描路径
     */
    private static final String[] SCAN_DIRS = new String[]{RPC_CONSUMER_SPI_DIR, RPC_SYSTEM_SPI_DIR};

    /**
     * 动态加载的类列表
     */
    private static final List<Class<?>> LOAD_CLASS_LIST = Arrays.asList(Serializer.class);

    /**
     * 加载所有类型
     */
    public void loadAll() {
        log.info("加载全部 SPI ");
        for (Class<?> loadClass : LOAD_CLASS_LIST) {
            load(loadClass);
        }
    }


    /**
     * 加载对象实例
     *
     * @param tClass
     * @param key
     * @param <T>
     * @return
     */
    public static <T> T getInstance(Class<T> tClass, String key) {
        String tClassName = tClass.getName();

        Map<String, Class<?>> keyClassMap = loaderMap.get(tClassName);
        if (keyClassMap == null) {
            throw new RuntimeException(String.format("SpiLoader 未加载 %s 类型", tClassName));
        }

        if (!keyClassMap.containsKey(key)) {
            throw new RuntimeException(String.format("SpiLoader 的 %s 不存在 key= %s 的类型实现类", tClassName, key));
        }

        //获取到加载实例的实现类
        Class<?> implClass = keyClassMap.get(key);
        String implClassName = implClass.getName();
        //从对象实例缓存中加载指定类型的实例
        if (!instanceCache.containsKey(implClassName)) {
            try {
                instanceCache.put(implClassName, implClass.getDeclaredConstructor().newInstance());
            } catch (Exception e) {
                String errorMsg = String.format("%s 实例化失败", implClassName);
                throw new RuntimeException(errorMsg, e);
            }
        }
        return (T) instanceCache.get(implClassName);

    }

    /**
     * 加载某个类型
     *
     * @param loadClass
     * @return
     */
    public static Map<String, Class<?>> load(Class<?> loadClass) {
        log.info("加载类型为 {} 的SPI", loadClass.getName());

        Map<String, Class<?>> keyClassMap = new HashMap<>();

        for (String scanDir : SCAN_DIRS) {
            List<URL> resources = ResourceUtil.getResources(scanDir + loadClass.getName());

            //读取配置文件
            for (URL resource : resources) {
                try {
                    InputStreamReader inputStreamReader = new InputStreamReader(resource.openStream());

                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        String[] keyClassName = line.split("=");
                        if (keyClassName.length > 1) {
                            String key = keyClassName[0];
                            String className = keyClassName[1];

                            Class<?> clazz = Class.forName(className);
                            keyClassMap.put(key, clazz);
                        }
                    }
                } catch (Exception e) {
                    log.info(e.getMessage());
                    log.error("SPI resource load error !", e);
                }
            }
        }
        loaderMap.put(loadClass.getName(), keyClassMap);
        return keyClassMap;
    }


}
