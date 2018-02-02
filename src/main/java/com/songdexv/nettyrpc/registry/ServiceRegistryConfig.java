package com.songdexv.nettyrpc.registry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.songdexv.nettyrpc.properties.ZkRegistryProperties;

/**
 * Created by songdexv on 2018/2/1.
 */
@Configuration
@EnableConfigurationProperties(ZkRegistryProperties.class)
@ConditionalOnExpression("${registry.zk.enable:false}")
public class ServiceRegistryConfig {
    private static final Logger logger = LoggerFactory.getLogger(ServiceRegistryConfig.class);

    @Autowired
    private ZkRegistryProperties zkRegistryProperties;

    @Bean(name = "serviceRegistry")
    public ServiceRegistry serviceRegistry() {
        logger.debug("------  init ServiceRegistry --------");
        return new ServiceRegistry(zkRegistryProperties);
    }
}
