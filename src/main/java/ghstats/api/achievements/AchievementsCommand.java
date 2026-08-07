package ghstats.api.achievements;

import ghstats.api.achievements.api.AchievementUnlocked;
import ghstats.api.achievements.api.UnlockableAchievement;
import ghstats.api.infrastructure.DomainMetrics;
import ghstats.api.integrations.github.api.GitCommit;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.stream.Collectors;

public class AchievementsCommand {

    private final List<UnlockableAchievement> achievements;
    private final AchievementsRepository achievementsRepository;
    private final DomainMetrics domainMetrics;

    public AchievementsCommand(
            List<UnlockableAchievement> achievements,
            AchievementsRepository achievementsRepository,
            DomainMetrics domainMetrics
    ) {
        this.achievements = achievements;
        this.achievementsRepository = achievementsRepository;
        this.domainMetrics = domainMetrics;
    }

    public Flux<AchievementUnlocked> analyseCommits(List<GitCommit> commits) {
        return Flux.fromIterable(matchingUnlocks(commits))
                .concatMap(achievementUnlocked -> {
                    String achievementId = achievementUnlocked.achievement().getId();
                    return achievementsRepository
                            .saveAchievementUnlock(achievementUnlocked)
                            .doOnNext(savedRows -> domainMetrics.achievementUnlockSaved(achievementId, savedRows > 0))
                            .filter(savedRows -> savedRows > 0)
                            .map(savedRows -> achievementUnlocked);
                });
    }

    public Flux<AchievementUnlocked> previewCommits(List<GitCommit> commits) {
        return Flux.fromIterable(matchingUnlocks(commits))
                .concatMap(achievementUnlocked -> achievementsRepository
                        .getUnlockedAchievements(achievementUnlocked.commit().author().userName())
                        .filter(persisted -> achievementUnlocked.achievement().getId().equals(persisted.achievementId()))
                        .hasElements()
                        .filter(alreadyUnlocked -> !alreadyUnlocked)
                        .map(ignored -> achievementUnlocked));
    }

    private List<AchievementUnlocked> matchingUnlocks(List<GitCommit> commits) {
        domainMetrics.achievementAnalysisStarted(commits.size(), achievements.size());
        return achievements.stream()
                .map(achievement -> {
                    String achievementId = achievement.getId();
                    domainMetrics.achievementEvaluated(achievementId);
                    return achievement
                            .unlock(commits)
                            .map(achievementUnlocked -> {
                                domainMetrics.achievementMatched(achievementId);
                                return achievementUnlocked;
                            })
                            .stream();
                })
                .flatMap(unlocks -> unlocks)
                .collect(Collectors.toList());
    }
}
