package ghstats.api.achievements;

import ghstats.api.achievements.api.AchievementDefinition;
import ghstats.api.achievements.api.AchievementUnlockedFeedItem;
import ghstats.api.integrations.github.api.UserName;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.time.ZoneId;
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

    public Mono<List<UserAchievement>> getUnlockedAchievements(UserName userName) {
        return achievementsRepository.getUnlockedAchievements(userName)
                .map(it -> {
                    AchievementDefinition achievementDefinition = achievements.get(it.getFirst());
                    return UserAchievement.of(achievementDefinition, it.getSecond());
                }).collectList();
    }

    public record UserAchievement(
        String description,
        String name,
        String id,
        URI image,
        URI icon,
        Commit commit
    ) {
        public static UserAchievement of(AchievementDefinition achievementDefinition, String commitId) {
            return new UserAchievement(
                    achievementDefinition.getDescription(),
                    achievementDefinition.getName(),
                    achievementDefinition.getId(),
                    achievementDefinition.getImage(),
                    achievementDefinition.getIcon(),
                    new Commit("https://github.com/search?q="+ commitId +"&type=commits")
            );
        }
        record Commit(
                String url
        ) {}
    }

}
