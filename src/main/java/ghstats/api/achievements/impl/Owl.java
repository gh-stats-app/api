package ghstats.api.achievements.impl;

import ghstats.api.achievements.api.AchievementUnlocked;
import ghstats.api.achievements.api.UnlockableAchievement;
import ghstats.api.integrations.github.api.GitCommit;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
class Owl implements UnlockableAchievement {

    @Override
    public String getName() {
        return "owl";
    }

    @Override
    public String getDescription() {
        return "Commit between 4am and 6am local time";
    }

    @Override
    public Optional<AchievementUnlocked> unlock(List<GitCommit> commits) {
        return commits.stream()
                .filter(commit -> commit.timestamp().getHour() >= 4
                        && commit.timestamp().getHour() < 6)
                .findAny()
                .map(commit -> new AchievementUnlocked(this, commit));
    }
}
