package ghstats.api.achievements.impl;

import ghstats.api.achievements.api.AchievementUnlocked;
import ghstats.api.integrations.github.api.GitCommit;
import ghstats.api.achievements.api.UnlockableAchievement;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
class ExactMatch implements UnlockableAchievement {

    @Override
    public String getId() {
        return "exact-match";
    }

    @Override
    public String getName() {
        return "Exact match";
    }

    @Override
    public String getDescription() {
        return "Commit exactly at 00:00";
    }

    @Override
    public Optional<AchievementUnlocked> unlock(List<GitCommit> commits) {
        return commits.stream()
                .filter(it -> it.timestamp().getHour() == 0 && it.timestamp().getMinute() == 0)
                .findAny()
                .map(commit -> new AchievementUnlocked(this, commit));
    }
}
