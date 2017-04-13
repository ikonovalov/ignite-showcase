package ru.codeunited.ignite.services;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.binary.BinaryObject;
import org.apache.ignite.resources.IgniteInstanceResource;
import org.apache.ignite.services.Service;
import org.apache.ignite.services.ServiceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.codeunited.ignite.ContainerKT;

/**
 * Created by ikonovalov on 13/04/17.
 */
public class UUIDServiceImpl implements UUIDService, Service {

    private final Logger log = LoggerFactory.getLogger(UUIDServiceImpl.class);

    @IgniteInstanceResource
    private Ignite ignite;

    private IgniteCache<String, ContainerKT> cache;

    private String svcName;

    @Override
    public void cancel(ServiceContext ctx) {

    }

    @Override
    public void init(ServiceContext ctx) throws Exception {
        cache = ignite.cache("zzCache");

        svcName = ctx.name();

        log.info("Service {} init() done.", svcName);
    }

    @Override
    public void execute(ServiceContext ctx) throws Exception {

    }

    @Override
    public String toUID(String key) {
        return cache.get(key).getUuid().toString();
    }
}
