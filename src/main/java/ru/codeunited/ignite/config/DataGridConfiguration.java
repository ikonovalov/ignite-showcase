package ru.codeunited.ignite.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.ignite.IgniteLogger;
import org.apache.ignite.cache.affinity.AffinityFunction;
import org.apache.ignite.cache.affinity.rendezvous.RendezvousAffinityFunction;
import org.apache.ignite.configuration.CacheConfiguration;
import org.apache.ignite.configuration.DataStorageConfiguration;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.logger.slf4j.Slf4jLogger;
import org.apache.ignite.spi.discovery.DiscoverySpi;
import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi;
import org.apache.ignite.spi.discovery.tcp.ipfinder.kubernetes.TcpDiscoveryKubernetesIpFinder;
import org.apache.ignite.spi.discovery.tcp.ipfinder.multicast.TcpDiscoveryMulticastIpFinder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;

import static org.apache.ignite.spi.discovery.tcp.ipfinder.multicast.TcpDiscoveryMulticastIpFinder.DFLT_MCAST_GROUP;

@Configuration
@ImportResource("classpath:ignite-override.xml")
@Slf4j
public class DataGridConfiguration {

    private static String INSTANCE_NAME;

    static {
        try {
            INSTANCE_NAME = InetAddress.getLocalHost().getHostName() + "-grid-instance";
        } catch (UnknownHostException e) {
            INSTANCE_NAME = UUID.randomUUID() + "-grid-instance";
        }
        MDC.put("dg-instance", INSTANCE_NAME);
    }


    @Bean
    public IgniteConfiguration igniteConfiguration(
            List<CacheConfiguration> cacheConfigurations,
            IgniteLogger logger,
            DiscoverySpi discoverySpi,
            DataStorageConfiguration dataStorageConfiguration) {

        CacheConfiguration[] cacheCfg = cacheConfigurations.toArray(new CacheConfiguration[cacheConfigurations.size()]);
        return new IgniteConfiguration()
                .setIgniteInstanceName(INSTANCE_NAME)
                .setCacheConfiguration(cacheCfg)
                .setGridLogger(logger)
                .setDiscoverySpi(discoverySpi)
                .setMetricsUpdateFrequency(5000L)
                .setMetricsLogFrequency(60000 * 10)
                .setDataStorageConfiguration(dataStorageConfiguration)
                .setUserAttributes(
                        IgniteNodeAttributes.newInstance()
                                .attr("node_type", "storage-service")
                                .toMap()
                );
    }

    @Bean
    public AffinityFunction affinityFunction(
            @Value("${ignite.partition.count}") int partitionCount) {

        log.info("ignite.partition.count: {}", partitionCount);
        return new RendezvousAffinityFunction(false, partitionCount);
    }

    @Bean
    public DataStorageConfiguration dataStorageConfiguration(
            @Value("${ignite.ds.wal}") String walPath,
            @Value("${ignite.ds.wal-arch}") String walArchPath,
            @Value("${ignite.ds.storage}") String storagePath) {

        log.info("Setup data storage configuration...");
        boolean metricEnabled = true;
        log.info("ignite.ds.wal {}", walPath);
        log.info("ignite.ds.wal-arch {}", walArchPath);
        log.info("ignite.ds.storage {}", storagePath);
        log.info("metricEnabled = {}", metricEnabled);

        return new DataStorageConfiguration()
                .setMetricsEnabled(metricEnabled)
                .setWalArchivePath(walArchPath)
                .setWalPath(walPath)
                .setStoragePath(storagePath);
    }

    @Bean
    public IgniteLogger gridLogger() {
        Logger logger = LoggerFactory.getLogger("org.apache.ignite");
        return new Slf4jLogger(logger);
    }

    @Bean
    public DiscoverySpi discoverySpi() {

        if (System.getenv("KUBERNETES_SERVICE_HOST") != null) {
            log.info("Setup kubernetes discovery SPI");
            return new TcpDiscoverySpi()
                    .setIpFinder(new TcpDiscoveryKubernetesIpFinder());

        } else {
            log.info("Setup tcp multicast discovery SPI");
            return new TcpDiscoverySpi()
                    .setIpFinder(new TcpDiscoveryMulticastIpFinder().setMulticastGroup(DFLT_MCAST_GROUP));
        }
    }

    static final class IgniteNodeAttributes {

        private final Map<String, String> attributes = new HashMap<>();

        private IgniteNodeAttributes() {
            super();
        }

        static IgniteNodeAttributes newInstance() {
            return new IgniteNodeAttributes();
        }

        IgniteNodeAttributes attr(String key, String value) {
            attributes.put(key, value);
            return this;
        }

        Map<String, String> toMap() {
            return Collections.unmodifiableMap(attributes);
        }
    }
}
