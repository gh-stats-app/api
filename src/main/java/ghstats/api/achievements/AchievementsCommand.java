package ghstats.api.achievements;

import ghstats.api.achievements.api.AchievementUnlocked;
import ghstats.api.achievements.api.UnlockableAchievement;
import ghstats.api.integrations.github.api.GitCommit;
import io.micrometer.core.instrument.MeterRegistry;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

public class AchievementsCommand {

    private final List<UnlockableAchievement> achievements;
    private final AchievementsRepository achievementsRepository;
    private final MeterRegistry meterRegistry;

    public AchievementsCommand(
            List<UnlockableAchievement> achievements,
            AchievementsRepository achievementsRepository,
            MeterRegistry meterRegistry
    ) {
        this.achievements = achievements;
        this.achievementsRepository = achievementsRepository;
        this.meterRegistry = meterRegistry;
    }

    public Flux<AchievementUnlocked> analyseCommits(List<GitCommit> commits) {
        List<Mono<AchievementUnlocked>> attemptedUnlocks = achievements.stream()
                .map(achievement -> achievement
                        .unlock(commits)
                        .map(achievementUnlocked -> {
                            meterRegistry.counter("achievement_" + achievement.getId()).increment();
                            return achievementsRepository
                                    .saveAchievementUnlock(achievementUnlocked)
                                    .filter(it -> it > 0)
                                    .map(it -> achievementUnlocked);
                        })
                        .orElseGet(Mono::empty))
                .collect(Collectors.toList());
        return Flux.concat(attemptedUnlocks);
    }
}
