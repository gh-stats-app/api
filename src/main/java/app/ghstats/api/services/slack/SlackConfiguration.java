package app.ghstats.api.services.slack;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
class SlackConfiguration {

    @Bean
    SlackClient slackClient(SlackConfigurationProperties configurationProperties) {
        WebClient webClient = WebClient.builder().build();
        return new SlackClient(webClient, configurationProperties);
    }
}
