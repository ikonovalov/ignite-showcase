package ru.codeunited.ignite.rest;

import org.apache.ignite.Ignite;
import org.apache.ignite.configuration.DataStorageConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.codeunited.ignite.model.IgnitePersistentStoreConfiguration;

@RestController
@RequestMapping("/ignite/configuration")
public class ConfigurationController {

    private final Ignite ignite;

    @Autowired
    public ConfigurationController(Ignite ignite) {
        this.ignite = ignite;
    }

    @GetMapping("/persistence")
    public IgnitePersistentStoreConfiguration persistentStoreConfiguration() {
        DataStorageConfiguration cfg = ignite.configuration().getDataStorageConfiguration();
        return IgnitePersistentStoreConfiguration.builder()
                .persistenceStorePath(cfg.getStoragePath())
                .walStorePath(cfg.getWalPath())
                .walArchivePath(cfg.getWalArchivePath())
                .build();
    }
}
