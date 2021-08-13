package app.ghstats.api.actions;

import app.ghstats.api.domain.ActionId;
import app.ghstats.api.domain.RepositoryName;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.net.HttpHeaders;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.nio.file.Files;

@RestController
@RequestMapping("/actions")
class ActionsController {

    private final String bash;
    private final ActionsCommand actionsCommand;

    ActionsController(ResourceLoader resourceLoader, ActionsCommand actionsCommand) throws IOException {
        this.bash = new String(resourceLoader.getResource("classpath:/bash/action.sh").getInputStream().readAllBytes());
        this.actionsCommand = actionsCommand;
    }

    @GetMapping("/bash")
    public Mono<ResponseEntity<String>> bash() {
        return Mono.just(ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE, "application/octet-stream").body(bash));
    }

    @PostMapping
    public Mono<ResponseEntity<Void>> markActionUsage(@RequestBody MarkActionRequest markActionRequest) {
        actionsCommand.markAction(markActionRequest.action(), markActionRequest.repository());
        return Mono.just(ResponseEntity.status(HttpStatus.CREATED).build());
    }

    static record MarkActionRequest(
            @JsonProperty("repository") RepositoryName repository,
            @JsonProperty("action") ActionId action
    ) {
    }
}
