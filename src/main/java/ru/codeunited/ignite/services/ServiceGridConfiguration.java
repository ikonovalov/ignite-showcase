package ru.codeunited.ignite.services;

import lombok.extern.slf4j.Slf4j;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteServices;
import org.apache.ignite.cluster.ClusterGroup;
import org.apache.ignite.services.Service;
import org.apache.ignite.services.ServiceConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.codeunited.ignite.services.impl.NodeServiceImpl;
import ru.codeunited.ignite.services.impl.QuestServiceDistributedImpl;

@Configuration
@Slf4j
public class ServiceGridConfiguration {

    private final Ignite ignite;

    @Autowired
    public ServiceGridConfiguration(Ignite ignite) {
        this.ignite = ignite;
    }

    @Bean /* just as a trigger */
    public ServiceConfiguration questServiceConfiguration(ClusterGroupSelectorStrategy clusterGroupSelectorStrategy) {
        // setup quest service
        ServiceConfiguration serviceCfg = new ServiceConfiguration();
        Service questService = new QuestServiceDistributedImpl();
        serviceCfg.setService(questService);
        serviceCfg.setName("quest-service");
        serviceCfg.setMaxPerNodeCount(1);

        deploy(ignite, clusterGroupSelectorStrategy, serviceCfg);

        return serviceCfg;
    }

    @Bean
    public ServiceConfiguration nodeServiceConfiguration(ClusterGroupSelectorStrategy clusterGroupSelectorStrategy) {
        // setup quest service
        ServiceConfiguration serviceCfg = new ServiceConfiguration();
        Service questService = new NodeServiceImpl();
        serviceCfg.setService(questService);
        serviceCfg.setName("node-service");
        serviceCfg.setMaxPerNodeCount(1);

        deploy(ignite, clusterGroupSelectorStrategy, serviceCfg);

        return serviceCfg;
    }

    private void deploy(Ignite ignite, ClusterGroupSelectorStrategy clusterGroupSelectorStrategy, ServiceConfiguration serviceCfg) {
        ClusterGroup clusterGroup = clusterGroupSelectorStrategy.select();
        IgniteServices services = ignite.services(clusterGroup);
        services.deploy(serviceCfg);
        log.info("{} deploy complete", serviceCfg.getName());
    }

}
