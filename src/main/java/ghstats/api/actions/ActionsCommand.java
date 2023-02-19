package ghstats.api.actions;

import ghstats.api.actions.api.ActionId;
import ghstats.api.actions.api.ReporterId;
import ghstats.api.actions.api.RepositoryName;
import io.micrometer.core.instrument.MeterRegistry;
import reactor.core.publisher.Mono;

public class ActionsCommand {

    private final ActionsRepository actionsRepository;
    private final MeterRegistry meterRegistry;

    public ActionsCommand(ActionsRepository actionsRepository, MeterRegistry meterRegistry) {
        this.actionsRepository = actionsRepository;
        this.meterRegistry = meterRegistry;
    }

    public Mono<Boolean> markAction(ActionId actionId, RepositoryName repository, String tag, ReporterId reporterId) {
        meterRegistry.counter("action_" + actionId.toString()).increment();
        return actionsRepository.saveActionUsage(repository, actionId, tag, reporterId);
    }
}

