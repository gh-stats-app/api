package ghstats.api.integrations.github.web;

import com.github.bgalek.github.dotcom.models.EventPayload;
import ghstats.api.achievements.AchievementsCommand;
import ghstats.api.achievements.api.AchievementUnlocked;
import ghstats.api.achievements.api.PullRequestContext;
import ghstats.api.achievements.api.PullRequestSnapshot;
import ghstats.api.infrastructure.DomainMetrics;
import ghstats.api.infrastructure.DomainMetrics.PullRequestProcessingResult;
import ghstats.api.infrastructure.DomainMetrics.WebhookSkipReason;
import ghstats.api.services.github.GithubClient;
import ghstats.api.integrations.github.api.CommitId;
import ghstats.api.integrations.github.api.GithubUser;
import ghstats.api.integrations.github.api.OrganisationName;
import ghstats.api.integrations.github.api.RepositoryName;
import ghstats.api.integrations.github.api.UserName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.List;

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
        if (!"closed".equals(event.getAction())) {
            domainMetrics.githubWebhookSkipped(WebhookSkipReason.UNSUPPORTED_ACTION);
            return accepted();
        }
        if (!merged) {
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
        var recipient = new GithubUser(
                UserName.valueOf(request.pullRequest().user().login()),
                request.pullRequest().user().type()
        );
        if (recipient.isBot()) {
            domainMetrics.githubWebhookSkipped(WebhookSkipReason.BOT_AUTHOR);
            return accepted();
        }
        return unlockMergedPullRequest(installationId, owner, repo, prNumber, recipient, request.pullRequest());
    }

    private Mono<ResponseEntity<Void>> unlockMergedPullRequest(
            long installationId,
            String owner,
            String repo,
            int prNumber,
            GithubUser recipient,
            GithubPullRequestWebhookRequest.PullRequest pullRequest
    ) {
        var processingTimer = domainMetrics.startGithubPullRequestProcessing();

        return githubClient.fetchPullRequestEvidence(installationId, owner, repo, prNumber)
                .flatMap(evidence -> {
                    var filtered = evidence.commits().stream()
                            .filter(commit -> !isBot(commit.author().userName()))
                            .toList();
                    domainMetrics.githubPullRequestCommits(
                            evidence.commits().size(),
                            evidence.commits().size() - filtered.size()
                    );
                    if (filtered.isEmpty()) {
                        return Mono.just(List.<AchievementUnlocked>of());
                    }
                    var snapshot = new PullRequestSnapshot(
                            OrganisationName.valueOf(owner),
                            RepositoryName.valueOf(repo),
                            prNumber,
                            CommitId.valueOf(pullRequest.head().sha()),
                            URI.create(pullRequest.htmlUrl() != null
                                    ? pullRequest.htmlUrl()
                                    : "https://github.com/%s/%s/pull/%d".formatted(owner, repo, prNumber))
                    );
                    var context = new PullRequestContext(recipient, snapshot, filtered, evidence.files());
                    return achievementsCommand.analysePullRequest(context).collectList();
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
        if (request.pullRequest() == null || request.pullRequest().user() == null) {
            return "missing pull request author";
        }
        if (request.pullRequest().user().login() == null || request.pullRequest().user().login().isBlank()) {
            return "missing pull request author login";
        }
        if (request.pullRequest().head() == null || request.pullRequest().head().sha() == null || request.pullRequest().head().sha().isBlank()) {
            return "missing pull request head SHA";
        }
        return null;
    }

    private boolean isBot(UserName userName) {
        return userName.value().endsWith("[bot]");
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
