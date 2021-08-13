package app.ghstats.api.actions;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.r2dbc.core.DatabaseClient;

@Configuration
class ActionsConfiguration {

    @Bean
    ActionsQuery actionsQuery(DatabaseClient databaseClient, MeterRegistry meterRegistry) {
        return new ActionsQuery(databaseClient, meterRegistry);
    }

    @Bean
    ActionsCommand actionsCommand(DatabaseClient databaseClient, MeterRegistry meterRegistry) {
        return new ActionsCommand(databaseClient, meterRegistry);
    }

    @Bean
    MeterRegistry meterRegistry() {
        return new SimpleMeterRegistry();
    }
}
