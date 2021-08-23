package app.ghstats.api.actions.web;

import app.ghstats.api.actions.ActionsCommand;
import app.ghstats.api.actions.ActionsQuery;
import app.ghstats.api.actions.api.ActionId;
import app.ghstats.api.actions.api.ReporterId;
import app.ghstats.api.actions.api.RepositoryName;
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
        return actionsCommand.markAction(ActionId.fromGithubString(markActionRequest.action()), RepositoryName.valueOf(markActionRequest.repository()), reporterId).map(it -> {
            if (it) return ResponseEntity.status(HttpStatus.CREATED).build();
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
        });
    }

    @GetMapping("/{user}/{actionName}")
    public Mono<ResponseEntity<List<LocalDateTime>>> getActionStats(@PathVariable ActionId.Owner user, @PathVariable ActionId.ActionName actionName) {
        return actionsQuery.getLastUsages(ActionId.valueOf(user, actionName)).collectList().map(ResponseEntity::ok);
    }
}
