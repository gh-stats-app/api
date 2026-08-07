package ghstats.api.integrations.github.web;

import com.github.bgalek.github.dotcom.models.EventPayload;
import com.google.common.base.Splitter;
import ghstats.api.infrastructure.DomainMetrics;
import ghstats.api.infrastructure.DomainMetrics.WebhookSkipReason;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

@Component
class GithubWebhookProcessor {

    private static final Logger logger = LoggerFactory.getLogger(GithubWebhookProcessor.class);

    private final DomainMetrics domainMetrics;
    private final GithubPullRequestWebhookHandler pullRequestHandler;
    private final GithubWebhookSignatureVerifier signatureVerifier;
    private final ObjectMapper objectMapper;

    GithubWebhookProcessor(
            DomainMetrics domainMetrics,
            GithubPullRequestWebhookHandler pullRequestHandler,
            GithubWebhookSignatureVerifier signatureVerifier,
            ObjectMapper objectMapper
    ) {
        this.domainMetrics = domainMetrics;
        this.pullRequestHandler = pullRequestHandler;
        this.signatureVerifier = signatureVerifier;
        this.objectMapper = objectMapper;
    }

    Mono<ResponseEntity<Void>> handle(GithubWebhookHeaders headers, String payload) {
        if (!signatureVerifier.isValid(payload, headers.signature256())) {
            domainMetrics.githubWebhookSkipped(WebhookSkipReason.INVALID_SIGNATURE);
            logger.warn("Rejecting GitHub webhook event '{}' with invalid signature", headers.event());
            return Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
        }
        return switch (GithubWebhookEvent.fromHeader(headers.event())) {
            case PING -> handlePing();
            case PULL_REQUEST -> handlePullRequestPayload(payload);
            case PUSH -> handleUnsupported(GithubWebhookEvent.PUSH.header);
            case UNSUPPORTED -> handleUnsupported(headers.event());
        };
    }

    Mono<ResponseEntity<Void>> handleForm(GithubWebhookHeaders headers, String body) {
        if (!signatureVerifier.isValid(body, headers.signature256())) {
            domainMetrics.githubWebhookSkipped(WebhookSkipReason.INVALID_SIGNATURE);
            logger.warn("Rejecting GitHub webhook form event '{}' with invalid signature", headers.event());
            return Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
        }

        String payload = formValue(body, "payload");
        if (payload == null || payload.isBlank()) {
            logger.warn("Ignoring GitHub webhook form request without payload");
            return accepted();
        }
        return handleVerified(headers, payload);
    }

    private Mono<ResponseEntity<Void>> handleVerified(GithubWebhookHeaders headers, String payload) {
        return switch (GithubWebhookEvent.fromHeader(headers.event())) {
            case PING -> handlePing();
            case PULL_REQUEST -> handlePullRequestPayload(payload);
            case PUSH -> handleUnsupported(GithubWebhookEvent.PUSH.header);
            case UNSUPPORTED -> handleUnsupported(headers.event());
        };
    }

    private Mono<ResponseEntity<Void>> handlePing() {
        domainMetrics.githubWebhookReceived(GithubWebhookEvent.PING.header, false);
        domainMetrics.githubWebhookSkipped(WebhookSkipReason.PING);
        return accepted();
    }

    private Mono<ResponseEntity<Void>> handleUnsupported(String event) {
        domainMetrics.githubWebhookReceived(event, false);
        domainMetrics.githubWebhookSkipped(WebhookSkipReason.UNSUPPORTED_EVENT);
        logger.debug("Ignoring unsupported GitHub webhook event '{}'", event);
        return accepted();
    }

    private Mono<ResponseEntity<Void>> handlePullRequestPayload(String payload) {
        try {
            JsonNode root = objectMapper.readTree(payload);
            EventPayload event = objectMapper.treeToValue(root, EventPayload.class);
            GithubPullRequestWebhookRequest request = objectMapper.treeToValue(root, GithubPullRequestWebhookRequest.class);
            return pullRequestHandler.handle(event, request);
        } catch (JacksonException e) {
            return invalid(e.getMessage(), e);
        }
    }

    private Mono<ResponseEntity<Void>> invalid(String reason, Exception e) {
        domainMetrics.githubWebhookSkipped(WebhookSkipReason.INVALID_PAYLOAD);
        logger.warn("Ignoring GitHub webhook with invalid payload: {}", reason);
        logger.debug("Invalid GitHub webhook payload", e);
        return accepted();
    }

    private Mono<ResponseEntity<Void>> accepted() {
        return Mono.just(ResponseEntity.accepted().build());
    }

    private String formValue(String body, String name) {
        if (body == null || body.isBlank()) {
            return null;
        }
        for (String part : Splitter.on('&').split(body)) {
            int separator = part.indexOf('=');
            String rawName = separator >= 0 ? part.substring(0, separator) : part;
            String rawValue = separator >= 0 ? part.substring(separator + 1) : "";
            String decodedName = URLDecoder.decode(rawName, StandardCharsets.UTF_8);
            if (name.equals(decodedName)) {
                return URLDecoder.decode(rawValue, StandardCharsets.UTF_8);
            }
        }
        return null;
    }
}
