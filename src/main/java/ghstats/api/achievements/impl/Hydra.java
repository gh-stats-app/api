package ghstats.api.achievements.impl;

import ghstats.api.achievements.api.AchievementUnlocked;
import ghstats.api.achievements.api.PullRequestContext;
import ghstats.api.achievements.api.UnlockableAchievement;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
class Hydra implements UnlockableAchievement {

    @Override
    public String getId() {
        return "hydra";
    }

    @Override
    public String getName() {
        return "Hydra";
    }

    @Override
    public String getDescription() {
        return "Create a commit with 3 or more parents";
    }

    @Override
    public Optional<AchievementUnlocked> unlock(PullRequestContext context) {
        return context.commits().stream()
                .filter(commit -> commit.parents().size() >= 3)
                .findFirst()
                .map(commit -> new AchievementUnlocked(this, context.recipient().userName(), commit));
    }
}
