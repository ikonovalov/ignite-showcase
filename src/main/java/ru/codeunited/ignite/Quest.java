package ru.codeunited.ignite;

import org.apache.ignite.*;
import org.apache.ignite.cache.*;
import org.apache.ignite.cache.affinity.Affinity;
import org.apache.ignite.cache.query.QueryCursor;
import org.apache.ignite.cache.query.ScanQuery;
import org.apache.ignite.cache.query.TextQuery;
import org.apache.ignite.configuration.CacheConfiguration;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.transactions.Transaction;

import javax.cache.Cache;
import java.util.*;
import java.util.stream.IntStream;

import static org.apache.ignite.cache.CacheAtomicityMode.TRANSACTIONAL;
import static org.apache.ignite.cache.CacheMode.PARTITIONED;
import static org.apache.ignite.cache.CacheRebalanceMode.SYNC;
import static org.apache.ignite.cache.CacheWriteSynchronizationMode.FULL_SYNC;

public class Quest {

    private static final String MY_CACHE = "MY_CACHE";

    public static void main(String[] args) {
        // Ignite config
        IgniteConfiguration igniteConfiguration = new IgniteConfiguration();

        // Cache config
        CacheConfiguration<Long, QuestValue> cacheConfiguration = new CacheConfiguration<>(MY_CACHE);
        cacheConfiguration.setAtomicityMode(TRANSACTIONAL);
        cacheConfiguration.setCacheMode(PARTITIONED);
        cacheConfiguration.setRebalanceMode(SYNC);
        cacheConfiguration.setBackups(2);
        cacheConfiguration.setWriteSynchronizationMode(FULL_SYNC);

        igniteConfiguration.setCacheConfiguration(cacheConfiguration);

        // setup query entries
        QueryEntity queryEntity = new QueryEntity();
        queryEntity.setKeyType(Long.class.getName());
        queryEntity.setValueType(QuestValue.class.getName());

        // fields
        LinkedHashMap<String, String> fields = new LinkedHashMap<>();
        fields.put("text", String.class.getName());
        fields.put("desc", String.class.getName());
        queryEntity.setFields(fields);

        // indexes
        Collection<QueryIndex> indexCollection = new ArrayList<>();
        indexCollection.add(new QueryIndex("text"));
        indexCollection.add(new QueryIndex("desc"));
        queryEntity.setIndexes(indexCollection);

        cacheConfiguration.setQueryEntities(Collections.singletonList(queryEntity));

        // Work
        Ignite ignite = Ignition.start(igniteConfiguration);
        final IgniteCache<Long, QuestValue> cache = ignite.getOrCreateCache(MY_CACHE);

        Affinity<Long> affinity = ignite.affinity(MY_CACHE);
        System.out.println("Partition: " + affinity.partition(20L));

        int endExclusive = new Random().nextInt(5000);
        System.out.println("Input range [0, " + endExclusive + "]");
        IntStream.range(0, endExclusive).forEach(
                i -> cache.put((long) i, new QuestValue(i, "text" + i, "desc" + i))
        );

        IgniteTransactions transactions = ignite.transactions();
        try {
            Transaction tx = transactions.txStart();
            cache.put(1L, new QuestValue(1L, "original", "old desc"));
            ignite.compute().affinityRun(MY_CACHE, 1L, () -> {
                if (cache.containsKey(1L)) {
                    cache.put(2L, new QuestValue(2L, "moved", "new desc"));
                }
            });

            tx.rollback();
        } catch (IgniteException ie) {
            ie.printStackTrace();
        } finally {
            System.out.println("=>  #1 " + cache.get(1L));
            System.out.println("=>  #2 " + cache.get(2L));
        }

        // try scan
        long s1 = System.currentTimeMillis();
        ScanQuery<Long, QuestValue> qry = new ScanQuery<>((k,v) -> "text5".equals(v.getText()));
        List<Cache.Entry<Long, QuestValue>> all = cache.query(qry).getAll();
        System.out.println("Scan found " + all.size() + " records in " + (System.currentTimeMillis() - s1) + "ms");
        if (all.size() > 0) {
            System.out.println("\t" + all.get(0));
        }

        // try text query
        long s2 = System.currentTimeMillis();
        TextQuery<Long, QuestValue> txt = new TextQuery<>(QuestValue.class, "%t%");
        try (QueryCursor<Cache.Entry<Long, QuestValue>> cursor = cache.query(txt)) {
            List<Cache.Entry<Long, QuestValue>> all_text = cursor.getAll();
            System.out.println("Text query " + all_text.size() + " records in " + (System.currentTimeMillis() - s2) + "ms");
            if (all_text.size() > 0)
                System.out.println("\t" + all_text.get(0));
        }
    }
}
