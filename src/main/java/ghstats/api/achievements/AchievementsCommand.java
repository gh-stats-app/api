package ghstats.api.achievements;

import ghstats.api.achievements.api.Achievement;
import ghstats.api.achievements.api.GitCommit;
import ghstats.api.notifications.NotificationsCommand;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

public class AchievementsCommand {

    private final List<Achievement> achievements;
    private final AchievementsRepository achievementsRepository;
    private final NotificationsCommand notificationsCommand;

    public AchievementsCommand(
            List<Achievement> achievements,
            AchievementsRepository achievementsRepository,
            NotificationsCommand notificationsCommand) {
        this.achievements = achievements;
        this.achievementsRepository = achievementsRepository;
        this.notificationsCommand = notificationsCommand;
    }

    public Mono<Integer> analyseCommit(List<GitCommit> commits) {
        List<Mono<Integer>> achievementsUnlocked = achievements.stream()
                .map(achievement -> achievement
                        .unlock(commits)
                        .map(achievementUnlocked -> achievementsRepository
                                .saveAchievement(achievement.getId(), achievementUnlocked)
                                .filter(it -> it > 0)
                                .flatMap(it -> notificationsCommand.notify(achievementUnlocked).then(Mono.just(it))))
                        .orElseGet(() -> Mono.just(0)))
                .collect(Collectors.toList());
        return Flux.concat(achievementsUnlocked).reduce(Integer::sum);
    }
}
