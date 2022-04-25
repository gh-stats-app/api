package ghstats.api.actions;

import ghstats.api.actions.api.ActionId;
import ghstats.api.actions.api.ReporterId;
import ghstats.api.actions.api.RepositoryName;
import org.springframework.r2dbc.core.DatabaseClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

class SqlActionsRepository implements ActionsRepository {

    private final DatabaseClient databaseClient;

    public SqlActionsRepository(DatabaseClient databaseClient) {
        this.databaseClient = databaseClient;
    }

    @Override
    public Mono<Boolean> saveActionUsage(RepositoryName repository, ActionId actionId, String tag, ReporterId reporterId) {
        return databaseClient.sql("INSERT INTO `stats` (`repository`, `action`, `tag`, `reporter`) VALUES (?, ?, ?, ?)")
                .bind(0, repository.value())
                .bind(1, actionId.serialize())
                .bind(2, tag)
                .bind(3, reporterId.value())
                .fetch()
                .rowsUpdated()
                .map(it -> 1 == it);
    }

    @Override
    public Mono<Long> getUsageCount(ActionId actionId) {
        return databaseClient.sql("SELECT COUNT(id) FROM `stats` WHERE action LIKE ?")
                .bind(0, actionId.serialize())
                .map(it -> it.get(0, Long.class))
                .first();
    }

    @Override
    public Mono<Long> getUsageCount(ActionId actionId, String tag) {
        return databaseClient.sql("SELECT COUNT(id) FROM `stats` WHERE action LIKE ? and tag LIKE ?")
                .bind(0, actionId.serialize())
                .bind(1, tag)
                .map(it -> it.get(0, Long.class))
                .first();
    }

    @Override
    public Mono<Long> getUsedByRepositoriesCount(ActionId actionId) {
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

    @Override
    public Flux<LocalDateTime> getLastUsages(ActionId actionId, String tag) {
        return databaseClient.sql("SELECT * FROM `stats` WHERE action LIKE ? and tag LIKE ? ORDER BY created_at DESC LIMIT 10")
                .bind(0, actionId.serialize())
                .bind(1, tag)
                .map(it -> it.get("created_at", LocalDateTime.class))
                .all();
    }
}
