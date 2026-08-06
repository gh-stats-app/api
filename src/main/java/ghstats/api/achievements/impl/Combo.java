package ghstats.api.achievements.impl;

import ghstats.api.achievements.api.AchievementUnlocked;
import ghstats.api.achievements.api.UnlockableAchievement;
import ghstats.api.integrations.github.api.GitCommit;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
class Combo implements UnlockableAchievement {

    @Override
    public String getId() {
        return "combo";
    }

    @Override
    public String getName() {
        return "Combo";
    }

    @Override
    public String getDescription() {
        return "10+ commits in a row";
    }

    @Override
    public Optional<AchievementUnlocked> unlock(List<GitCommit> commits) {
        if (commits.size() >= 10) {
            return Optional.of(new AchievementUnlocked(this, commits.get(commits.size() - 1)));
        }
        return Optional.empty();
    }
}
