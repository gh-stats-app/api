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
                .body("9edc6eb3e0bd61b0a5db88a19cb4c3eb17fc92408c705e01a7b64d3c05a29ba6688cf24d6ba38ee0afe766cba264788e454436e4a2feb35e10765e7934c4f7fb"));
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
