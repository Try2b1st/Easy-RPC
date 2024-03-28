package org.wgz.registry;

import cn.hutool.json.JSONUtil;
import io.etcd.jetcd.*;
import io.etcd.jetcd.options.GetOption;
import io.etcd.jetcd.options.PutOption;
import org.wgz.config.RegistryConfig;
import org.wgz.model.ServiceMetaInfo;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;


public class EtcdRegistry implements Registry {

    private Client client;

    private KV kvClient;

    private final static String ETCD_ROOT_PATH = "/root/";

    @Override
    public void init(RegistryConfig registryConfig) {
        client = Client.builder()
                .endpoints(registryConfig.getAddress())
                .connectTimeout(Duration.ofMillis(registryConfig.getTimeout()))
                .build();
        kvClient = client.getKVClient();
    }

    @Override
    public void register(ServiceMetaInfo serviceMetaInfo) throws ExecutionException, InterruptedException {
        //创建Lease客户端
        Lease leaseClient = client.getLeaseClient();

        //设置30秒的租约
        long leaseId = leaseClient.grant(30).get().getID();

        //处理服务原信息
        String registryKey = ETCD_ROOT_PATH + serviceMetaInfo.getServiceNodeKey();
        ByteSequence key = ByteSequence.from(registryKey, StandardCharsets.UTF_8);
        ByteSequence value = ByteSequence.from(JSONUtil.toJsonStr(serviceMetaInfo), StandardCharsets.UTF_8);

        //KV和租约关联，并设置过期时间
        PutOption putOption = PutOption.builder().withLeaseId(leaseId).build();
        kvClient.put(key, value, putOption);
    }

    @Override
    public void unRegister(ServiceMetaInfo serviceMetaInfo) {
        kvClient.delete(
                ByteSequence.from(
                        ETCD_ROOT_PATH + serviceMetaInfo.getServiceNodeKey(), StandardCharsets.UTF_8));
    }

    @Override
    public List<ServiceMetaInfo> getServiceMetaInfoList(String serviceKey) {
        String searchPrefix = ETCD_ROOT_PATH + serviceKey + "/";

        //设置支持前缀查询
        GetOption getOption = GetOption.builder().isPrefix(true).build();
        try {
            List<KeyValue> kvs = kvClient
                    .get(ByteSequence.from(searchPrefix, StandardCharsets.UTF_8), getOption)
                    .get()
                    .getKvs();

            //解析服务
            return kvs.stream().map(
                    keyValue -> {
                        String value = keyValue.getValue().toString(StandardCharsets.UTF_8);
                        return JSONUtil.toBean(value,ServiceMetaInfo.class);
                    }
            ).collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("获取服务列表失败：" + e);
        }


    }

    @Override
    public void destroy() {
        System.out.println("当前节点下线");

        if(kvClient != null){
            kvClient.close();
        }
        if(client != null){
            client.close();
        }
    }
}
