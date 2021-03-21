package com.study.service.config;

import org.apache.curator.CuratorZookeeperClient;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CuratorZookeeperClientConfig {
    @Value("${zookeeper-client-address}")
    private String zkServerIps;

    @Bean("curatorFramework")
    public CuratorFramework init(){
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        CuratorFramework client = CuratorFrameworkFactory.builder()                  // 使用工厂类来建造客户端的实例对象
                .connectString(zkServerIps)                         // 放入zookeeper服务器ip
                .sessionTimeoutMs(15000).retryPolicy(retryPolicy)   // 设定会话时间以及重连策略
                .build();
        return client;
    }
}
