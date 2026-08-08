package ghstats.api.achievements.impl;

import ghstats.api.achievements.api.AchievementUnlocked;
import ghstats.api.achievements.api.PullRequestContext;
import ghstats.api.achievements.api.UnlockableAchievement;
import ghstats.api.integrations.github.api.GitCommit;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Component
class Gitignore implements UnlockableAchievement {

    @Override
    public String getId() {
        return "for-stallman";
    }

    @Override
    public String getName() {
        return "Gitignore";
    }

    @Override
    public String getDescription() {
        return "Add .gitignore";
    }

    @Override
    public Optional<AchievementUnlocked> unlock(PullRequestContext context) {
        return context.addedFiles().stream().anyMatch(filename -> filename.endsWith(".gitignore"))
                ? Optional.of(new AchievementUnlocked(this, context.recipient().userName(), context.triggerCommit()))
                : Optional.empty();
    }
}
