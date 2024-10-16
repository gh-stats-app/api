package ghstats.api.achievements.impl;

import ghstats.api.achievements.api.AchievementUnlocked;
import ghstats.api.integrations.github.api.GitCommit;
import ghstats.api.achievements.api.UnlockableAchievement;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

@Component
class ProfessionalPride implements UnlockableAchievement {

    @Override
    public String getId() {
        return "programmers-day";
    }

    @Override
    public String getName() {
        return "Professional Pride";
    }

    @Override
    public String getDescription() {
        return "Commit on Programmers' Day";
    }

    @Override
    public Optional<AchievementUnlocked> unlock(List<GitCommit> commits) {
        return commits.stream()
                .filter(it -> it.timestamp().getDayOfYear() == 256)
                .findFirst()
                .map(commit -> new AchievementUnlocked(this, commit));
    }
}
