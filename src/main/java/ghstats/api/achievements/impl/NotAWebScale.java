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
class NotAWebScale implements UnlockableAchievement {

    @Override
    public String getId() {
        return "sql";
    }

    @Override
    public String getName() {
        return "Not a Web Scale";
    }

    @Override
    public String getDescription() {
        return "Add SQL file to the repo";
    }

    @Override
    public Optional<AchievementUnlocked> unlock(PullRequestContext context) {
        return context.addedFiles().stream().anyMatch(filename -> filename.endsWith(".sql"))
                ? Optional.of(new AchievementUnlocked(this, context.recipient().userName(), context.triggerCommit()))
                : Optional.empty();
    }
}
