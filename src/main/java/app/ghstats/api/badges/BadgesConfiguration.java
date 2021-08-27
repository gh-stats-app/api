package app.ghstats.api.badges;

import app.ghstats.api.actions.ActionsQuery;
import app.ghstats.api.services.github.GithubClient;
import app.ghstats.api.services.shields.ShieldsClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BadgesConfiguration {

    @Bean
    BadgesQuery badgesQuery(ActionsQuery actionsQuery, ShieldsClient shieldsClient, GithubClient githubClient) {
        return new BadgesQuery(actionsQuery, shieldsClient, githubClient);
    }
}
