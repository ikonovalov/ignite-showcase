package ru.codeunited.ignite;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.Ignition;
import org.apache.ignite.binary.BinaryObject;
import org.apache.ignite.binary.BinaryObjectBuilder;

/**
 * Created by ikonovalov on 19/04/16.
 */
public class CacheBinaryReader {

    public static void main(String[] args) {
        System.out.println(">>>> " + CacheBinaryReader.class.getSimpleName());

        Ignite ignite = Ignition.start("my-cache.xml");

        IgniteCache<Long, BinaryObject> zzCache = ignite.cache("zzCache").withKeepBinary();

        BinaryObject bo = zzCache.get(100L);

        String a = bo.field("A");
        Long b = bo.field("B");

        System.out.println("String A = " + a);
        System.out.println("Long B = " + b);

        IgniteCache<Long, BinaryObject> qqCache = ignite.cache("qqCache").withKeepBinary();
        qqCache.put(900L, bo);

        System.out.println("Done");
    }

}
