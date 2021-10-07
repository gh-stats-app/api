package app.ghstats.api.services.metrics;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

@ConstructorBinding
@ConfigurationProperties(prefix = "graphite")
record GraphiteConfigurationProperties(String instance, String token, String endpoint) {
}
