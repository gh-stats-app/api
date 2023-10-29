package ghstats.api.achievements;

import ghstats.api.achievements.api.AchievementDefinition;
import ghstats.api.achievements.api.AchievementUnlockedFeedItem;
import ghstats.api.achievements.api.UserUnlockedAchievement;
import ghstats.api.integrations.github.api.UserName;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class AchievementsQuery {

    private final Map<String, AchievementDefinition> achievements;
    private final AchievementsRepository achievementsRepository;

    public AchievementsQuery(List<AchievementDefinition> achievements, AchievementsRepository achievementsRepository) {
        this.achievements = achievements.stream().collect(Collectors.toMap(AchievementDefinition::getId, Function.identity()));
        this.achievementsRepository = achievementsRepository;
    }

    public List<AchievementDefinition> getAllDefinitions() {
        return achievements.values().stream().toList();
    }

    public Flux<AchievementUnlockedFeedItem> getFeed() {
        return achievementsRepository.getLastUnlocked(10).map(unlockData -> new AchievementUnlockedFeedItem(
                unlockData.user(),
                achievements.get(unlockData.achievementId()),
                unlockData.unlockedAt()
        ));
    }

    public Mono<Map<String, Long>> getUnlockedStats() {
        return achievementsRepository.getUnlockedStats();
    }

    public Mono<Map<UserName, Long>> getScoreBoard() {
        return achievementsRepository.getScoreboard();
    }

    public Flux<UserUnlockedAchievement> getUnlockedAchievements(UserName userName) {
        return achievementsRepository
                .getUnlockedAchievements(userName)
                .map(it -> new UserUnlockedAchievement(
                        achievements.get(it.achievementId()),
                        it.commitId(),
                        it.unlockedAt()
                ));
    }
}
