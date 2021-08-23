package app.ghstats.api.actions;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.r2dbc.core.DatabaseClient;

@Configuration
public
class ActionsConfiguration {

    @Bean
    ActionsRepository actionsRepository(DatabaseClient databaseClient) {
        return new SqlActionsRepository(databaseClient);
    }

    @Bean
    ActionsQuery actionsQuery(ActionsRepository actionsRepository) {
        return new ActionsQuery(actionsRepository);
    }

    @Bean
    ActionsCommand actionsCommand(ActionsRepository actionsRepository, MeterRegistry meterRegistry) {
        return new ActionsCommand(actionsRepository, meterRegistry);
    }

    @Bean
    MeterRegistry meterRegistry() {
        return new SimpleMeterRegistry();
    }
}
