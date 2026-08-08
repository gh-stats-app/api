package ghstats.api.integrations.github.web;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/integrations/github")
class GithubIntegrationController {

    private final GithubWebhookProcessor processor;

    GithubIntegrationController(GithubWebhookProcessor processor) {
        this.processor = processor;
    }

    @PostMapping(value = "/events", consumes = MediaType.APPLICATION_JSON_VALUE)
    Mono<ResponseEntity<Void>> onGithubJsonEvent(
            @RequestHeader(name = "X-GitHub-Event", required = false) String event,
            @RequestHeader(name = "X-Hub-Signature-256", required = false) String signature256,
            @RequestHeader(name = "X-GitHub-Delivery", required = false) String deliveryId,
            @RequestBody String payload
    ) {
        return processor.handle(new GithubWebhookHeaders(event, signature256, deliveryId), payload);
    }
}
