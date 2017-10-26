package ru.codeunited.ignite;

import org.apache.ignite.Ignite;
import org.apache.ignite.Ignition;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication(
        scanBasePackages = "ru.codeunited.ignite"
)
public class BootUp {

    @Bean
    public Ignite ignite(IgniteConfiguration igniteConfiguration) {
        Ignite start = Ignition.start(igniteConfiguration);
        start.active(true);
        return start;
    }

    public static void main(String[] args) {
        SpringApplication.run(BootUp.class, args);
    }
}
