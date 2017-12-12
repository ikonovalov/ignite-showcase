package ru.codeunited.ignite.config;

import com.ecwid.consul.v1.ConsulClient;
import com.ecwid.consul.v1.agent.model.NewService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;


@Configuration
@ConditionalOnClass(ConsulClient.class)
@Slf4j
public class ConsulRegHook {

    private static final String SERVICE_NAME = "dg";

    @Configuration
    @ConfigurationProperties(prefix = "consul")
    @Data
    public static class ConsulSetting {
        private String url;
        private List<String> tags = new ArrayList<>();
    }

    @Value("${server.port}")
    private String serverPort;

    private final NewService service = new NewService();

    private final ConsulSetting consulSetting;

    @Autowired
    public ConsulRegHook(ConsulSetting consulSetting) {
        this.consulSetting = consulSetting;
    }

    public void prepareServiceInfo() {

    }

    @PostConstruct
    public void registration() {
        try {
            ConsulClient client = new ConsulClient(consulSetting.getUrl());
            String hostAddress = InetAddress.getLocalHost().getHostAddress();

            this.service.setName(SERVICE_NAME);
            this.service.setTags(consulSetting.getTags());
            this.service.setPort(Integer.valueOf(serverPort));
            this.service.setId(hostAddress + ":" + serverPort);
            this.service.setAddress(hostAddress);
            client.agentServiceRegister(service);
            log.info("Registered as \n{}", service);
        } catch (UnknownHostException e) {
            log.error("Can't determinate hostname. ", e);
        } catch (Exception e) {
            log.error("Can't perform registration in a Consul " + consulSetting.getUrl() + ". Skip it and go further.", e);
        }
    }

    @PreDestroy
    public void deRegistration() {
        ConsulClient client = new ConsulClient(consulSetting.getUrl());
        client.agentServiceDeregister(service.getId());
    }

}
