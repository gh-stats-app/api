package app.ghstats.api.actions;

import app.ghstats.api.domain.ActionId;
import org.springframework.r2dbc.core.DatabaseClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

public class ActionsQuery {

    private final DatabaseClient databaseClient;

    public ActionsQuery(DatabaseClient databaseClient) {
        this.databaseClient = databaseClient;
    }

    public Mono<Long> getUsageCount(ActionId actionId) {
        return databaseClient.sql("SELECT COUNT(id) FROM `stats` WHERE action LIKE ?")
                .bind(0, actionId.serialize())
                .map(it -> it.get(0, Long.class))
                .first();
    }

    public Mono<Long> getRepositoriesCount(ActionId actionId) {
        return databaseClient.sql("SELECT COUNT(DISTINCT(repository)) FROM `stats` WHERE action LIKE ?")
                .bind(0, actionId.serialize())
                .map(it -> it.get(0, Long.class))
                .first();
    }

    public Flux<LocalDateTime> getLastUsages(ActionId actionId) {
        return databaseClient.sql("SELECT * FROM `stats` WHERE action LIKE ? ORDER BY created_at DESC LIMIT 10")
                .bind(0, actionId.serialize())
                .map(it -> it.get("created_at", LocalDateTime.class))
                .all();
    }
}
