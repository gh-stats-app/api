package ghstats.api.achievements.impl;

import ghstats.api.achievements.api.AchievementUnlocked;
import ghstats.api.achievements.api.PullRequestContext;
import ghstats.api.achievements.api.UnlockableAchievement;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
class WorkerBee implements UnlockableAchievement {

    private static final int REQUIRED_COMMITS = 100;

    @Override
    public String getId() {
        return "worker-bee";
    }

    @Override
    public String getName() {
        return "Worker Bee";
    }

    @Override
    public String getDescription() {
        return "Make 100 or more non-merge commits";
    }

    @Override
    public Optional<AchievementUnlocked> unlock(PullRequestContext context) {
        int nonMergeCommits = 0;
        for (var commit : context.commits()) {
            if (commit.parents().size() < 2 && ++nonMergeCommits == REQUIRED_COMMITS) {
                return Optional.of(new AchievementUnlocked(
                        this,
                        context.recipient().userName(),
                        commit
                ));
            }
        }
        return Optional.empty();
    }
}
