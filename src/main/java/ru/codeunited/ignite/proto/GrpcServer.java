package ru.codeunited.ignite.proto;

import com.ecwid.consul.v1.agent.model.NewService;
import io.grpc.BindableService;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.consul.discovery.ConsulDiscoveryProperties;
import org.springframework.cloud.consul.serviceregistry.ConsulRegistration;
import org.springframework.cloud.consul.serviceregistry.ConsulServiceRegistry;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

@Component
@Slf4j
public class GrpcServer {

    private Server server;

    @Autowired
    private List<BindableService> gRpcServices;

    @Autowired
    private Optional<ConsulServiceRegistry> serviceRegistry;

    @Autowired
    private Optional<ConsulDiscoveryProperties> discoveryProperties;

    @Autowired
    private Optional<ConsulRegistration> bootRegistration;

    private final AtomicReference<ConsulRegistration> gRpcServiceRegistration = new AtomicReference<>();

    @Value("${server.grpc}")
    private int port;

    @PostConstruct
    public void prepareServiceRegistration() throws UnknownHostException {
        if (serviceRegistry.isPresent() && discoveryProperties.isPresent() && bootRegistration.isPresent()) {
            NewService gRpcService = new NewService();
            gRpcService.setId(bootRegistration.get().getServiceId() + "-gRpc");
            gRpcService.setName("gRPC");
            gRpcService.setAddress(InetAddress.getLocalHost().getHostAddress());
            gRpcService.setPort(port);
            ConsulRegistration cr = new ConsulRegistration(gRpcService, discoveryProperties.get());
            gRpcServiceRegistration.compareAndSet(null, cr);
            log.debug("Prepare gRPC service registration: {}", cr);
        }
    }

    @PostConstruct
    public void start() throws IOException {
        ServerBuilder serverBuilder = ServerBuilder.forPort(port);
        gRpcServices.forEach(serverBuilder::addService);
        server = serverBuilder
                .build()
                .start();
        log.info("gRPC server started, listening on {}", port);

        serviceRegistry.ifPresent(registry -> registry.register(gRpcServiceRegistration.get()));
    }

    @PreDestroy
    public void stop() {
        log.info("Stop gRPC server");
        if (server != null) {
            server.shutdown();
            log.debug("gRPC server stopped");
            serviceRegistry.ifPresent(registry -> registry.deregister(gRpcServiceRegistration.get()));
        }
    }
}
