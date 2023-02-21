package ghstats.api.achievements;

import ghstats.api.achievements.api.AchievementUnlocked;
import ghstats.api.integrations.github.api.UserName;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

interface AchievementsRepository {
    Mono<Long> saveAchievement(String achievementId, AchievementUnlocked achievementUnlocked);

    Flux<Map<UserName, String>> getLastUnlocked(int limit);

    Mono<Map<String, Long>> getUnlockedStats();

    Mono<Map<UserName, Long>> getScoreboard();

    Flux<String> getUnlockedAchievements(UserName userName);
}
