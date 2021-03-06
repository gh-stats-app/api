package app.ghstats.api.actions;

import app.ghstats.api.actions.api.ActionId;
import app.ghstats.api.actions.api.ReporterId;
import app.ghstats.api.actions.api.RepositoryName;
import app.ghstats.api.notifications.NotificationsCommand;
import io.micrometer.core.instrument.MeterRegistry;
import reactor.core.publisher.Mono;

public class ActionsCommand {

    private final ActionsRepository actionsRepository;
    private final MeterRegistry meterRegistry;
    private final NotificationsCommand notificationsCommand;

    public ActionsCommand(ActionsRepository actionsRepository, NotificationsCommand notificationsCommand, MeterRegistry meterRegistry) {
        this.actionsRepository = actionsRepository;
        this.meterRegistry = meterRegistry;
        this.notificationsCommand = notificationsCommand;
    }

    public Mono<Boolean> markAction(ActionId actionId, RepositoryName repository, String tag, ReporterId reporterId) {
        meterRegistry.counter(actionId.toString()).increment();
        return actionsRepository.saveActionUsage(repository, actionId, tag, reporterId);
    }
}

