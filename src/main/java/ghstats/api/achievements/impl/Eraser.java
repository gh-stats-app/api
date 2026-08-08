package ghstats.api.achievements.impl;

import ghstats.api.achievements.api.AchievementUnlocked;
import ghstats.api.achievements.api.PullRequestContext;
import ghstats.api.achievements.api.UnlockableAchievement;
import ghstats.api.integrations.github.api.GitCommit;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
class Eraser implements UnlockableAchievement {

    @Override
    public String getId() {
        return "eraser";
    }

    @Override
    public String getName() {
        return "Eraser";
    }

    @Override
    public String getDescription() {
        return "Make a commit with no lines added, only deletions";
    }

    @Override
    public Optional<AchievementUnlocked> unlock(PullRequestContext context) {
        boolean onlyRemovesFiles = context.addedFiles().isEmpty()
                && !context.removedFiles().isEmpty()
                && context.modifiedFiles().isEmpty();
        return onlyRemovesFiles
                ? Optional.of(new AchievementUnlocked(this, context.recipient().userName(), context.triggerCommit()))
                : Optional.empty();
    }
}
