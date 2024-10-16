package ghstats.api.achievements.impl;

import ghstats.api.achievements.api.AchievementUnlocked;
import ghstats.api.achievements.api.UnlockableAchievement;
import ghstats.api.integrations.github.api.GitCommit;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
class NewYearNewBugs implements UnlockableAchievement {

    @Override
    public String getName() {
        return "new-year";
    }

    @Override
    public String getDescription() {
        return "Commit on Jan 1.";
    }

    @Override
    public Optional<AchievementUnlocked> unlock(List<GitCommit> commits) {
        return commits.stream()
                .filter(commit -> commit.timestamp().getDayOfYear() == 1)
                .findAny()
                .map(commit -> new AchievementUnlocked(this, commit));
    }
}
