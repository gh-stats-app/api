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
class WellRewriteThatLater implements UnlockableAchievement {

    @Override
    public String getId() {
        return "shell";
    }

    @Override
    public String getName() {
        return "We'll Rewrite that Later";
    }

    @Override
    public String getDescription() {
        return "Add Bash file to the repo";
    }

    @Override
    public Optional<AchievementUnlocked> unlock(PullRequestContext context) {
        return context.addedFiles().stream().anyMatch(filename -> filename.endsWith(".sh") || filename.endsWith(".bash"))
                ? Optional.of(new AchievementUnlocked(this, context.recipient().userName(), context.triggerCommit()))
                : Optional.empty();
    }
}
