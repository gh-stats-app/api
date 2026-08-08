package ghstats.api.integrations.github.web;

import ghstats.api.infrastructure.DomainMetrics;
import ghstats.api.infrastructure.DomainMetrics.WebhookSkipReason;
import ghstats.api.integrations.github.installations.GithubInstallation;
import ghstats.api.integrations.github.installations.GithubInstallationsCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Set;

@Component
class GithubInstallationWebhookHandler {

    private static final Logger logger = LoggerFactory.getLogger(GithubInstallationWebhookHandler.class);
    private static final Set<String> SUPPORTED_ACTIONS = Set.of(
            "created",
            "deleted",
            "suspend",
            "unsuspend",
            "new_permissions_accepted"
    );

    private final DomainMetrics domainMetrics;
    private final GithubInstallationsCommand installationsCommand;

    GithubInstallationWebhookHandler(
            DomainMetrics domainMetrics,
            GithubInstallationsCommand installationsCommand
    ) {
        this.domainMetrics = domainMetrics;
        this.installationsCommand = installationsCommand;
    }

    Mono<ResponseEntity<Void>> handle(GithubInstallationWebhookRequest request, String deliveryId) {
        if (request == null || request.installation() == null) {
            return invalid("missing installation");
        }
        String action = request.action();
        domainMetrics.githubWebhookReceived(action, false);
        if (action == null || !SUPPORTED_ACTIONS.contains(action)) {
            domainMetrics.githubWebhookSkipped(WebhookSkipReason.UNSUPPORTED_ACTION);
            logger.debug("Ignoring unsupported GitHub installation action '{}'", action);
            return accepted();
        }

        String invalidReason = invalidReason(request.installation());
        if (invalidReason != null) {
            return invalid(invalidReason);
        }

        var installation = toInstallation(request, deliveryId);
        return installationsCommand.record(installation)
                .doOnSuccess(ignored -> {
                    domainMetrics.githubInstallationEvent(action, "processed");
                    logger.info(
                            "Recorded GitHub installation event '{}' for installation {} ({})",
                            action,
                            installation.id(),
                            installation.account().login()
                    );
                })
                .doOnError(error -> {
                    domainMetrics.githubInstallationEvent(action, "error");
                    logger.error(
                            "Could not record GitHub installation event '{}' for installation {}",
                            action,
                            installation.id(),
                            error
                    );
                })
                .then(accepted());
    }

    private GithubInstallation toInstallation(GithubInstallationWebhookRequest request, String deliveryId) {
        var source = request.installation();
        var account = source.account();
        var now = OffsetDateTime.now(ZoneOffset.UTC);
        var eventTime = source.updatedAt() != null ? source.updatedAt() : now;
        var status = switch (request.action()) {
            case "deleted" -> GithubInstallation.Status.DELETED;
            case "suspend" -> GithubInstallation.Status.SUSPENDED;
            default -> source.suspendedAt() == null
                    ? GithubInstallation.Status.ACTIVE
                    : GithubInstallation.Status.SUSPENDED;
        };
        OffsetDateTime permissionsAcceptedAt = switch (request.action()) {
            case "created", "new_permissions_accepted" -> eventTime;
            default -> null;
        };
        OffsetDateTime deletedAt = "deleted".equals(request.action()) ? eventTime : null;

        return new GithubInstallation(
                source.id(),
                source.appId(),
                source.appSlug(),
                source.targetId(),
                new GithubInstallation.Account(account.id(), account.login(), account.type()),
                source.repositorySelection(),
                source.permissions(),
                status,
                source.createdAt(),
                source.updatedAt(),
                permissionsAcceptedAt,
                source.suspendedAt(),
                deletedAt,
                request.action(),
                deliveryId
        );
    }

    private String invalidReason(GithubInstallationWebhookRequest.Installation installation) {
        if (installation.id() == null) {
            return "missing installation id";
        }
        if (installation.appId() == null) {
            return "missing app id";
        }
        if (installation.account() == null) {
            return "missing installation account";
        }
        if (installation.account().id() == null) {
            return "missing installation account id";
        }
        if (installation.account().login() == null || installation.account().login().isBlank()) {
            return "missing installation account login";
        }
        return null;
    }

    private Mono<ResponseEntity<Void>> invalid(String reason) {
        domainMetrics.githubWebhookSkipped(WebhookSkipReason.INVALID_PAYLOAD);
        logger.warn("Ignoring GitHub installation webhook with invalid payload: {}", reason);
        return accepted();
    }

    private Mono<ResponseEntity<Void>> accepted() {
        return Mono.just(ResponseEntity.accepted().build());
    }
}
