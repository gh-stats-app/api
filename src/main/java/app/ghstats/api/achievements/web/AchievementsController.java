package app.ghstats.api.achievements.web;

import app.ghstats.api.achievements.AchievementsCommand;
import app.ghstats.api.achievements.api.CommitId;
import app.ghstats.api.achievements.api.GitCommit;
import app.ghstats.api.achievements.api.UserName;
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
@RequestMapping("/achievements")
class AchievementsController {

    private final AchievementsCommand achievementsCommand;

    AchievementsController(AchievementsCommand achievementsCommand) {
        this.achievementsCommand = achievementsCommand;
    }

    @PostMapping
    public Mono<ResponseEntity<Void>> onGithubEvent(@RequestBody GithubWebhookRequest githubWebhookRequest) {
        List<GitCommit> commits = githubWebhookRequest.commits()
                .stream()
                .map(req -> new GitCommit(
                        CommitId.valueOf(req.id()),
                        UserName.valueOf(req.author().username()),
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
