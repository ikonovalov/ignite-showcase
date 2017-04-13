package ru.codeunited.ignite;

import org.apache.ignite.*;
import org.apache.ignite.cache.CachePeekMode;

import java.util.UUID;
import java.util.stream.IntStream;

/**
 * Created by ikonovalov on 19/04/16.
 */
public class CacheWriter {

    public static void main(String[] args) {

        Ignite ignite = Ignition.start("my-cache.xml");

        IgniteCache<String, ContainerKT> cache = ignite.cache("zzCache");

        // load zzCache
        IntStream.range(0000, 2000).forEach(
                i -> cache.put("K" + i, new ContainerKT(
                        UUID.randomUUID(),
                        new byte[]{0xF, (byte) i},
                        2)
                )
        );

        IgniteCompute compute = ignite.compute(ignite.cluster());


        compute.broadcast(() -> System.out.println("33New local cache size " + cache.localSize(CachePeekMode.ALL)));

        //Ignition.stop(true);
    }

}
