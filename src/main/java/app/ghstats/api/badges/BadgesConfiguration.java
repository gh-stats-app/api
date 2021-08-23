package app.ghstats.api.badges;

import app.ghstats.api.actions.ActionsQuery;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class BadgesConfiguration {

    @Bean
    BadgesQuery badgesQuery(ActionsQuery actionsQuery) {
        WebClient webClient = WebClient.builder().build();
        return new BadgesQuery(actionsQuery, webClient);
    }
}
