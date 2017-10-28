package ru.codeunited.ignite;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteAtomicSequence;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.Ignition;
import org.apache.ignite.configuration.AtomicConfiguration;
import org.apache.ignite.configuration.CacheConfiguration;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.lang.IgniteRunnable;

import java.util.UUID;
import java.util.stream.IntStream;

import static org.apache.ignite.cache.CacheAtomicityMode.ATOMIC;
import static org.apache.ignite.cache.CacheMode.PARTITIONED;

@Slf4j
class Show2Writer {

    private static final String PAYLOAD_CACHE = "payloadCache";

    @AllArgsConstructor
    @NoArgsConstructor @Setter
    @Builder @EqualsAndHashCode
    static class Key {
        private long id;
        private UUID uuid;
        private short version;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    static class Payload {
        private String text;
    }

    private final IgniteConfiguration igniteConfiguration = new IgniteConfiguration();

    private final CacheConfiguration<Key, Payload> payloadCacheConfiguration = new CacheConfiguration<>();

    private Show2Writer init() {
        payloadCacheConfiguration.setName(PAYLOAD_CACHE);
        payloadCacheConfiguration.setCacheMode(PARTITIONED);
        payloadCacheConfiguration.setAtomicityMode(ATOMIC);

        igniteConfiguration.setClientMode(false);
        igniteConfiguration.setCacheConfiguration(payloadCacheConfiguration);

        AtomicConfiguration atomicCfg = new AtomicConfiguration();
        atomicCfg.setBackups(3);
        atomicCfg.setCacheMode(PARTITIONED);
        atomicCfg.setAtomicSequenceReserveSize(5);
        igniteConfiguration.setAtomicConfiguration(atomicCfg);
        return this;
    }

    private void run() {
        Ignite ignite = Ignition.start(igniteConfiguration);
        IgniteCache<Key, Payload> cache = ignite.cache(PAYLOAD_CACHE);
        UUID nodeUUID = ignite.cluster().localNode().id();

        IntStream.range(0, 100).forEach(i -> {
            Key key = new Key();
            key.uuid = UUID.nameUUIDFromBytes("Hello".getBytes());
            key.version = 1;
            Payload payload = new Payload();
            payload.text = "Hello " + nodeUUID;
            cache.put(key,  payload);
        });

        ignite.compute().broadcast(new SequenceClosure("example-sequence"));

    }

    public static void main(String[] args) {
        new Show2Writer()
                .init()
                .run();
    }

    private static class SequenceClosure implements IgniteRunnable {
        /** Sequence name. */
        private final String seqName;

        private static final int RETRIES = 20;

        /**
         * @param seqName Sequence name.
         */
        SequenceClosure(String seqName) {
            this.seqName = seqName;
        }

        /** {@inheritDoc} */
        @Override public void run() {
            // Create sequence. Only one concurrent call will succeed in creation.
            // Rest of the callers will get already created instance.
            IgniteAtomicSequence seq = Ignition.ignite().atomicSequence(seqName, 0, true);

            // First value of atomic sequence on this node.
            long firstVal = seq.get();

            System.out.println("Sequence initial value on local node: " + firstVal);

            for (int i = 0; i < RETRIES; i++)
                System.out.println("Sequence [currentValue=" + seq.get() + ", afterIncrement=" +
                        seq.incrementAndGet() + ']');

            System.out.println("Sequence after incrementing [expected=" + (firstVal + RETRIES) + ", actual=" +
                    seq.get() + ']');
        }
    }
}
