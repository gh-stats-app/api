package app.ghstats.api.notifications;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

import java.net.URI;

@ConstructorBinding
@ConfigurationProperties(prefix = "notifications")
record NotificationsConfigurationProperties(SlackConfigurationProperties slack) {
    static record SlackConfigurationProperties(URI webhook) {
    }
}
