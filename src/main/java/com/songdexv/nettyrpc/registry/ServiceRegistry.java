package com.songdexv.nettyrpc.registry;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.songdexv.nettyrpc.properties.ZkRegistryProperties;

/**
 * Created by songdexv on 2018/2/2.
 */
public class ServiceRegistry {
    private static final Logger logger = LoggerFactory.getLogger(ServiceRegistryConfig.class);

    private CuratorFramework client = null;

    private ZkRegistryProperties zkRegistryProperties;

    public ServiceRegistry(ZkRegistryProperties zkRegistryProperties) {
        logger.debug("--------construct ServiceRegistry, using " + zkRegistryProperties.toString());
        this.zkRegistryProperties = zkRegistryProperties;
        ExponentialBackoffRetry retryPolicy = new ExponentialBackoffRetry(1000, 3);
        client = CuratorFrameworkFactory.newClient(zkRegistryProperties.getAddress(), zkRegistryProperties
                .getSessionTimeout(), zkRegistryProperties.getConnectionTimeout(), retryPolicy);
        client.start();
    }

    public void register(String serviceUri) {
        try {
            String path = client.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL_SEQUENTIAL)
                    .forPath(zkRegistryProperties.getDataPath(), serviceUri.getBytes());
            logger.debug("create zookeeper node ({}=>{})", path, serviceUri);
        } catch (Exception e) {
            logger.error("register service to zk error", e);
        }
    }
}
