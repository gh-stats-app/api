package app.ghstats.api.actions;

import app.ghstats.api.actions.api.ActionId;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

public class ActionsQuery {

    private final ActionsRepository actionsRepository;

    public ActionsQuery(ActionsRepository actionsRepository) {
        this.actionsRepository = actionsRepository;
    }

    public Mono<Long> getUsageCount(ActionId actionId) {
        return actionsRepository.getUsageCount(actionId);
    }

    public Mono<Long> getRepositoriesCount(ActionId actionId) {
        return actionsRepository.getUsedByRepositoriesCount(actionId);
    }

    public Flux<LocalDateTime> getLastUsages(ActionId actionId) {
        return actionsRepository.getLastUsages(actionId);
    }
}
