package ghstats.api.services.shields;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
class ShieldsConfiguration {

    @Bean
    ShieldsClient shieldsClient() {
        WebClient webClient = WebClient.builder().build();
        return new ShieldsClient(webClient);
    }
}
