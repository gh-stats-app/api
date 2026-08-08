package ghstats.api.achievements.api;

import java.util.Optional;

public interface UnlockableAchievement extends AchievementDefinition {
    Optional<AchievementUnlocked> unlock(PullRequestContext context);
}
