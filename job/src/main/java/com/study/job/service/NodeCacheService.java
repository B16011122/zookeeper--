package com.study.job.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.NodeCache;
import org.apache.curator.framework.recipes.cache.NodeCacheListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;
@Slf4j
@Service
public class NodeCacheService   {
    @Autowired
    private CuratorFramework client;


    public void run(String... args) throws Exception {
        client.start();
        nodeCacheListen(client,"/test");
    }

    public void nodeCacheListen(CuratorFramework client, String path) throws Exception {
        NodeCache nodeCache = new NodeCache(client,path);
        nodeCache.start(true);
        if (nodeCache.getCurrentData() != null){
            log.info("节点初始化数据为:{}", new String(nodeCache.getCurrentData().getData()));
        }else {
            log.warn("节点初始化数据为空");
        }

        nodeCache.getListenable().addListener(new NodeCacheListener() {
            @Override
            public void nodeChanged() throws Exception {
                if (nodeCache.getCurrentData() != null) {
                    String re = new String(nodeCache.getCurrentData().getData());
                    log.info("节点路径:{}, 节点数据:{}", nodeCache.getCurrentData().getPath(), re);
                } else {
                    log.warn("当前节点被删除了");
                }
            }
        });
    }
}
