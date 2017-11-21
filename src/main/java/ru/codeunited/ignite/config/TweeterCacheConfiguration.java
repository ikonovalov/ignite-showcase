package ru.codeunited.ignite.config;

import org.apache.ignite.cache.CacheRebalanceMode;
import org.apache.ignite.cache.CacheWriteSynchronizationMode;
import org.apache.ignite.cache.affinity.AffinityFunction;
import org.apache.ignite.configuration.CacheConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.codeunited.ignite.model.QuestValue;
import ru.codeunited.ignite.model.Tweet;

import static org.apache.ignite.cache.CacheAtomicityMode.ATOMIC;
import static org.apache.ignite.cache.CacheMode.PARTITIONED;

@Configuration
public class TweeterCacheConfiguration {

    public static final String CACHE_NAME = "TweeterCache";

    @Bean
    public CacheConfiguration<Long, Tweet> longTweeterCacheConfiguration(AffinityFunction affinityFunction) {
        CacheConfiguration<Long, Tweet> cacheConfiguration = new CacheConfiguration<>(CACHE_NAME);
        cacheConfiguration.setAtomicityMode(ATOMIC);
        cacheConfiguration.setCacheMode(PARTITIONED);
        cacheConfiguration.setRebalanceMode(CacheRebalanceMode.ASYNC);
        cacheConfiguration.setBackups(0);
        cacheConfiguration.setWriteSynchronizationMode(CacheWriteSynchronizationMode.FULL_ASYNC);
        cacheConfiguration.setAffinity(affinityFunction);
        return cacheConfiguration;
    }

}
