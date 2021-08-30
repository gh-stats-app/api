package app.ghstats.api.services.mailgun;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
class MailgunConfiguration {

    @Bean
    MailgunClient mailgunClient(MailgunConfigurationProperties configurationProperties) {
        WebClient webClient = WebClient.builder().build();
        return new MailgunClient(webClient, configurationProperties);
    }
}
