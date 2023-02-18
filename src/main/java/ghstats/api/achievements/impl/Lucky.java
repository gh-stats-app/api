package ghstats.api.achievements.impl;

import ghstats.api.achievements.api.Achievement;
import ghstats.api.achievements.api.AchievementUnlocked;
import ghstats.api.achievements.api.GitCommit;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
class Lucky implements Achievement {

    @Override
    public String getId() {
        return "lucky";
    }

    @Override
    public String getName() {
        return "Lucky";
    }

    @Override
    public String getDescription() {
        return "Consecutive 777 in commit hash";
    }

    @Override
    public Optional<AchievementUnlocked> unlock(List<GitCommit> commits) {
        return commits.stream()
                .filter(it -> it.id().value().contains("777"))
                .findFirst()
                .map(commit -> new AchievementUnlocked(this, commit));
    }
}
