package ru.codeunited.ignite.rest;

import lombok.extern.slf4j.Slf4j;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.cache.query.QueryCursor;
import org.apache.ignite.cache.query.ScanQuery;
import org.apache.ignite.cache.query.TextQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.codeunited.ignite.config.MyCacheConfiguration;
import ru.codeunited.ignite.model.QuestValue;

import javax.annotation.PostConstruct;
import javax.cache.Cache;
import java.util.List;
import java.util.stream.Collectors;

import static ru.codeunited.ignite.config.MyCacheConfiguration.MY_CACHE;

@RestController
@RequestMapping("/quest")
@Slf4j
public class QuestController {

    private final Ignite ignite;

    @Autowired
    public QuestController(Ignite ignite, MyCacheConfiguration.StreamLoader streamLoader) {
        this.ignite = ignite;
    }

    @PostMapping("/search")
    public List<QuestValue> search(@RequestBody String queryText) {
        IgniteCache<Long, QuestValue> cache = ignite.cache(MY_CACHE);
        long startMonent = System.currentTimeMillis();
        TextQuery<Long, QuestValue> txt = new TextQuery<>(QuestValue.class, queryText);
        try (QueryCursor<Cache.Entry<Long, QuestValue>> cursor = cache.query(txt)) {
            log.debug("Open cursor in {}ms", (System.currentTimeMillis() - startMonent));
            return cursor.getAll().stream().map(Cache.Entry::getValue).collect(Collectors.toList());
        }
    }

    @PostMapping("/put")
    public String put(@RequestBody QuestValue value) {
        IgniteCache<Long, QuestValue> cache = ignite.cache(MY_CACHE);
        cache.put(value.getId(), value);
        return "OK";
    }

    @PostConstruct
    public void testScans() {
        log.debug("Launch test scans...");
        final IgniteCache<Long, QuestValue> cache = ignite.getOrCreateCache(MY_CACHE);

        // try scan
        long s1 = System.currentTimeMillis();
        ScanQuery<Long, QuestValue> qry = new ScanQuery<>((k,v) -> "text5".equals(v.getText()));
        List<Cache.Entry<Long, QuestValue>> all = cache.query(qry).getAll();
        log.debug("Scan found " + all.size() + " records in " + (System.currentTimeMillis() - s1) + "ms");
        if (all.size() > 0) {
            log.debug("\t" + all.get(0));
        }

        // try text query
        long startPoint = System.currentTimeMillis();
        TextQuery<Long, QuestValue> txt = new TextQuery<>(QuestValue.class, "desc: desc133* AND text: t*1*2*");
        //TextQuery<Long, QuestValue> txt = new TextQuery<>(QuestValue.class, "desc: [desc100 TO desc110]");
        try (QueryCursor<Cache.Entry<Long, QuestValue>> cursor = cache.query(txt)) {
            List<Cache.Entry<Long, QuestValue>> allRecords = cursor.getAll();
            log.debug("#1 Text query {} records in {}ms", allRecords.size(), System.currentTimeMillis() - startPoint);
            allRecords.forEach(entry -> log.debug("\t{}", entry.getValue()));
            log.debug("\tTotal entries {}", allRecords.size());
        }

    }
}
