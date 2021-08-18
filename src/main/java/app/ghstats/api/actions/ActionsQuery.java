package app.ghstats.api.actions;

import app.ghstats.api.domain.ActionId;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.r2dbc.core.DatabaseClient;
import reactor.core.publisher.Mono;

public class ActionsQuery {

    private final DatabaseClient databaseClient;
    private final MeterRegistry meterRegistry;

    public ActionsQuery(DatabaseClient databaseClient, MeterRegistry meterRegistry) {
        this.databaseClient = databaseClient;
        this.meterRegistry = meterRegistry;
    }

    public Mono<Long> getUsage(ActionId actionId) {
        ;
        return databaseClient.sql("SELECT COUNT(DISTINCT(repository)) FROM `stats` WHERE action LIKE ?")
                .bind(0, PersistedActionId.valueOf(actionId).value())
                .map(it -> it.get(0, Long.class))
                .first();
    }

    private record PersistedActionId(String value) {
        static PersistedActionId valueOf(ActionId actionId) {
            return new PersistedActionId(actionId.value());
        }

        public String value() {
            return "__%s".formatted(value.replaceFirst("/", "_"));
        }
    }
}
