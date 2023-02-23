package ghstats.api.achievements.api;

import ghstats.api.integrations.github.api.UserName;

import java.time.ZonedDateTime;

public record AchievementUnlockedFeedItem(
        UserName userName,
        AchievementDefinition achievementDefinition,
        ZonedDateTime unlockedAt
) {
}
