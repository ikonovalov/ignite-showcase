package ru.codeunited.ignite;

import lombok.extern.slf4j.Slf4j;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCheckedException;
import org.apache.ignite.IgniteSpring;
import org.apache.ignite.Ignition;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.netflix.hystrix.dashboard.EnableHystrixDashboard;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

@SpringBootApplication(
        scanBasePackages = "ru.codeunited.ignite"
)
@EnableCircuitBreaker
@EnableHystrixDashboard
@Slf4j
public class BootUp {

    @Bean
    public Ignite grid(IgniteConfiguration igniteConfiguration, ApplicationContext applicationContext) throws IgniteCheckedException {
        Ignite grid = IgniteSpring.start(igniteConfiguration, applicationContext);
        grid.active(true);
        log.info("{} started and activated", grid.name());
        return grid;
    }

    public static void main(String[] args) {
        SpringApplication
                .run(BootUp.class, args)
                .registerShutdownHook();
    }
}
