package ghstats.api.achievements.impl;

import ghstats.api.achievements.api.AchievementUnlocked;
import ghstats.api.achievements.api.UnlockableAchievement;
import ghstats.api.integrations.github.api.GitCommit;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
class DealWithIt implements UnlockableAchievement {
    @Override
    public String getId() {
        return "deal-with-it";
    }

    @Override
    public String getName() {
        return "Deal with it";
    }

    @Override
    public String getDescription() {
        return "Update master/main branch with force mode";
    }

    @Override
    public Optional<AchievementUnlocked> unlock(List<GitCommit> commits) {
        return commits.stream()
                .filter(it -> it.pushMetadata().forced() &&
                                (it.pushMetadata().ref().contains("main") || it.pushMetadata().ref().contains("master"))
                )
                .findFirst()
                .map(commit -> new AchievementUnlocked(this, commit));
    }
}
