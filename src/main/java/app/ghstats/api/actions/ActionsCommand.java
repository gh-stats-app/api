package app.ghstats.api.actions;

import app.ghstats.api.actions.api.ActionId;
import app.ghstats.api.actions.api.ReporterId;
import app.ghstats.api.actions.api.RepositoryName;
import io.micrometer.core.instrument.MeterRegistry;
import reactor.core.publisher.Mono;

public class ActionsCommand {

    private final ActionsRepository actionsRepository;
    private final MeterRegistry meterRegistry;

    public ActionsCommand(ActionsRepository actionsRepository, MeterRegistry meterRegistry) {
        this.actionsRepository = actionsRepository;
        this.meterRegistry = meterRegistry;
    }

    public Mono<Boolean> markAction(ActionId actionId, RepositoryName repository, ReporterId reporterId) {
        meterRegistry.counter(actionId.toString()).increment();
        return actionsRepository.saveActionUsage(repository, actionId, reporterId);
    }
}

