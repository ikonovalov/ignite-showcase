package ru.codeunited.ignite.rest;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.cache.CacheMetrics;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/metric")
public class MetricsController {

    private final Ignite ignite;

    @Autowired
    public MetricsController(Ignite ignite) {
        this.ignite = ignite;
    }

    @GetMapping("{cache}")
    @HystrixCommand(commandKey = "CacheMetrics")
    public CacheMetrics cluster(@PathVariable("cache") String cacheName) {
        IgniteCache cache = ignite.cache(cacheName);
        if (cache !=null) {
            return cache.metrics();
        } else {
            throw new IllegalArgumentException("Cache doesn't exist " + cacheName);
        }
    }
}
