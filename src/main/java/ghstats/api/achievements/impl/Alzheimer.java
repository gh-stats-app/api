package ghstats.api.achievements.impl;

import ghstats.api.achievements.api.AchievementUnlocked;
import ghstats.api.integrations.github.api.GitCommit;
import ghstats.api.achievements.api.UnlockableAchievement;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

@Component
class Alzheimer implements UnlockableAchievement {

    @Override
    public String getId() {
        return "alzheimers";
    }

    @Override
    public String getName() {
        return "Alzheimer's";
    }

    @Override
    public String getDescription() {
        return "Pushed at least 1 month old commit";
    }

    @Override
    public Optional<AchievementUnlocked> unlock(List<GitCommit> commits) {
        return commits.stream()
                .filter(it -> it.timestamp().plusMonths(1).isBefore(ZonedDateTime.now(it.timestamp().getZone())))
                .findFirst()
                .map(commit -> new AchievementUnlocked(this, commit));
    }
}
