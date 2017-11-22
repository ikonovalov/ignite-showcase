package ru.codeunited.ignite.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.ignite.cache.affinity.AffinityFunction;
import org.apache.ignite.cache.affinity.rendezvous.RendezvousAffinityFunction;
import org.apache.ignite.configuration.CacheConfiguration;
import org.apache.ignite.configuration.DataStorageConfiguration;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi;
import org.apache.ignite.spi.discovery.tcp.ipfinder.kubernetes.TcpDiscoveryKubernetesIpFinder;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.UUID;

@Configuration
@Slf4j
public class DataGridConfiguration {

    static String INSTANCE_NAME;
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
            DataStorageConfiguration dataStorageConfiguration) throws UnknownHostException {

        CacheConfiguration[] cacheCfg = cacheConfigurations.toArray(new CacheConfiguration[cacheConfigurations.size()]);
        IgniteConfiguration configuration = new IgniteConfiguration()
                .setIgniteInstanceName(InetAddress.getLocalHost().getHostName() + "-grid-instance")
                .setCacheConfiguration(cacheCfg)
                .setDataStorageConfiguration(dataStorageConfiguration);

        if (System.getenv("KUBERNETES_SERVICE_HOST") != null) {
            log.info("Setup kubernetes discovery");
            configuration.setDiscoverySpi(new TcpDiscoverySpi().setIpFinder(new TcpDiscoveryKubernetesIpFinder()));
        }
        return configuration;
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

        log.info("ignite.ds.wal {}", walPath);
        log.info("ignite.ds.wal-arch {}", walArchPath);
        log.info("ignite.ds.storage {}", storagePath);

        return new DataStorageConfiguration()
                .setMetricsEnabled(true)
                .setWalArchivePath(walArchPath)
                .setWalPath(walPath)
                .setStoragePath(storagePath);
    }
}
