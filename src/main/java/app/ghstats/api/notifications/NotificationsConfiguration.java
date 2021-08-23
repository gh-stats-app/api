package app.ghstats.api.notifications;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
class NotificationsConfiguration {

    @Bean
    WebClient notificationWebClient() {
        return WebClient.builder().build();
    }

    @Bean
    NotificationsCommand notificationsCommand(WebClient notificationWebClient,
                                              NotificationsConfigurationProperties configurationProperties) {
        return new NotificationsCommand(notificationWebClient, configurationProperties);
    }
}
