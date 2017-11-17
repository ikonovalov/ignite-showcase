package ru.codeunited.ignite.services.impl;

import org.apache.ignite.Ignite;
import org.apache.ignite.cluster.ClusterGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.codeunited.ignite.services.ClusterGroupSelectorStrategy;

import java.util.function.Supplier;

@Component
public class ServersOnlyClusterGroupSelector implements ClusterGroupSelectorStrategy, Supplier<ClusterGroup> {

    private final Ignite ignite;

    @Autowired
    public ServersOnlyClusterGroupSelector(Ignite ignite) {
        this.ignite = ignite;
    }


    @Override
    public ClusterGroup select() {
        return ignite.cluster().forServers();
    }

    @Override
    public ClusterGroup get() {
        return select();
    }
}
