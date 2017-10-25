package ru.codeunited.ignite;

import lombok.extern.slf4j.Slf4j;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.cache.affinity.Affinity;
import org.apache.ignite.cache.query.QueryCursor;
import org.apache.ignite.cache.query.ScanQuery;
import org.apache.ignite.cache.query.TextQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.codeunited.ignite.config.MyCacheConfig;
import ru.codeunited.ignite.model.QuestValue;

import javax.annotation.PostConstruct;
import javax.cache.Cache;
import java.util.List;
import java.util.stream.Collectors;

import static ru.codeunited.ignite.config.MyCacheConfig.MY_CACHE;

@RestController("/quest")
@Slf4j
public class Quest {

    private final Ignite ignite;

    @Autowired
    public Quest(Ignite ignite, MyCacheConfig.Loader loader) {
        this.ignite = ignite;
    }

    @GetMapping("/search")
    public List<QuestValue> search(@RequestBody String queryText) {
        IgniteCache<Long, QuestValue> cache = ignite.getOrCreateCache(MY_CACHE);
        long startMonent = System.currentTimeMillis();
        TextQuery<Long, QuestValue> txt = new TextQuery<>(QuestValue.class, queryText);
        try (QueryCursor<Cache.Entry<Long, QuestValue>> cursor = cache.query(txt)) {
            log.debug("Open cursor in {}ms", (System.currentTimeMillis() - startMonent));
            return cursor.getAll().stream().map(Cache.Entry::getValue).collect(Collectors.toList());
        }
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
        long s2 = System.currentTimeMillis();
        TextQuery<Long, QuestValue> txt = new TextQuery<>(QuestValue.class, "desc: desc133* AND text: t*1*2*");
        //TextQuery<Long, QuestValue> txt = new TextQuery<>(QuestValue.class, "desc: [desc100 TO desc110]");
        try (QueryCursor<Cache.Entry<Long, QuestValue>> cursor = cache.query(txt)) {
            List<Cache.Entry<Long, QuestValue>> all_text = cursor.getAll();
            log.debug("#1 Text query {} records in {}ms", all_text.size(), System.currentTimeMillis() - s2);
            all_text.forEach(entry -> log.debug("\t{}", entry.getValue()));
            log.debug("\tTotal entries {}", all_text.size());
        }


        s2 = System.currentTimeMillis();
        txt = new TextQuery<>(QuestValue.class, "desc: desc3* AND text: t*3*");
        try (QueryCursor<Cache.Entry<Long, QuestValue>> cursor = cache.query(txt)) {
            Cache.Entry<Long, QuestValue> next = cursor.iterator().next();
            log.debug("#2 Text query records in " + (System.currentTimeMillis() - s2) + "ms");
            log.debug("\t First: " + next.getValue());
        }
    }
}
