package ghstats.api.achievements;

import ghstats.api.achievements.api.Achievement;
import ghstats.api.achievements.api.GitCommit;
import ghstats.api.notifications.NotificationsCommand;
import io.micrometer.core.instrument.MeterRegistry;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

public class AchievementsCommand {

    private final List<Achievement> achievements;
    private final AchievementsRepository achievementsRepository;
    private final NotificationsCommand notificationsCommand;
    private final MeterRegistry meterRegistry;

    public AchievementsCommand(
            List<Achievement> achievements,
            AchievementsRepository achievementsRepository,
            NotificationsCommand notificationsCommand,
            MeterRegistry meterRegistry
    ) {
        this.achievements = achievements;
        this.achievementsRepository = achievementsRepository;
        this.notificationsCommand = notificationsCommand;
        this.meterRegistry = meterRegistry;
    }

    public Mono<Long> analyseCommit(List<GitCommit> commits) {
        List<Mono<Long>> achievementsUnlocked = achievements.stream()
                .map(achievement -> achievement
                        .unlock(commits)
                        .map(achievementUnlocked -> {
                            meterRegistry.counter("achievement_" + achievement.getId(), "achievement");
                            return achievementsRepository
                                    .saveAchievement(achievement.getId(), achievementUnlocked)
                                    .filter(it -> it > 0)
                                    .flatMap(it -> notificationsCommand.notify(achievementUnlocked).then(Mono.just(it)));
                        })
                        .orElseGet(() -> Mono.just(0L)))
                .collect(Collectors.toList());
        return Flux.concat(achievementsUnlocked).reduce(Long::sum);
    }
}
