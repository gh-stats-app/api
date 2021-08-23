package app.ghstats.api.actions;

import app.ghstats.api.actions.api.ActionId;
import app.ghstats.api.actions.api.ReporterId;
import app.ghstats.api.actions.api.RepositoryName;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

interface ActionsRepository {
    Mono<Boolean> saveActionUsage(RepositoryName repository, ActionId actionId, ReporterId reporterId);

    Mono<Long> getUsageCount(ActionId actionId);

    Mono<Long> getUsedByRepositoriesCount(ActionId actionId);

    Flux<LocalDateTime> getLastUsages(ActionId actionId);
}
