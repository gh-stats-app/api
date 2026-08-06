package ghstats.api.achievements.impl;

import ghstats.api.achievements.api.AchievementUnlocked;
import ghstats.api.achievements.api.UnlockableAchievement;
import ghstats.api.integrations.github.api.GitCommit;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
class ManOfFewWords implements UnlockableAchievement {

    @Override
    public String getId() {
        return "man-of-few-words";
    }

    @Override
    public String getName() {
        return "A Man of Few Words";
    }

    @Override
    public String getDescription() {
        return "Commit message with 3 letters or less";
    }

    @Override
    public Optional<AchievementUnlocked> unlock(List<GitCommit> commits) {
        return commits.stream()
                .filter(it -> it.message().trim().length() <= 3)
                .findFirst()
                .map(commit -> new AchievementUnlocked(this, commit));
    }
}
