package ru.codeunited.ignite;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.Ignition;
import org.apache.ignite.binary.BinaryObject;
import org.apache.ignite.binary.BinaryType;
import org.apache.ignite.lang.IgniteFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * Created by ikonovalov on 28/04/16.
 */
public class CacheReader {

    private static final Logger LOG = LoggerFactory.getLogger(CacheReader.class);

    public static void main(String[] args) {
        Ignition.setClientMode(true);
        Ignite ignite = Ignition.start("my-cache.xml");
        IgniteCache<String, BinaryObject> cache = ignite.cache("zzCache").withAsync().withKeepBinary();

        cache.get("K899");
        cache.get("K898");
        cache.get("K897");

        cache.future().listen(objectIgniteFuture -> {
            BinaryObject bo = (BinaryObject) objectIgniteFuture.get(10, TimeUnit.SECONDS);
            LOG.info("K899 found");
        });

        LOG.debug("Done");

    }

}
