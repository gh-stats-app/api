package ghstats.api.achievements.impl;

import ghstats.api.achievements.api.AchievementUnlocked;
import ghstats.api.integrations.github.api.GitCommit;
import ghstats.api.achievements.api.UnlockableAchievement;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
class WorldBalance implements UnlockableAchievement {

    @Override
    public String getId() {
        return "world-balance";
    }

    @Override
    public String getName() {
        return "World Balance";
    }

    @Override
    public String getDescription() {
        return "Number of files added == number of files deleted";
    }

    @Override
    public Optional<AchievementUnlocked> unlock(List<GitCommit> commits) {
        return commits.stream()
                .filter(it -> it.added().size() + it.modified().size() + it.removed().size() >= 100)
                .findAny()
                .map(commit -> new AchievementUnlocked(this, commit));
    }
}
