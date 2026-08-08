package ghstats.api.achievements.impl;

import ghstats.api.achievements.api.AchievementUnlocked;
import ghstats.api.achievements.api.PullRequestContext;
import ghstats.api.achievements.api.UnlockableAchievement;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
class Massive implements UnlockableAchievement {

    private static final long ADDITION_THRESHOLD = 1_000;

    @Override
    public String getId() {
        return "massive";
    }

    @Override
    public String getName() {
        return "Massive";
    }

    @Override
    public String getDescription() {
        return "Add more than 1000 lines in one pull request";
    }

    @Override
    public Optional<AchievementUnlocked> unlock(PullRequestContext context) {
        long additions = context.files().stream()
                .mapToLong(file -> file.additions())
                .sum();
        return additions > ADDITION_THRESHOLD
                ? Optional.of(new AchievementUnlocked(this, context.recipient().userName(), context.triggerCommit()))
                : Optional.empty();
    }
}
