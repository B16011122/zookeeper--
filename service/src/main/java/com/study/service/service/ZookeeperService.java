package com.study.service.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.api.CuratorWatcher;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.data.Stat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.concurrent.CountDownLatch;

@Slf4j
@Service
public class ZookeeperService implements CommandLineRunner {
    @Autowired
    private CuratorFramework client;


    public void init() throws Exception {
        client.start();
        String node = createNode("/test/t3", "123", CreateMode.PERSISTENT);
        System.out.println(node);
        String path = "/test";
        System.out.println("-----------------");
        System.out.println(updateData(8,path,"7894").toString());
        List<String> list = client.getChildren().forPath("/");
        byte[] bytes = client.getData().storingStatIn(new Stat()).forPath(path);
        System.out.println(new String(bytes));
        for (String s : list) {
            System.out.println(s);
        }
        System.out.println("---------------------");
    }

    public String createNode(String path, String value, CreateMode mode) {
        if (client == null) {
            System.err.println("无zookeeper连接");
        }
        try {
            if (client.checkExists().forPath(path) != null) {
                return null;
            }
            String node = client.create()
                    .creatingParentsIfNeeded()
                    .withMode(mode)
                    .forPath(path, value.getBytes());
            return node;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Stat updateData(int version,String path, String value){
        try {
            return client.setData().withVersion(version).forPath(path,value.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Void deleteData(int version, String path){
        try {
            return client.delete()
                    .guaranteed() // 保障机制，若未删除成功，只要会话有效会在后台一直尝试删除
                    .deletingChildrenIfNeeded() // 若当前节点包含子节点，子节点也删除
                    .withVersion(version)
                    .forPath(path);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
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

    @Override
    public void run(String... args) throws Exception {
        Thread.sleep(3000);
        init();
    }
}
