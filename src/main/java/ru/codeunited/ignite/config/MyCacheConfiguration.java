package ru.codeunited.ignite.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.IgniteDataStreamer;
import org.apache.ignite.cache.CacheRebalanceMode;
import org.apache.ignite.cache.CacheWriteSynchronizationMode;
import org.apache.ignite.cache.affinity.rendezvous.RendezvousAffinityFunction;
import org.apache.ignite.cache.query.QueryCursor;
import org.apache.ignite.cache.query.ScanQuery;
import org.apache.ignite.configuration.CacheConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import ru.codeunited.ignite.model.QuestValue;

import javax.annotation.PostConstruct;
import javax.cache.Cache;

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
    public static class CacheAutomation {

        private final Ignite ignite;

        @Value("${ignite.cache.my_cache.preload}")
        private int preload;

        @Value("${ignite.cache.my_cache.rebuild}")
        private boolean rebuild;

        @Autowired
        public CacheAutomation(Ignite ignite) {
            this.ignite = ignite;
        }

        @PostConstruct
        public void preload() {
            if (preload > 0) {
                long loadStart = System.currentTimeMillis();
                log.info("{} Input range [0, {}]", MY_CACHE, preload);

                IgniteDataStreamer<Long, QuestValue> dataStreamer = ignite.dataStreamer(MY_CACHE);
                dataStreamer.allowOverwrite(false); // default

                IntStream.range(0, preload).forEach(
                        i -> dataStreamer.addData((long) i, new QuestValue(i, "text" + i, "desc" + i))
                );
                log.info("{} Load complete in {}ms", MY_CACHE, System.currentTimeMillis() - loadStart);

            } else
                log.debug("{} Preload phase skipped", MY_CACHE);
        }

        @PostConstruct /* Dumb Lucene fix */
        public void rebuild() {
            if (rebuild) {
                log.info("{} Rebuild phase...", MY_CACHE);
                long startPoint = System.currentTimeMillis();
                long records = 0;
                ScanQuery<Long, QuestValue> fullScan = new ScanQuery<>((k, v) -> true);
                IgniteCache<Object, Object> cache = ignite.cache(MY_CACHE);
                try (QueryCursor<Cache.Entry<Long, QuestValue>> cursor = cache.query(fullScan)) {
                    for (Cache.Entry<Long, QuestValue> entry : cursor) {
                        cache.put(entry.getKey(), entry.getValue());
                        records++;
                        if (records % 1000 == 0)
                            log.info(">> put {} records", records);
                    }
                }
                long duration = System.currentTimeMillis() - startPoint;
                log.info("{} was rebuild in {}ms with {} entries", MY_CACHE, duration, records);
            } else {
                log.info("{} Rebuild phase skipped.", MY_CACHE);
            }
        }
    }

}
