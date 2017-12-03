package ru.codeunited.ignite.services.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.resources.IgniteInstanceResource;
import org.apache.ignite.services.Service;
import org.apache.ignite.services.ServiceContext;
import ru.codeunited.ignite.config.MyCacheConfiguration;
import ru.codeunited.ignite.model.QuestValue;
import ru.codeunited.ignite.services.QuestService;

/**
 * Created by ikonovalov on 13/04/17.
 */
@Slf4j
public class QuestServiceDistributedImpl implements QuestService, Service {

    @IgniteInstanceResource
    private Ignite ignite;

    private IgniteCache<Long, QuestValue> cache;

    private String svcName;

    @Override
    public void cancel(ServiceContext ctx) {
        log.info("{} got cancel()", svcName);
    }

    @Override
    public void init(ServiceContext ctx) {
        cache = ignite.cache(MyCacheConfiguration.MY_CACHE);
        svcName = ctx.name();
        log.info("Service {} init() done.", svcName);
    }

    @Override
    public void execute(ServiceContext ctx) {
        log.info("Executing distributed service: {}", svcName);
    }

    @Override
    public QuestValue quest(Long key) {
        return cache.get(key);
    }
}
