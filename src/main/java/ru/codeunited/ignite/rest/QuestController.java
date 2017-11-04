package ru.codeunited.ignite.rest;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import lombok.extern.slf4j.Slf4j;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.cache.CacheMetrics;
import org.apache.ignite.cache.query.QueryCursor;
import org.apache.ignite.cache.query.SqlQuery;
import org.apache.ignite.cache.query.TextQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.codeunited.ignite.config.MyCacheConfiguration;
import ru.codeunited.ignite.model.QuestValue;

import javax.cache.Cache;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static ru.codeunited.ignite.config.MyCacheConfiguration.MY_CACHE;

@RestController
@RequestMapping("/quest")
@Slf4j
public class QuestController {

    private final Ignite ignite;

    @Autowired
    public QuestController(Ignite ignite, MyCacheConfiguration.CacheAutomation cacheAutomation) {
        this.ignite = ignite;
    }

    private IgniteCache<Long, QuestValue> cache() {
        return ignite.cache(MY_CACHE);
    }

    @GetMapping("/metrics")
    public CacheMetrics metrics() {
        return cache().metrics();
    }

    @PostMapping("/search")
    public List<QuestValue> search(@RequestBody String queryText) {
        IgniteCache<Long, QuestValue> cache = cache();
        long startMoment = System.currentTimeMillis();
        TextQuery<Long, QuestValue> txt = new TextQuery<>(QuestValue.class, queryText);
        try (QueryCursor<Cache.Entry<Long, QuestValue>> cursor = cache.query(txt)) {
            log.debug("Open cursor in {}ms", (System.currentTimeMillis() - startMoment));
            return cursor.getAll().stream().map(Cache.Entry::getValue).collect(Collectors.toList());
        }
    }

    @PostMapping("/sql")
    public List<QuestValue> sql(@RequestBody String sqlText) {
        IgniteCache<Long, QuestValue> cache = cache();
        SqlQuery<Long, QuestValue> quest = new SqlQuery<>(QuestValue.class, sqlText);
        try(QueryCursor<Cache.Entry<Long, QuestValue>> cursor = cache.query(quest)) {
            return cursor.getAll().stream().map(Cache.Entry::getValue).collect(Collectors.toList());
        }
    }

    @PostMapping
    @HystrixCommand(
            commandKey = "PutQuest",
            fallbackMethod = "putFallback"
    )
    public boolean put(@RequestBody QuestValue value) {
        IgniteCache<Long, QuestValue> cache = cache();
        long id = value.getId();
        return cache.putIfAbsent(id, value);
    }

    public boolean putFallback(QuestValue value) {
        return Boolean.FALSE;
    }

    @GetMapping("/{id}")
    @HystrixCommand(
            commandKey = "GetQuest",
            fallbackMethod = "getFallback"
    )
    public QuestValue get(@PathVariable("id") Long id) {
        IgniteCache<Long, QuestValue> cache = cache();
        return Optional.ofNullable(cache.get(id)).orElseThrow(() -> new RuntimeException(id + " not found"));
    }

    @SuppressWarnings("SameReturnValue")
    public QuestValue getFallback(Long id) {
        return QuestValue.EMPTY;
    }

}
