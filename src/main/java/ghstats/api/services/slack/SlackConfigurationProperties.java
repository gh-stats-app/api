package ghstats.api.services.slack;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.net.URI;

@ConfigurationProperties(prefix = "slack")
record SlackConfigurationProperties(URI webhook) {
}
