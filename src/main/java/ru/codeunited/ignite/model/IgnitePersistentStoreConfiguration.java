package ru.codeunited.ignite.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class IgnitePersistentStoreConfiguration {

    private String persistenceStorePath;

    private String walStorePath;
}
