package ghstats.api.achievements.impl;

import ghstats.api.achievements.api.AchievementUnlocked;
import ghstats.api.achievements.api.UnlockableAchievement;
import ghstats.api.integrations.github.api.GitCommit;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
class MarkOfTheBeast implements UnlockableAchievement {

    @Override
    public String getId() {
        return "mark-of-the-beast";
    }

    @Override
    public String getName() {
        return "Mark of the Beast";
    }

    @Override
    public String getDescription() {
        return "Consecutive 666 in commit hash";
    }

    @Override
    public Optional<AchievementUnlocked> unlock(List<GitCommit> commits) {
        return commits.stream()
                .filter(it -> it.id().value().contains("666"))
                .findFirst()
                .map(commit -> new AchievementUnlocked(this, commit));
    }
}
