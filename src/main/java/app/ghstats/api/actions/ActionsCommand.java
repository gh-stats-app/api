package app.ghstats.api.actions;

import app.ghstats.api.domain.ActionId;
import app.ghstats.api.domain.RepositoryName;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.r2dbc.core.DatabaseClient;
import reactor.core.publisher.Mono;

public class ActionsCommand {

    private final DatabaseClient databaseClient;
    private final MeterRegistry meterRegistry;

    public ActionsCommand(DatabaseClient databaseClient, MeterRegistry meterRegistry) {
        this.databaseClient = databaseClient;
        this.meterRegistry = meterRegistry;
    }

    Mono<Boolean> markAction(ActionId id, RepositoryName repository) {
        meterRegistry.counter(id.value()).increment();
        return databaseClient.sql("INSERT INTO `stats` (`repository`, `action`) VALUES (?, ?)")
                .bind(0, repository)
                .bind(1, id)
                .fetch()
                .rowsUpdated()
                .map(it -> 1 == it);
    }
}

