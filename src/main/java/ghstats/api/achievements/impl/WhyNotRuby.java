package ghstats.api.achievements.impl;

import ghstats.api.achievements.api.AchievementUnlocked;
import ghstats.api.achievements.api.PullRequestContext;
import ghstats.api.integrations.github.api.GitCommit;
import ghstats.api.achievements.api.UnlockableAchievement;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Component
class WhyNotRuby implements UnlockableAchievement {

    @Override
    public String getId() {
        return "python";
    }

    @Override
    public String getName() {
        return "Why not Ruby?";
    }

    @Override
    public String getDescription() {
        return "Add Python file to the repo";
    }

    @Override
    public Optional<AchievementUnlocked> unlock(PullRequestContext context) {
        return context.addedFiles().stream().anyMatch(filename -> filename.endsWith(".py"))
                ? Optional.of(new AchievementUnlocked(this, context.recipient().userName(), context.triggerCommit()))
                : Optional.empty();
    }
}
