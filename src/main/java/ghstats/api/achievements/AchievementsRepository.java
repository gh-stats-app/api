package ghstats.api.achievements;

import ghstats.api.achievements.api.AchievementUnlocked;
import reactor.core.publisher.Mono;

public interface AchievementsRepository {
    Mono<Integer> saveAchievement(String achievementId, AchievementUnlocked achievementUnlocked);
}
