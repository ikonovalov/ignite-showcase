package ru.codeunited.ignite.rest;

import lombok.extern.slf4j.Slf4j;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteServices;
import org.apache.ignite.services.ServiceDescriptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.codeunited.ignite.services.NodeService;

import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/services")
@Slf4j
public class ServiceController {

    private final Ignite ignite;

    @Autowired
    public ServiceController(Ignite ignite) {
        this.ignite = ignite;
    }

    @GetMapping("/execution-id")
    public UUID executionId() {
        IgniteServices services = ignite.services();
        NodeService nodeService = services.serviceProxy("node-service", NodeService.class, false);
        return nodeService.getExecutionId();
    }

    @GetMapping
    public List<String> services() {
        Collection<ServiceDescriptor> serviceDescriptors = ignite.services().serviceDescriptors();
        return serviceDescriptors
                .stream()
                .map(ServiceDescriptor::name)
                .collect(Collectors.toList());
    }
}
