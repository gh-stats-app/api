package app.ghstats.api.services.slack;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

import java.net.URI;

@ConstructorBinding
@ConfigurationProperties(prefix = "slack")
record SlackConfigurationProperties(URI webhook) {
}
