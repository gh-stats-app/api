package ghstats.api.services.slack;

import ghstats.api.achievements.api.AchievementUnlocked;
import reactor.core.publisher.Mono;

public interface NotificationChannel {
    Mono<Void> sendUnlockedMessage(AchievementUnlocked achievementUnlocked);
}
