package app.ghstats.api.actions;

import app.ghstats.api.domain.ActionId;
import app.ghstats.api.domain.RepositoryName;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.net.HttpHeaders;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.time.Duration;

import static org.springframework.http.HttpHeaders.CACHE_CONTROL;

@RestController
@RequestMapping("/actions")
class ActionsController {

    private final String bash;
    private final ActionsCommand actionsCommand;

    ActionsController(ResourceLoader resourceLoader, ActionsCommand actionsCommand) throws IOException {
        this.bash = new String(resourceLoader.getResource("classpath:/bash/1.0/action.sh").getInputStream().readAllBytes());
        this.actionsCommand = actionsCommand;
    }

    @GetMapping("/bash/v1")
    public Mono<ResponseEntity<String>> bash() {
        return Mono.just(ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, "text/x-shellscript")
                .header(CACHE_CONTROL, CacheControl.maxAge(Duration.ofDays(7)).cachePublic().getHeaderValue())
                .body(bash)
        );
    }

    @GetMapping("/bash/v1.sha512")
    public Mono<ResponseEntity<String>> sha512() {
        return Mono.just(ResponseEntity.ok()
                .header(CACHE_CONTROL, CacheControl.maxAge(Duration.ofDays(7)).cachePublic().getHeaderValue())
                .body("b505b69ef70dede48573543a267ad75bb954b37c9ae1b961bcf8eea9f44b3f01f2884a1351b3fde51c1f4f8e1efc8e500a70d23195accf629cb708d541ff2c4a"));
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
