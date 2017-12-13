package ru.codeunited.ignite.monitoring;

import io.prometheus.client.Collector;
import io.prometheus.client.GaugeMetricFamily;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.cache.CacheMetrics;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class DataGridExporter extends Collector {

    private final Ignite ignite;

    @Autowired
    public DataGridExporter(Ignite ignite) {
        this.ignite = ignite;
    }

    @Override
    public List<MetricFamilySamples> collect() {

        List<MetricFamilySamples> result  = new ArrayList<>();
        addLocalCachesMetrics(result);
        return result;
    }

    private void addLocalCachesMetrics(List<MetricFamilySamples> result) {
        List<String> labelNames = Arrays.asList("grid", "cache");

        GaugeMetricFamily cacheOffheapEntriesCntGauge = creteGaugeFamily(labelNames, "dg_cache_metric_offheap_entry_cnt");
        GaugeMetricFamily cacheHeapEntriesCntGauge = creteGaugeFamily(labelNames, "dg_cache_metric_heap_entry_cnt");

        result.add(cacheOffheapEntriesCntGauge);
        result.add(cacheHeapEntriesCntGauge);

        for(String cacheName : ignite.cacheNames()) {
            IgniteCache cache = ignite.cache(cacheName);
            CacheMetrics cacheMetrics = cache.localMetrics();

            final List<String> labelValues = Arrays.asList(ignite.name(), cache.getName());

            cacheOffheapEntriesCntGauge.addMetric(labelValues,
                    cacheMetrics.getOffHeapEntriesCount()
            );

            cacheHeapEntriesCntGauge.addMetric(labelValues,
                    cacheMetrics.getHeapEntriesCount()
            );
        }
    }

    @NotNull
    private GaugeMetricFamily creteGaugeFamily(List<String> labelNames, String name) {
        return new GaugeMetricFamily(name, "", labelNames);
    }
}
