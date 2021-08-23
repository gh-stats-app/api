package app.ghstats.api.achievements;

import app.ghstats.api.achievements.api.Achievement;
import app.ghstats.api.achievements.api.GitCommit;
import org.springframework.r2dbc.core.DatabaseClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

public class AchievementsCommand {

    private final List<Achievement> achievements;
    private final AchievementsRepository achievementsRepository;

    public AchievementsCommand(List<Achievement> achievements, AchievementsRepository achievementsRepository) {
        this.achievements = achievements;
        this.achievementsRepository = achievementsRepository;
    }

    public Mono<Integer> analyseCommit(List<GitCommit> commits) {
        List<Mono<Integer>> achievementsUnlocked = achievements.stream()
                .map(achievement -> achievement
                        .check(commits)
                        .map(achievementUnlocked -> achievementsRepository.saveAchievement(achievement.getId(), achievementUnlocked))
                        .orElseGet(() -> Mono.just(0)))
                .collect(Collectors.toList());
        return Flux.concat(achievementsUnlocked).reduce(Integer::sum);
    }
}
