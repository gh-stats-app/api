package ghstats.api.achievements.impl;

import ghstats.api.achievements.api.AchievementUnlocked;
import ghstats.api.achievements.api.UnlockableAchievement;
import ghstats.api.integrations.github.api.GitCommit;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
class EmptyCommit implements UnlockableAchievement {

    @Override
    public String getId() {
        return "empty-commit";
    }

    @Override
    public String getName() {
        return "<empty title>";
    }

    @Override
    public String getDescription() {
        return "Make an empty commit";
    }

    @Override
    public Optional<AchievementUnlocked> unlock(List<GitCommit> commits) {
        return commits.stream()
                .filter(it -> it.added().isEmpty() && it.removed().isEmpty() && it.modified().isEmpty())
                .findAny()
                .map(commit -> new AchievementUnlocked(this, commit));
    }
}
