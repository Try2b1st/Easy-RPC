package org.wgz.utils;

import cn.hutool.core.util.StrUtil;
import cn.hutool.setting.dialect.Props;
import io.netty.util.internal.StringUtil;

/**
 * 和配置相关的工具类
 */
public class ConfigUtils {


    /**
     * 加载配置对象
     *
     * @param tclass
     * @param prefix
     * @param <T>
     * @return
     */
    public static <T> T loadConfig(Class<T> tclass, String prefix) {
        return loadConfig(tclass, prefix, "");
    }


    /**
     * 加载配置对象，支持区分环境
     *
     * @param tClass
     * @param prefix
     * @param environment
     * @param <T>
     * @return
     */
    public static <T> T loadConfig(Class<T> tClass, String prefix, String environment) {
        StringBuilder configFileName = new StringBuilder("application");
        if (StrUtil.isNotBlank(environment)) {
            configFileName.append("-").append(environment);
        }
        configFileName.append(".properties");
        Props props = new Props(configFileName.toString());
        return props.toBean(tClass, prefix);
    }
}
