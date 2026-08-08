package ghstats.api.achievements.impl;

import ghstats.api.achievements.api.AchievementUnlocked;
import ghstats.api.achievements.api.PullRequestContext;
import ghstats.api.achievements.api.UnlockableAchievement;
import ghstats.api.integrations.github.api.GitCommit;
import org.springframework.stereotype.Component;

import java.time.Month;
import java.util.List;
import java.util.Optional;

@Component
class Halloween implements UnlockableAchievement {

    @Override
    public String getId() {
        return "halloween";
    }

    @Override
    public String getName() {
        return "This Code Looks Scary";
    }

    @Override
    public String getDescription() {
        return "Commit on Oct 31";
    }

    @Override
    public Optional<AchievementUnlocked> unlock(PullRequestContext context) {
        List<GitCommit> commits = context.commits();
        return commits.stream()
                .filter(it -> it.timestamp().getMonth() == Month.OCTOBER
                        && it.timestamp().getDayOfMonth() == 31)
                .findAny()
                .map(commit -> new AchievementUnlocked(this, context.recipient().userName(), commit));
    }
}
