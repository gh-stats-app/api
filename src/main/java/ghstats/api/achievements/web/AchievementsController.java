package ghstats.api.achievements.web;

import ghstats.api.achievements.AchievementsCommand;
import ghstats.api.achievements.AchievementsQuery;
import ghstats.api.achievements.api.CommitAuthor;
import ghstats.api.achievements.api.CommitId;
import ghstats.api.achievements.api.GitCommit;
import ghstats.api.achievements.api.UserEmail;
import ghstats.api.achievements.api.UserName;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.net.URI;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/achievements")
class AchievementsController {

    private final AchievementsCommand achievementsCommand;
    private final AchievementsQuery achievementsQuery;

    AchievementsController(AchievementsCommand achievementsCommand, AchievementsQuery achievementsQuery) {
        this.achievementsCommand = achievementsCommand;
        this.achievementsQuery = achievementsQuery;
    }

    @GetMapping
    List<AchievementResponse> listAchievements() {
        return achievementsQuery.getAll()
                .stream()
                .map(it -> new AchievementResponse(
                        it.getId(),
                        it.getDescription(),
                        it.getImage(),
                        it.getIcon()
                ))
                .collect(Collectors.toList());
    }

    @PostMapping
    Mono<ResponseEntity<Void>> onGithubEvent(@RequestBody GithubWebhookRequest githubWebhookRequest, @RequestParam(value = "slack", required = false) URI slackWebhook) {
        List<GitCommit> commits = githubWebhookRequest.commits()
                .stream()
                .map(req -> new GitCommit(
                        CommitId.valueOf(req.id()),
                        new CommitAuthor(UserName.valueOf(req.author().username()), UserEmail.valueOf(req.author().email())),
                        req.message(),
                        req.timestamp(),
                        req.added(),
                        req.removed(),
                        req.modified())
                )
                .collect(Collectors.toList());
        return achievementsCommand.analyseCommit(commits)
                .publishOn(Schedulers.fromExecutor(Executors.newSingleThreadExecutor()))
                .thenReturn(ResponseEntity.accepted().build());
    }
}
