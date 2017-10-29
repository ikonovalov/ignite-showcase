package ru.codeunited.ignite.rest;

import org.apache.ignite.Ignite;
import org.apache.ignite.cache.CacheMetrics;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/metric")
public class MetricsController {

    private final Ignite ignite;

    @Autowired
    public MetricsController(Ignite ignite) {
        this.ignite = ignite;
    }

    @GetMapping("{cache}")
    public CacheMetrics cluster(@PathVariable("cache") String cacheName) {
        return ignite.cache(cacheName).metrics();
    }
}
