package ru.codeunited.ignite.config;

import org.apache.ignite.cache.affinity.AffinityFunction;
import org.apache.ignite.cache.affinity.rendezvous.RendezvousAffinityFunction;
import org.apache.ignite.configuration.CacheConfiguration;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.configuration.PersistentStoreConfiguration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class DataGridConfiguration {

    @Bean
    public IgniteConfiguration igniteConfiguration(
            List<CacheConfiguration> cacheConfigurations,
            PersistentStoreConfiguration persistentStoreConfiguration
    ) {

        return new IgniteConfiguration()
                .setCacheConfiguration(
                        cacheConfigurations.toArray(new CacheConfiguration[cacheConfigurations.size()])
                )
                .setPersistentStoreConfiguration(persistentStoreConfiguration);
    }

    @Bean
    public AffinityFunction affinityFunction(
            @Value("${ignite.partition.count}") int partitionCount
    ) {
        return new RendezvousAffinityFunction(false, partitionCount);
    }


    @Bean
    public PersistentStoreConfiguration persistentStoreConfiguration() {
        PersistentStoreConfiguration configuration = new PersistentStoreConfiguration();
        configuration.setMetricsEnabled(true);

        return configuration;
    }
}
