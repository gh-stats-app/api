package ghstats.api.integrations.github.web;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

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
            @RequestBody String payload
    ) {
        return processor.handle(new GithubWebhookHeaders(event, signature256), payload);
    }

    @PostMapping(value = "/events", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    Mono<ResponseEntity<Void>> onGithubFormEvent(
            @RequestHeader(name = "X-GitHub-Event", required = false) String event,
            @RequestHeader(name = "X-Hub-Signature-256", required = false) String signature256,
            ServerWebExchange exchange
    ) {
        return DataBufferUtils.join(exchange.getRequest().getBody())
                .map(dataBuffer -> {
                    byte[] bytes = new byte[dataBuffer.readableByteCount()];
                    dataBuffer.read(bytes);
                    DataBufferUtils.release(dataBuffer);
                    return new String(bytes, StandardCharsets.UTF_8);
                })
                .flatMap(body -> processor.handleForm(new GithubWebhookHeaders(event, signature256), body));
    }
}
