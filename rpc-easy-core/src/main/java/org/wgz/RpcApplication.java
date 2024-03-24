package org.wgz;

import lombok.extern.slf4j.Slf4j;
import org.wgz.config.RpcConfig;
import org.wgz.constant.RpcConstant;
import org.wgz.utils.ConfigUtils;

/**
 * RPC 框架应用
 * 相当于holder，存放了项目全局要用到变量，采用饿汉式，使用双检锁实现单例模式
 */
@Slf4j
public class RpcApplication {

    private static volatile RpcConfig rpcConfig;


    /**
     * 框架初始化，支持自定义配置
     *
     * @param newRpcConfig
     */
    public static void init(RpcConfig newRpcConfig) {
        rpcConfig = newRpcConfig;
        log.info("rpc init config = {}", newRpcConfig.toString());
    }


    public static void init() {
        RpcConfig newRpcConfig;

        try {
            //加载配置文件，返回配置对象
            newRpcConfig = ConfigUtils.loadConfig(RpcConfig.class, RpcConstant.DEFAULT_CONFIG_PREFIX);
        } catch (Exception e) {
            log.error(e.toString());
            //加载失败，采用默认值
            newRpcConfig = new RpcConfig();
        }
        init(newRpcConfig);
    }

    /**
     * 双检查锁实现单例模式
     * 整个下可以用 RpcConfig rpcConfig = RpcApplication.getInstance; 来获取RpcConfig对像实例
     *
     * @return
     */
    public static RpcConfig getInstance(){
        if(rpcConfig == null){
            synchronized (RpcApplication.class){
                if(rpcConfig == null){
                    init();
                }
            }
        }
        return rpcConfig;
    }

}
