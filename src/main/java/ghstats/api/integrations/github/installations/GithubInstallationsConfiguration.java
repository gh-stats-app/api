package ghstats.api.integrations.github.installations;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.r2dbc.core.DatabaseClient;
import tools.jackson.databind.ObjectMapper;

@Configuration
class GithubInstallationsConfiguration {

    @Bean
    GithubInstallationsRepository githubInstallationsRepository(
            DatabaseClient databaseClient,
            ObjectMapper objectMapper
    ) {
        return new SqlGithubInstallationsRepository(databaseClient, objectMapper);
    }

    @Bean
    GithubInstallationsCommand githubInstallationsCommand(GithubInstallationsRepository repository) {
        return new GithubInstallationsCommand(repository);
    }
}
