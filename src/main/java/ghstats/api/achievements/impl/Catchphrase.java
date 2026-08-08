package ghstats.api.achievements.impl;

import ghstats.api.achievements.api.AchievementUnlocked;
import ghstats.api.achievements.api.PullRequestContext;
import ghstats.api.achievements.api.UnlockableAchievement;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
class Catchphrase implements UnlockableAchievement {

    private static final int REQUIRED_REPETITIONS = 10;

    @Override
    public String getId() {
        return "catchphrase";
    }

    @Override
    public String getName() {
        return "Catchphrase";
    }

    @Override
    public String getDescription() {
        return "Use the same commit message 10 times";
    }

    @Override
    public Optional<AchievementUnlocked> unlock(PullRequestContext context) {
        Map<String, Integer> repetitions = new HashMap<>();
        for (var commit : context.commits()) {
            int count = repetitions.merge(commit.message(), 1, Integer::sum);
            if (count >= REQUIRED_REPETITIONS) {
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
