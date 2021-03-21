package com.study.job.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.TreeCache;
import org.apache.curator.framework.recipes.cache.TreeCacheEvent;
import org.apache.curator.framework.recipes.cache.TreeCacheListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

import java.net.URLDecoder;
import java.util.Arrays;

@Slf4j
@Service
public class TreeCacheService implements CommandLineRunner {
    @Autowired
    private CuratorFramework client;

    public void treeCacheListen(CuratorFramework client, String path) throws Exception {
        TreeCache treeCache = new TreeCache(client, path);
        treeCache.getListenable().addListener(new TreeCacheListener() {
            @Override
            public void childEvent(CuratorFramework client, TreeCacheEvent event) throws Exception {
                ChildData eventData = event.getData();
                String curPath =eventData.getPath();
                System.out.println("curPath: "+curPath);
                switch (event.getType()){
                    case NODE_ADDED:
                        log.info("Node add" + curPath);
                        break;
                    case NODE_UPDATED:
                        log.info("Node update" + curPath + " --" + new String(eventData.getData()));
                        break;
                    case NODE_REMOVED:
                        log.info("Node remove " + curPath);
                        break;
                }
            }
        });
        treeCache.start();
    }

    @Override
    public void run(String... args) throws Exception {
        client.start();
        treeCacheListen(client,"/test");
    }
}
