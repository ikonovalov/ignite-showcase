package ru.codeunited.ignite.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteDataStreamer;
import org.apache.ignite.cache.CacheRebalanceMode;
import org.apache.ignite.cache.CacheWriteSynchronizationMode;
import org.apache.ignite.cache.affinity.rendezvous.RendezvousAffinityFunction;
import org.apache.ignite.configuration.CacheConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import ru.codeunited.ignite.model.QuestValue;

import javax.annotation.PostConstruct;

import java.util.stream.IntStream;

import static org.apache.ignite.cache.CacheAtomicityMode.ATOMIC;
import static org.apache.ignite.cache.CacheMode.PARTITIONED;

@Configuration
public class MyCacheConfiguration {

    public static final String MY_CACHE = "MY_CACHE";

    @Bean
    public CacheConfiguration<Long, QuestValue> longQuestValueCacheConfiguration() {
        CacheConfiguration<Long, QuestValue> cacheConfiguration = new CacheConfiguration<>(MY_CACHE);
        cacheConfiguration.setAtomicityMode(ATOMIC);
        cacheConfiguration.setCacheMode(PARTITIONED);
        cacheConfiguration.setRebalanceMode(CacheRebalanceMode.ASYNC);
        cacheConfiguration.setBackups(0);
        cacheConfiguration.setWriteSynchronizationMode(CacheWriteSynchronizationMode.PRIMARY_SYNC);
        cacheConfiguration.setAffinity(new RendezvousAffinityFunction(false, 64));
        cacheConfiguration.setIndexedTypes(Long.class, QuestValue.class);
        return cacheConfiguration;
    }

    @Component @Slf4j
    public static class StreamLoader {
        private final Ignite ignite;

        @Autowired
        public StreamLoader(Ignite ignite) {
            this.ignite = ignite;
        }

        @PostConstruct
        public void load() {
            int maxLoad = 20_000;
            long loadStart = System.currentTimeMillis();
            log.info("Input range [0, " + maxLoad + "]");
            IgniteDataStreamer<Long, QuestValue> dataStreamer = ignite.dataStreamer(MY_CACHE);
            dataStreamer.allowOverwrite(false); // default
            IntStream.range(0, maxLoad).forEach(
                    i -> dataStreamer.addData((long) i, new QuestValue(i, "text" + i, "desc" + i))
            );
            log.info("Load complete in " + (System.currentTimeMillis() - loadStart) + "ms");
        }
    }

}
