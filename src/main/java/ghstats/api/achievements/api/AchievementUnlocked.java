package ghstats.api.achievements.api;

import ghstats.api.integrations.github.api.GitCommit;
import ghstats.api.integrations.github.api.UserName;

public record AchievementUnlocked(AchievementDefinition achievement, UserName recipient, GitCommit commit) {
}
