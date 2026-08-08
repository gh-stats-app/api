package ghstats.api.integrations.github.web;

import com.github.bgalek.github.dotcom.models.EventPayload;
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

@Component
class GithubWebhookProcessor {

    private static final Logger logger = LoggerFactory.getLogger(GithubWebhookProcessor.class);

    private final DomainMetrics domainMetrics;
    private final GithubInstallationWebhookHandler installationHandler;
    private final GithubPullRequestWebhookHandler pullRequestHandler;
    private final GithubWebhookSignatureVerifier signatureVerifier;
    private final ObjectMapper objectMapper;

    GithubWebhookProcessor(
            DomainMetrics domainMetrics,
            GithubInstallationWebhookHandler installationHandler,
            GithubPullRequestWebhookHandler pullRequestHandler,
            GithubWebhookSignatureVerifier signatureVerifier,
            ObjectMapper objectMapper
    ) {
        this.domainMetrics = domainMetrics;
        this.installationHandler = installationHandler;
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
        return handleEvent(headers, payload);
    }

    private Mono<ResponseEntity<Void>> handleEvent(GithubWebhookHeaders headers, String payload) {
        return switch (GithubWebhookEvent.fromHeader(headers.event())) {
            case PING -> handlePing();
            case INSTALLATION -> handleInstallationPayload(payload, headers.deliveryId());
            case PULL_REQUEST -> handlePullRequestPayload(payload);
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

    private Mono<ResponseEntity<Void>> handleInstallationPayload(String payload, String deliveryId) {
        try {
            var request = objectMapper.readValue(payload, GithubInstallationWebhookRequest.class);
            return installationHandler.handle(request, deliveryId);
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

}
