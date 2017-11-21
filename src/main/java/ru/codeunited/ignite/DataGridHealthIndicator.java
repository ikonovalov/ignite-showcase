package ru.codeunited.ignite;

import org.apache.ignite.Ignite;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
public class DataGridHealthIndicator implements HealthIndicator {

    private final Ignite ignite;

    @Autowired
    public DataGridHealthIndicator(Ignite ignite) {
        this.ignite = ignite;
    }

    @Override
    public Health health() {
        if (!ignite.active()) {
            return Health.outOfService().build();
        }
        if (ignite.cacheNames().size() > 0) {
            Health.Builder healthBuilder = Health.up();
            ignite.cacheNames().forEach(cacheName -> healthBuilder.withDetail(cacheName, "OK"));
            return healthBuilder.build();
        }
        return Health.unknown().build();
    }
}
