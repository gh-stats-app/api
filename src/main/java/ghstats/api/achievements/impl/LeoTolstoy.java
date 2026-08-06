package ghstats.api.achievements.impl;

import ghstats.api.achievements.api.AchievementUnlocked;
import ghstats.api.achievements.api.UnlockableAchievement;
import ghstats.api.integrations.github.api.GitCommit;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
class LeoTolstoy implements UnlockableAchievement {

    @Override
    public String getId() {
        return "leo-tolstoy";
    }

    @Override
    public String getName() {
        return "Leo Tolstoy";
    }

    @Override
    public String getDescription() {
        return "More than 10 lines in a commit message";
    }

    @Override
    public Optional<AchievementUnlocked> unlock(List<GitCommit> commits) {
        return commits.stream()
                .filter(it -> it.message().split("\n").length > 10)
                .findFirst()
                .map(commit -> new AchievementUnlocked(this, commit));
    }
}
