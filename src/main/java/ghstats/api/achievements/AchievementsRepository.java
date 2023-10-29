package ghstats.api.achievements;

import ghstats.api.achievements.SqlAchievementsRepository.PersistedUserUnlockedAchievement;
import ghstats.api.achievements.api.AchievementUnlocked;
import ghstats.api.integrations.github.api.UserName;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.ZonedDateTime;
import java.util.Map;

interface AchievementsRepository {
    Mono<Long> saveAchievementUnlock(AchievementUnlocked achievementUnlocked);

    Flux<UnlockData> getLastUnlocked(int limit);

    Mono<Map<String, Long>> getUnlockedStats();

    Mono<Map<UserName, Long>> getScoreboard();

    Flux<PersistedUserUnlockedAchievement> getUnlockedAchievements(UserName userName);

    record UnlockData(UserName user, String achievementId, ZonedDateTime unlockedAt) {
    }
}
