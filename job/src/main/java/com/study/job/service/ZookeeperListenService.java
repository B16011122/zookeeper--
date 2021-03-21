package com.study.job.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.api.CuratorWatcher;
import org.apache.zookeeper.WatchedEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

import javax.xml.ws.Action;
@Slf4j
@Service
public class ZookeeperListenService   {
    @Autowired
    private CuratorFramework client;



    public void run(String... args) throws Exception {
        client.start();
        String result = watcher("/test");
        System.out.println(result);
    }

    // watcher事件,使用usingWatcher的时候,监听只会触发一次，监听完毕后就销毁
    public String watcher(String path){
        try {
            return new String(client.getData()
                    .usingWatcher(new CuratorWatcher() {
                        @Override
                        public void process(WatchedEvent event) throws Exception {
                            log.info("触发watcher, path:{}", event.getPath());
                        }
                    })
                    .forPath(path));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
