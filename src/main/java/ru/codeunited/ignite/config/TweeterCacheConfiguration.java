package ru.codeunited.ignite.config;

import org.apache.ignite.cache.affinity.AffinityFunction;
import org.apache.ignite.configuration.CacheConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.codeunited.ignite.model.Tweet;

import static org.apache.ignite.cache.CacheAtomicityMode.ATOMIC;
import static org.apache.ignite.cache.CacheMode.PARTITIONED;
import static org.apache.ignite.cache.CacheRebalanceMode.ASYNC;
import static org.apache.ignite.cache.CacheWriteSynchronizationMode.FULL_ASYNC;

@Configuration
public class TweeterCacheConfiguration {

    public static final String CACHE_NAME = "TweeterCache";

    @Bean
    public CacheConfiguration<Long, Tweet> tweeterCacheConfig(AffinityFunction affinityFunction) {
        CacheConfiguration<Long, Tweet> cacheConfiguration = new CacheConfiguration<Long, Tweet>(CACHE_NAME)
                .setAtomicityMode(ATOMIC)
                .setCacheMode(PARTITIONED)
                .setRebalanceMode(ASYNC)
                .setBackups(0)
                .setWriteSynchronizationMode(FULL_ASYNC)
                .setAffinity(affinityFunction);
        return cacheConfiguration;
    }

}
