package app.ghstats.api.achievements;

import app.ghstats.api.achievements.api.AchievementUnlocked;
import reactor.core.publisher.Mono;

public interface AchievementsRepository {
    Mono<Integer> saveAchievement(String achievementId, AchievementUnlocked achievementUnlocked);
}
