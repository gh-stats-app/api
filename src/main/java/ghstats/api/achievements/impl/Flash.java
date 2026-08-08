package ghstats.api.achievements.impl;

import ghstats.api.achievements.api.AchievementUnlocked;
import ghstats.api.achievements.api.PullRequestContext;
import ghstats.api.achievements.api.UnlockableAchievement;
import ghstats.api.integrations.github.api.GitCommit;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Component
class Flash implements UnlockableAchievement {

    @Override
    public String getId() {
        return "flash";
    }

    @Override
    public String getName() {
        return "Flash";
    }

    @Override
    public String getDescription() {
        return "Two different commits within 15 seconds";
    }

    @Override
    public Optional<AchievementUnlocked> unlock(PullRequestContext context) {
        List<GitCommit> commits = context.commits();
        if (commits.size() < 2) return Optional.empty();
        List<GitCommit> sorted = commits.stream()
                .sorted(Comparator.comparing(GitCommit::timestamp))
                .toList();
        for (int i = 1; i < sorted.size(); i++) {
            Duration gap = Duration.between(sorted.get(i - 1).timestamp(), sorted.get(i).timestamp()).abs();
            if (gap.getSeconds() <= 15) {
                return Optional.of(new AchievementUnlocked(this, context.recipient().userName(), sorted.get(i)));
            }
        }
        return Optional.empty();
    }
}
