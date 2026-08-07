package ghstats.api.integrations.github.web;

import com.github.bgalek.github.dotcom.models.EventPayload;
import ghstats.api.achievements.AchievementsCommand;
import ghstats.api.infrastructure.DomainMetrics;
import ghstats.api.infrastructure.DomainMetrics.PullRequestProcessingResult;
import ghstats.api.infrastructure.DomainMetrics.WebhookSkipReason;
import ghstats.api.services.github.GithubClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
class GithubPullRequestWebhookHandler {

    private static final Logger logger = LoggerFactory.getLogger(GithubPullRequestWebhookHandler.class);

    private final AchievementsCommand achievementsCommand;
    private final DomainMetrics domainMetrics;
    private final GithubClient githubClient;
    private final GithubAchievementCommentFormatter commentFormatter;

    GithubPullRequestWebhookHandler(
            AchievementsCommand achievementsCommand,
            DomainMetrics domainMetrics,
            GithubClient githubClient,
            GithubAchievementCommentFormatter commentFormatter
    ) {
        this.achievementsCommand = achievementsCommand;
        this.domainMetrics = domainMetrics;
        this.githubClient = githubClient;
        this.commentFormatter = commentFormatter;
    }

    Mono<ResponseEntity<Void>> handle(EventPayload event, GithubPullRequestWebhookRequest request) {
        if (event == null || request == null) {
            return invalid("empty JSON body");
        }
        boolean merged = request.pullRequest() != null && Boolean.TRUE.equals(request.pullRequest().merged());
        domainMetrics.githubWebhookReceived(event.getAction(), merged);
        if (!shouldProcess(event.getAction(), merged)) {
            domainMetrics.githubWebhookSkipped(WebhookSkipReason.NOT_MERGED);
            return accepted();
        }
        String invalidPayloadReason = invalidPayloadReason(event, request);
        if (invalidPayloadReason != null) {
            return invalid(invalidPayloadReason);
        }

        String owner = request.repository().owner().login();
        String repo = request.repository().name();
        int prNumber = event.getNumber();
        Long installationId = installationId(request);
        if (installationId == null) {
            logger.warn("Ignoring PR event for {}/{} #{} without GitHub App installation id", owner, repo, prNumber);
            domainMetrics.githubWebhookSkipped(WebhookSkipReason.MISSING_INSTALLATION);
            return accepted();
        }
        if (merged) {
            return unlockMergedPullRequest(installationId, owner, repo, prNumber);
        }
        return previewPullRequest(installationId, owner, repo, prNumber);
    }

    private Mono<ResponseEntity<Void>> previewPullRequest(long installationId, String owner, String repo, int prNumber) {
        var processingTimer = domainMetrics.startGithubPullRequestProcessing();

        return githubClient.fetchPrCommits(installationId, owner, repo, prNumber)
                .flatMap(commits -> {
                    var filtered = commits.stream()
                            .filter(c -> !c.author().userName().value().contains("[bot]"))
                            .toList();
                    domainMetrics.githubPullRequestCommits(commits.size(), commits.size() - filtered.size());
                    return achievementsCommand.previewCommits(filtered).collectList();
                })
                .flatMap(unlocks -> {
                    if (unlocks.isEmpty()) {
                        domainMetrics.githubPullRequestProcessed(processingTimer, PullRequestProcessingResult.NO_UNLOCKS);
                        return accepted();
                    }
                    String userName = unlocks.getFirst().commit().author().userName().value();
                    String comment = commentFormatter.formatPreview(userName);
                    return githubClient.createOrUpdatePrComment(installationId, owner, repo, prNumber, comment)
                            .doOnSuccess(ignored -> domainMetrics.githubPullRequestProcessed(processingTimer, PullRequestProcessingResult.UNLOCKS_COMMENTED))
                            .then(accepted());
                })
                .doOnError(e -> {
                    domainMetrics.githubPullRequestProcessed(processingTimer, PullRequestProcessingResult.ERROR);
                    logger.error("Error previewing PR event for {}/{} #{}", owner, repo, prNumber, e);
                })
                .onErrorResume(ignored -> accepted());
    }

    private Mono<ResponseEntity<Void>> unlockMergedPullRequest(long installationId, String owner, String repo, int prNumber) {
        var processingTimer = domainMetrics.startGithubPullRequestProcessing();

        return githubClient.fetchPrCommits(installationId, owner, repo, prNumber)
                .flatMap(commits -> {
                    var filtered = commits.stream()
                            .filter(c -> !c.author().userName().value().contains("[bot]"))
                            .toList();
                    domainMetrics.githubPullRequestCommits(commits.size(), commits.size() - filtered.size());
                    return achievementsCommand.analyseCommits(filtered).collectList();
                })
                .flatMap(unlocks -> {
                    if (unlocks.isEmpty()) {
                        domainMetrics.githubPullRequestProcessed(processingTimer, PullRequestProcessingResult.NO_UNLOCKS);
                        return accepted();
                    }
                    String comment = commentFormatter.format(unlocks);
                    return githubClient.createOrUpdatePrComment(installationId, owner, repo, prNumber, comment)
                            .doOnSuccess(ignored -> domainMetrics.githubPullRequestProcessed(processingTimer, PullRequestProcessingResult.UNLOCKS_COMMENTED))
                            .then(accepted());
                })
                .doOnError(e -> {
                    domainMetrics.githubPullRequestProcessed(processingTimer, PullRequestProcessingResult.ERROR);
                    logger.error("Error processing PR event for {}/{} #{}", owner, repo, prNumber, e);
                })
                .onErrorResume(ignored -> accepted());
    }

    private boolean shouldProcess(String action, boolean merged) {
        if ("closed".equals(action)) {
            return merged;
        }
        return "opened".equals(action) ||
                "reopened".equals(action) ||
                "synchronize".equals(action) ||
                "ready_for_review".equals(action);
    }

    private String invalidPayloadReason(EventPayload event, GithubPullRequestWebhookRequest request) {
        if (event.getNumber() == null) {
            return "missing pull request number";
        }
        if (request.repository() == null) {
            return "missing repository";
        }
        if (request.repository().name() == null || request.repository().name().isBlank()) {
            return "missing repository name";
        }
        if (request.repository().owner() == null) {
            return "missing repository owner";
        }
        if (request.repository().owner().login() == null || request.repository().owner().login().isBlank()) {
            return "missing repository owner login";
        }
        return null;
    }

    private Long installationId(GithubPullRequestWebhookRequest request) {
        return request.installation() == null ? null : request.installation().id();
    }

    private Mono<ResponseEntity<Void>> invalid(String reason) {
        domainMetrics.githubWebhookSkipped(WebhookSkipReason.INVALID_PAYLOAD);
        logger.warn("Ignoring GitHub webhook with invalid payload: {}", reason);
        return accepted();
    }

    private Mono<ResponseEntity<Void>> accepted() {
        return Mono.just(ResponseEntity.accepted().build());
    }
}
