package ru.codeunited.ignite;

import org.apache.ignite.*;
import org.apache.ignite.cache.CacheAtomicityMode;
import org.apache.ignite.cache.affinity.Affinity;
import org.apache.ignite.configuration.CacheConfiguration;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.transactions.Transaction;

public class Quest {

    public static void main(String[] args) {
        IgniteConfiguration igniteConfiguration = new IgniteConfiguration();
        CacheConfiguration<Long, String> cacheConfiguration = new CacheConfiguration<>();
        igniteConfiguration.setCacheConfiguration(cacheConfiguration);
        cacheConfiguration.setAtomicityMode(CacheAtomicityMode.TRANSACTIONAL);
        String my_cache = "MY_CACHE";
        cacheConfiguration.setName(my_cache);

        Ignite ignite = Ignition.start(igniteConfiguration);
        final IgniteCache<Long, String> cache = ignite.getOrCreateCache(my_cache);
        Affinity<Long> affinity = ignite.affinity(my_cache);
        System.out.println("Partition: " + affinity.partition(20L));

        cache.put(1L, "HW");

        IgniteTransactions transactions = ignite.transactions();
        try {
            Transaction tx = transactions.txStart();
            cache.put(1L, "Hello world 1");
            ignite.compute().affinityRun(my_cache, 1L, () -> {
                if (cache.containsKey(1L)) {
                    cache.put(2L, "Hello world 2");
                }
            });

            tx.rollback();
        } catch (IgniteException ie) {
            ie.printStackTrace();
        } finally {
            System.out.println("#1 " + cache.get(1L));
            System.out.println("#2 " + cache.get(2L));
        }
    }
}
