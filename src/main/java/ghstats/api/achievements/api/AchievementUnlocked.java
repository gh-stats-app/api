package ghstats.api.achievements.api;

import ghstats.api.integrations.github.api.GitCommit;

public record AchievementUnlocked(AchievementDefinition achievement, GitCommit commit) {
}
