package ru.codeunited.ignite.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.ignite.cache.affinity.AffinityFunction;
import org.apache.ignite.cache.affinity.rendezvous.RendezvousAffinityFunction;
import org.apache.ignite.configuration.CacheConfiguration;
import org.apache.ignite.configuration.DataStorageConfiguration;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

@Configuration
@Slf4j
public class DataGridConfiguration {

    @Bean
    public IgniteConfiguration igniteConfiguration(
            List<CacheConfiguration> cacheConfigurations,
            DataStorageConfiguration dataStorageConfiguration) throws UnknownHostException {

        CacheConfiguration[] cacheCfg = cacheConfigurations.toArray(new CacheConfiguration[cacheConfigurations.size()]);
        return new IgniteConfiguration()
                .setIgniteInstanceName(InetAddress.getLocalHost().getHostName() + "-grid-instance")
                .setCacheConfiguration(cacheCfg)
                .setDataStorageConfiguration(dataStorageConfiguration);
    }

    @Bean
    public AffinityFunction affinityFunction(
            @Value("${ignite.partition.count}") int partitionCount ) {

        log.info("ignite.partition.count: {}", partitionCount);
        return new RendezvousAffinityFunction(false, partitionCount);
    }

    @Bean
    public DataStorageConfiguration dataStorageConfiguration(
            @Value("${ignite.ds.wal}") String walPath,
            @Value("${ignite.ds.wal-arch}") String walArchPath,
            @Value("${ignite.ds.storage}") String storagePath ) {

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
