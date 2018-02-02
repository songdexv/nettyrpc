package com.songdexv.nettyrpc.registry;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.songdexv.nettyrpc.client.ConnectionManager;
import com.songdexv.nettyrpc.properties.ZkRegistryProperties;

/**
 * Created by songdexv on 2018/2/1.
 */
public class ServiceDiscovery {
    private static final Logger logger = LoggerFactory.getLogger(ServiceDiscovery.class);

    private CuratorFramework client = null;

    private ZkRegistryProperties zkRegistryProperties;

    private volatile List<String> serviceUriList = new ArrayList<>();

    public ServiceDiscovery(ZkRegistryProperties zkRegistryProperties) {
        logger.debug("--------construct ServiceDiscovery, using " + zkRegistryProperties.toString());
        this.zkRegistryProperties = zkRegistryProperties;
        ExponentialBackoffRetry retryPolicy = new ExponentialBackoffRetry(1000, 3);
        client = CuratorFrameworkFactory.newClient(zkRegistryProperties.getAddress(), zkRegistryProperties
                .getSessionTimeout(), zkRegistryProperties.getConnectionTimeout(), retryPolicy);
        client.start();
        PathChildrenCache cache = new PathChildrenCache(client, zkRegistryProperties.getDataPath(), true);
        cache.getListenable().addListener(new PathChildrenCacheListener() {
            @Override
            public void childEvent(CuratorFramework curatorFramework, PathChildrenCacheEvent pathChildrenCacheEvent)
                    throws Exception {
                logger.info(zkRegistryProperties.getDataPath() + " child nodes changed");
                List<String> dataList = new ArrayList<>();
                List<String> nodeList = curatorFramework.getChildren().forPath(zkRegistryProperties.getDataPath());
                for (String node : nodeList) {
                    dataList.add(new String(curatorFramework.getData().forPath(zkRegistryProperties.getDataPath() +
                            "/" + node)));
                }
                serviceUriList = dataList;
                ConnectionManager.getInstance().updateConnectedServer(serviceUriList);
            }
        });
        try {
            cache.start();
            List<String> nodeList = client.getChildren().forPath(zkRegistryProperties.getDataPath());
            for (String node : nodeList) {
                serviceUriList.add(new String(client.getData().forPath(zkRegistryProperties.getDataPath() +
                        "/" + node)));
            }
        } catch (Exception e) {
            logger.error("start PathChildrenCache error", e);
        }
    }

    public String discover() {
        String serviceUri = null;
        if (serviceUriList.size() > 0) {
            if (serviceUriList.size() == 1) {
                serviceUri = serviceUriList.get(0);
            } else {
                serviceUri = serviceUriList.get(ThreadLocalRandom.current().nextInt(serviceUriList.size()));
            }
        }
        return serviceUri;
    }

    public void stop() {
        client.close();
    }
}
