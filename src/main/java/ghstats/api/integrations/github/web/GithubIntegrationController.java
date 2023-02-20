package ghstats.api.integrations.github.web;

import ghstats.api.achievements.AchievementsCommand;
import ghstats.api.integrations.github.api.CommitAuthor;
import ghstats.api.integrations.github.api.CommitId;
import ghstats.api.integrations.github.api.GitCommit;
import ghstats.api.integrations.github.api.UserEmail;
import ghstats.api.integrations.github.api.UserName;
import ghstats.api.integrations.github.web.GithubWebhookRequest.GithubCommitRequestItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/integrations/github")
class GithubIntegrationController {

    private final AchievementsCommand achievementsCommand;
    private static final Logger logger = LoggerFactory.getLogger(GithubIntegrationController.class);

    GithubIntegrationController(AchievementsCommand achievementsCommand) {
        this.achievementsCommand = achievementsCommand;
    }

    @PostMapping("/events")
    Mono<ResponseEntity<Void>> onGithubEvent(@RequestBody GithubWebhookRequest githubWebhookRequest) {
        List<GitCommit> commits = githubWebhookRequest.commits()
                .stream()
                .map(GithubIntegrationController::toGitCommit)
                .filter(commit -> !commit.author().userName().value().contains("[bot]"))
                .collect(Collectors.toList());
        return achievementsCommand.analyseCommit(commits)
                .publishOn(Schedulers.fromExecutor(Executors.newSingleThreadExecutor()))
                .thenReturn(ResponseEntity.accepted().build());
    }

    @PostMapping("/installed")
    ResponseEntity<String> installed(@RequestBody String githubEvent) {
        logger.info("app installed" + githubEvent);
        return ResponseEntity.accepted().build();
    }

    private static GitCommit toGitCommit(GithubCommitRequestItem commit) {
        return new GitCommit(
                CommitId.valueOf(commit.id()),
                new CommitAuthor(UserName.valueOf(commit.author().username()), UserEmail.valueOf(commit.author().email())),
                commit.message(),
                commit.timestamp(),
                commit.added(),
                commit.removed(),
                commit.modified(),
                commit.url()
        );
    }
}
