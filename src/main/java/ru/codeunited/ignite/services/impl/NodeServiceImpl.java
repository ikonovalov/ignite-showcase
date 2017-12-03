package ru.codeunited.ignite.services.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.ignite.Ignite;
import org.apache.ignite.resources.IgniteInstanceResource;
import org.apache.ignite.services.Service;
import org.apache.ignite.services.ServiceContext;
import ru.codeunited.ignite.services.NodeService;

import java.net.InetAddress;
import java.util.UUID;

@Slf4j
public class NodeServiceImpl implements NodeService, Service {

    @IgniteInstanceResource
    private Ignite ignite;

    private UUID executionId;

    @Override
    public void cancel(ServiceContext ctx) {

    }

    @Override
    public void init(ServiceContext ctx) throws Exception {
        this.executionId = ctx.executionId();
        log.debug("Service {} initialized on {}", ctx.name(), InetAddress.getLocalHost().getHostName());
    }

    @Override
    public void execute(ServiceContext ctx) {

    }

    @Override
    public UUID getExecutionId() {
        return executionId;
    }
}
