package ru.codeunited.ignite.prometheus;

import io.prometheus.client.Collector;
import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.exporter.MetricsServlet;
import io.prometheus.client.hotspot.MemoryPoolsExports;
import io.prometheus.client.hotspot.StandardExports;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.util.List;

@Configuration
@ConditionalOnClass(CollectorRegistry.class)
public class PrometheusConfiguration {

    @Bean @ConditionalOnMissingBean
    public CollectorRegistry metricRegistry() {
        return CollectorRegistry.defaultRegistry;
    }

    @Bean @ConditionalOnClass(MetricsServlet.class)
    public ServletRegistrationBean registerPrometheusExporterServlet(CollectorRegistry metricRegistry) {
        return new ServletRegistrationBean(new MetricsServlet(metricRegistry), "/metrics");
    }

    @Bean
    ExporterRegister exporterRegister(List<Collector> collectors, CollectorRegistry registry) throws IOException {
        return new ExporterRegister(collectors);
    }

    @Bean
    StandardExports standardExports() {
        return new StandardExports();
    }

    @Bean
    MemoryPoolsExports memoryPoolsExports() {
        return new MemoryPoolsExports();
    }

    public static class ExporterRegister {

        private List<Collector> collectors;

        public ExporterRegister(List<Collector> collectors) {
            for (Collector collector : collectors) {
                collector.register();
            }
            this.collectors = collectors;
        }

        public List<Collector> getCollectors() {
            return collectors;
        }

    }

}
