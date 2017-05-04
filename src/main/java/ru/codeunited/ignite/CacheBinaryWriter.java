package ru.codeunited.ignite;

import org.apache.ignite.*;
import org.apache.ignite.binary.BinaryObject;
import org.apache.ignite.binary.BinaryObjectBuilder;
import org.apache.ignite.cache.CachePeekMode;
import org.apache.ignite.cluster.ClusterGroup;
import org.apache.ignite.events.CacheEvent;
import org.apache.ignite.events.EventType;
import org.apache.ignite.internal.binary.builder.BinaryObjectBuilderImpl;
import org.apache.ignite.lang.IgniteBiPredicate;
import org.apache.ignite.lang.IgnitePredicate;
import ru.codeunited.ignite.services.UUIDServiceImpl;

import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import static org.apache.ignite.events.EventType.EVT_CACHE_OBJECT_PUT;
import static org.apache.ignite.events.EventType.EVT_CACHE_OBJECT_READ;
import static org.apache.ignite.events.EventType.EVT_CACHE_OBJECT_REMOVED;

/**
 * Created by ikonovalov on 19/04/16.
 */
public class CacheBinaryWriter {

    public static void main(String[] args) {

        Ignite ignite = Ignition.start("my-cache.xml");

        String cacheName1 = "zzCache";

        IgniteBiPredicate<UUID, CacheEvent> localLstPredicate = (uuid, event) -> {
            System.out.println("Local: " + event);
            return true;
        };

        IgnitePredicate<CacheEvent> remoteLstPredicate = event -> {
            System.out.println("Remote: " + event);
            return true;
        };

        int[] eventTypes = {EVT_CACHE_OBJECT_PUT, EVT_CACHE_OBJECT_READ, EVT_CACHE_OBJECT_REMOVED};

        ignite
                .events(ignite.cluster().forCacheNodes(cacheName1))
                .remoteListen(localLstPredicate, remoteLstPredicate, eventTypes);


        IgniteCache<Long, BinaryObject> cache1 = ignite.cache(cacheName1).withKeepBinary();

        BinaryObjectBuilder builder = ignite.binary().builder("java.lang.Object");

        builder.setField("A", "A");
        builder.setField("B", 555L);

        BinaryObject binaryObj = builder.build();

        cache1.put(100L, binaryObj);

        IgniteCache<Long, BinaryObject> qqCache = ignite.cache("qqCache").withKeepBinary();
        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);
        scheduledExecutorService.scheduleAtFixedRate(
                () -> {
                    BinaryObject binaryObject = qqCache.get(900L);
                    if (binaryObject != null) {
                        System.out.println(binaryObject);
                    } else
                        System.out.println("Nothing");
                },
                0, 5, TimeUnit.SECONDS);

    }

}
