package ru.codeunited.ignite.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;

@Component
@ConditionalOnProperty(name = "zookeeper")
@Slf4j
public class LogbackZKConfigurationChanger {

    private static final String NAMESPACE_DG = "dg";

    private final String zkConnections;

    private String environment = "dev";

    private CuratorFramework framework;

    public LogbackZKConfigurationChanger() {
        log.info("Enabled zookeeper integration");
        zkConnections = System.getenv("zookeeper");
    }

    private CuratorFrameworkFactory.Builder framework() {
        return CuratorFrameworkFactory.builder().connectString(zkConnections).retryPolicy(
                new ExponentialBackoffRetry(1000, 3)
        ).namespace(NAMESPACE_DG);
    }

    @PostConstruct
    public void attach() {
        try (CuratorFramework framework = framework().build()) {

            framework.start();
            this.framework = framework;

            String envPath = "/" + environment;
            if (framework.checkExists().creatingParentsIfNeeded().forPath(envPath) == null) {
                framework.create().forPath(envPath);
                log.debug("Create ZKNode {}", envPath);
            }

            String instancePath = envPath + "/" + DataGridConfiguration.INSTANCE_NAME;

            framework.getCuratorListenable().addListener((client, event) -> System.out.println(event));
            framework
                    .create()
                    .creatingParentsIfNeeded()
                    .withMode(CreateMode.EPHEMERAL)
                    .forPath(
                            instancePath + "/logging",
                            Files.readAllBytes(Paths.get("/home/ikonovalov/developer/projects/ignite-showcase/src/main/resources/logback.xml"))
                    );
            log.debug("Create ZKNode (EPHEMERAL) {}", instancePath);

        } catch (Exception e) {
            log.warn(e.getMessage());
        }
    }


    @PreDestroy
    public void cleanup() {
        Optional.ofNullable(framework).ifPresent(CuratorFramework::close);
    }
}
