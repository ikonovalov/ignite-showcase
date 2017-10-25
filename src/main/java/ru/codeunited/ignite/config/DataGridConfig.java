package ru.codeunited.ignite.config;

import org.apache.ignite.Ignite;
import org.apache.ignite.Ignition;
import org.apache.ignite.configuration.CacheConfiguration;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class DataGridConfig {

    @Bean
    public IgniteConfiguration igniteConfiguration(List<CacheConfiguration> cacheConfigurations) {
        IgniteConfiguration igniteConfiguration = new IgniteConfiguration();
        igniteConfiguration.setCacheConfiguration(
                cacheConfigurations.toArray(new CacheConfiguration[cacheConfigurations.size()])
        );
        return igniteConfiguration;
    }

    @Bean
    public Ignite ignite(IgniteConfiguration igniteConfiguration) {
        return Ignition.start(igniteConfiguration);
    }
}
