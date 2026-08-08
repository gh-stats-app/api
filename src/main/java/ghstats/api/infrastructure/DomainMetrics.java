package ghstats.api.infrastructure;

import io.micrometer.core.instrument.DistributionSummary;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.stereotype.Component;

@Component
public class DomainMetrics {

    private final MeterRegistry meterRegistry;

    public DomainMetrics(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    public void githubWebhookReceived(String action, boolean merged) {
        meterRegistry.counter(
                "ghstats.github.webhook.received",
                "action", webhookAction(action),
                "merged", Boolean.toString(merged)
        ).increment();
    }

    public void githubWebhookSkipped(WebhookSkipReason reason) {
        meterRegistry.counter("ghstats.github.webhook.skipped", "reason", reason.tag).increment();
    }

    public void githubInstallationEvent(String action, String result) {
        meterRegistry.counter(
                "ghstats.github.installation.events",
                "action", installationAction(action),
                "result", result
        ).increment();
    }

    public Timer.Sample startGithubPullRequestProcessing() {
        return Timer.start(meterRegistry);
    }

    public void githubPullRequestCommits(int totalCommits, int ignoredBotCommits) {
        recordCommitSummary("total", totalCommits);
        recordCommitSummary("analysed", totalCommits - ignoredBotCommits);
        recordCommitSummary("ignored_bot", ignoredBotCommits);
    }

    public void githubPullRequestProcessed(Timer.Sample sample, PullRequestProcessingResult result) {
        meterRegistry.counter("ghstats.github.pull_request.processed", "result", result.tag).increment();
        sample.stop(meterRegistry.timer("ghstats.github.pull_request.processing", "result", result.tag));
    }

    public void achievementAnalysisStarted(int commitCount, int achievementCount) {
        meterRegistry.counter("ghstats.achievements.analysis.runs").increment();
        DistributionSummary.builder("ghstats.achievements.analysis.commits")
                .register(meterRegistry)
                .record(commitCount);
        DistributionSummary.builder("ghstats.achievements.analysis.definitions")
                .register(meterRegistry)
                .record(achievementCount);
    }

    public void achievementEvaluated(String achievementId) {
        meterRegistry.counter("ghstats.achievements.evaluated", "achievement", achievementId).increment();
    }

    public void achievementMatched(String achievementId) {
        meterRegistry.counter("ghstats.achievements.matches", "achievement", achievementId).increment();
    }

    public void achievementUnlockSaved(String achievementId, boolean created) {
        meterRegistry.counter(
                "ghstats.achievements.unlocks",
                "achievement", achievementId,
                "result", created ? "created" : "duplicate"
        ).increment();
    }

    private void recordCommitSummary(String type, int count) {
        DistributionSummary.builder("ghstats.github.pull_request.commits")
                .tag("type", type)
                .register(meterRegistry)
                .record(count);
    }

    private String webhookAction(String action) {
        if ("closed".equals(action)) {
            return "closed";
        }
        if ("ping".equals(action)) {
            return "ping";
        }
        if (action == null || action.isBlank()) {
            return "missing";
        }
        return "other";
    }

    private String installationAction(String action) {
        return switch (action) {
            case "created", "deleted", "suspend", "unsuspend", "new_permissions_accepted" -> action;
            default -> "other";
        };
    }

    public enum WebhookSkipReason {
        PING("ping"),
        NOT_MERGED("not_merged"),
        BOT_AUTHOR("bot_author"),
        MISSING_INSTALLATION("missing_installation"),
        UNSUPPORTED_EVENT("unsupported_event"),
        UNSUPPORTED_ACTION("unsupported_action"),
        INVALID_PAYLOAD("invalid_payload"),
        INVALID_SIGNATURE("invalid_signature");

        private final String tag;

        WebhookSkipReason(String tag) {
            this.tag = tag;
        }
    }

    public enum PullRequestProcessingResult {
        NO_UNLOCKS("no_unlocks"),
        UNLOCKS_COMMENTED("unlocks_commented"),
        ERROR("error");

        private final String tag;

        PullRequestProcessingResult(String tag) {
            this.tag = tag;
        }
    }
}
