package ghstats.api.achievements.impl;

import ghstats.api.achievements.api.AchievementUnlocked;
import ghstats.api.achievements.api.PullRequestContext;
import ghstats.api.achievements.api.UnlockableAchievement;
import ghstats.api.integrations.github.api.PullRequestFile;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
class Mover implements UnlockableAchievement {

    @Override
    public String getId() {
        return "mover";
    }

    @Override
    public String getName() {
        return "Mover";
    }

    @Override
    public String getDescription() {
        return "Move or rename a file without changing its contents";
    }

    @Override
    public Optional<AchievementUnlocked> unlock(PullRequestContext context) {
        boolean containsPureRename = context.files().stream()
                .anyMatch(file -> file.status() == PullRequestFile.Status.RENAMED
                        && file.previousFilename() != null
                        && file.additions() == 0
                        && file.deletions() == 0);
        return containsPureRename
                ? Optional.of(new AchievementUnlocked(this, context.recipient().userName(), context.triggerCommit()))
                : Optional.empty();
    }
}
