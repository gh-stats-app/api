package app.ghstats.api.achievements;

import app.ghstats.api.achievements.api.Achievement;
import app.ghstats.api.achievements.api.GitCommit;
import app.ghstats.api.services.slack.SlackClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

public class AchievementsCommand {

    private final List<Achievement> achievements;
    private final AchievementsRepository achievementsRepository;
    private final SlackClient slackClient;

    public AchievementsCommand(
            List<Achievement> achievements,
            AchievementsRepository achievementsRepository,
            SlackClient slackClient
    ) {
        this.achievements = achievements;
        this.achievementsRepository = achievementsRepository;
        this.slackClient = slackClient;
    }

    public Mono<Integer> analyseCommit(List<GitCommit> commits) {
        List<Mono<Integer>> achievementsUnlocked = achievements.stream()
                .map(achievement -> achievement
                        .check(commits)
                        .map(achievementUnlocked -> Mono.zip(
                                achievementsRepository.saveAchievement(achievement.getId(), achievementUnlocked),
                                slackClient.sendMessage(achievement, achievementUnlocked.userName()),
                                (integer, unused) -> integer))
                        .orElseGet(() -> Mono.just(0)))
                .collect(Collectors.toList());
        return Flux.concat(achievementsUnlocked).reduce(Integer::sum);
    }
}
