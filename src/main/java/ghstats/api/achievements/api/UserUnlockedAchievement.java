package ghstats.api.achievements.api;

import java.time.ZonedDateTime;

public record UserUnlockedAchievement(
        AchievementDefinition achievement,
        String commitId,
        ZonedDateTime unlockedAt
) {
}
