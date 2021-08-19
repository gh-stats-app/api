package app.ghstats.api.actions;

import app.ghstats.api.domain.ActionId;
import app.ghstats.api.domain.ReporterId;
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

    Mono<Boolean> markAction(ActionId actionId, RepositoryName repository, ReporterId reporterId) {
        meterRegistry.counter(actionId.toString()).increment();
        return databaseClient.sql("INSERT INTO `stats` (`repository`, `action`, `reporter`) VALUES (?, ?, ?)")
                .bind(0, repository.value())
                .bind(1, actionId.serialize())
                .bind(2, reporterId.value())
                .fetch()
                .rowsUpdated()
                .map(it -> 1 == it);
    }
}

