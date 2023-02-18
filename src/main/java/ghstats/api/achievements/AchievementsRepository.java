package ghstats.api.achievements;

import ghstats.api.achievements.api.AchievementUnlocked;
import reactor.core.publisher.Mono;

interface AchievementsRepository {
    Mono<Long> saveAchievement(String achievementId, AchievementUnlocked achievementUnlocked);
}
