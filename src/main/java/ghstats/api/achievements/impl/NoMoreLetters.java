package ghstats.api.achievements.impl;

import ghstats.api.achievements.api.AchievementUnlocked;
import ghstats.api.achievements.api.UnlockableAchievement;
import ghstats.api.integrations.github.api.GitCommit;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
class NoMoreLetters implements UnlockableAchievement {
    @Override
    public String getId() {
        return "no-more-letters";
    }

    @Override
    public String getName() {
        return "No More Letters";
    }

    @Override
    public String getDescription() {
        return "Write a commit message without any letters";
    }

    @Override
    public Optional<AchievementUnlocked> unlock(List<GitCommit> commits) {
        return commits.stream()
                .filter(it -> it.message().matches("[^a-zA-Z]*"))
                .findFirst()
                .map(commit -> new AchievementUnlocked(this, commit));
    }
}
