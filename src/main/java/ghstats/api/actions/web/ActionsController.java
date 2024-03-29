package ghstats.api.actions.web;

import ghstats.api.actions.ActionsCommand;
import ghstats.api.actions.ActionsQuery;
import ghstats.api.actions.api.ActionId;
import ghstats.api.actions.api.ActionId.ActionName;
import ghstats.api.actions.api.ActionId.Owner;
import ghstats.api.actions.api.ReporterId;
import ghstats.api.actions.api.RepositoryName;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/actions")
class ActionsController {

    private final ActionsCommand actionsCommand;
    private final ActionsQuery actionsQuery;

    ActionsController(ActionsCommand actionsCommand, ActionsQuery actionsQuery) {
        this.actionsCommand = actionsCommand;
        this.actionsQuery = actionsQuery;
    }

    @PostMapping
    public Mono<ResponseEntity<Void>> markActionUsage(@RequestBody MarkActionRequest markActionRequest,
                                                      @RequestHeader(name = "x-reporter", defaultValue = "unknown") ReporterId reporterId) {
        ActionId actionId = ActionId.fromGithubString(markActionRequest.action());
        String tag = markActionRequest.action().substring(markActionRequest.action().lastIndexOf('@') + 1).trim();
        return actionsCommand.markAction(actionId, RepositoryName.valueOf(markActionRequest.repository()), tag, reporterId).map(it -> {
            if (it) return ResponseEntity.status(HttpStatus.CREATED).build();
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
        });
    }

    @GetMapping("/{user}/{actionName}")
    public Mono<ResponseEntity<List<LocalDateTime>>> getActionStats(@PathVariable Owner user, @PathVariable String actionName) {
        String[] split = actionName.split("@", 2);
        String tag = split.length == 2 ? split[1] : null;
        ActionName repository = ActionName.valueOf(split[0]);
        return Optional.ofNullable(tag).map(tagValue -> actionsQuery.getLastUsages(ActionId.valueOf(user, repository), tagValue).collectList())
                .orElseGet(() -> actionsQuery.getLastUsages(ActionId.valueOf(user, repository)).collectList())
                .map(ResponseEntity::ok);
    }
}
