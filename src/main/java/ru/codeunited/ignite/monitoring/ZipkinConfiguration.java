package ru.codeunited.ignite.monitoring;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(value = "spring.zipkin.enabled", matchIfMissing = true)
public class ZipkinConfiguration {


}
