package ghstats.api.integrations.github.web;

import ghstats.api.achievements.AchievementsCommand;
import ghstats.api.achievements.api.AchievementUnlocked;
import ghstats.api.services.github.GithubClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/integrations/github")
class GithubIntegrationController {

    private static final String COMMENT_MARKER = "<!-- gh-stats-achievements -->";
    private static final Logger logger = LoggerFactory.getLogger(GithubIntegrationController.class);

    private final AchievementsCommand achievementsCommand;
    private final GithubClient githubClient;

    GithubIntegrationController(AchievementsCommand achievementsCommand, GithubClient githubClient) {
        this.achievementsCommand = achievementsCommand;
        this.githubClient = githubClient;
    }

    @PostMapping("/events")
    Mono<ResponseEntity<Void>> onGithubEvent(@RequestBody GithubPullRequestWebhookRequest request) {
        if (!"closed".equals(request.action()) || request.pullRequest() == null || !request.pullRequest().merged()) {
            return Mono.just(ResponseEntity.accepted().build());
        }
        if (request.installation() == null) {
            logger.warn("Ignoring merged PR event without GitHub App installation id");
            return Mono.just(ResponseEntity.accepted().build());
        }

        long installationId = request.installation().id();
        String owner = request.repository().owner().login();
        String repo = request.repository().name();
        int prNumber = request.number();

        return githubClient.fetchPrCommits(installationId, owner, repo, prNumber)
                .flatMap(commits -> {
                    var filtered = commits.stream()
                            .filter(c -> !c.author().userName().value().contains("[bot]"))
                            .toList();
                    return achievementsCommand.analyseCommits(filtered).collectList();
                })
                .flatMap(unlocks -> {
                    if (unlocks.isEmpty()) {
                        return Mono.just(ResponseEntity.accepted().<Void>build());
                    }
                    String comment = formatComment(unlocks);
                    return githubClient.createOrUpdatePrComment(installationId, owner, repo, prNumber, comment)
                            .thenReturn(ResponseEntity.accepted().<Void>build());
                })
                .doOnError(e -> logger.error("Error processing PR event for {}/{} #{}", owner, repo, prNumber, e))
                .onErrorReturn(ResponseEntity.accepted().build());
    }

    private String formatComment(List<AchievementUnlocked> unlocks) {
        String rows = unlocks.stream()
                .map(u -> "| ![](https://api.gh-stats.app/img/%s.png) | **%s** - %s | @%s |".formatted(
                        u.achievement().getId(),
                        u.achievement().getName(),
                        u.achievement().getDescription(),
                        u.commit().author().userName().value()))
                .collect(Collectors.joining("\n"));

        return """
                %s
                ## Achievements Unlocked

                | | Achievement | Unlocked by |
                |---|---|---|
                %s
                """.formatted(COMMENT_MARKER, rows);
    }
}
