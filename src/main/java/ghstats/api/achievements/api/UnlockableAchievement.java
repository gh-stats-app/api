package ghstats.api.achievements.api;

import ghstats.api.integrations.github.api.GitCommit;

import java.util.List;
import java.util.Optional;

public interface UnlockableAchievement extends AchievementDefinition {
    Optional<AchievementUnlocked> unlock(List<GitCommit> commits);
}
