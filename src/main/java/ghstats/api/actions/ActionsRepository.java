package ghstats.api.actions;

import ghstats.api.actions.api.ActionId;
import ghstats.api.actions.api.ReporterId;
import ghstats.api.actions.api.RepositoryName;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

interface ActionsRepository {
    Mono<Boolean> saveActionUsage(RepositoryName repository, ActionId actionId, String tag, ReporterId reporterId);

    Mono<Long> getUsageCount(ActionId actionId);

    Mono<Long> getUsageCount(ActionId actionId, String tag);

    Mono<Long> getUsedByRepositoriesCount(ActionId actionId);

    Flux<LocalDateTime> getLastUsages(ActionId actionId);

    Flux<LocalDateTime> getLastUsages(ActionId actionId, String tag);
}
