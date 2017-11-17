package ru.codeunited.ignite.services;

import org.apache.ignite.cluster.ClusterGroup;

public interface ClusterGroupSelectorStrategy {

    ClusterGroup select();
}
