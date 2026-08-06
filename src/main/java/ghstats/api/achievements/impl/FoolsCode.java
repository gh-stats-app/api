package ghstats.api.achievements.impl;

import ghstats.api.achievements.api.AchievementUnlocked;
import ghstats.api.achievements.api.UnlockableAchievement;
import ghstats.api.integrations.github.api.GitCommit;
import org.springframework.stereotype.Component;

import java.time.Month;
import java.util.List;
import java.util.Optional;

@Component
class FoolsCode implements UnlockableAchievement {

    @Override
    public String getId() {
        return "fools-day";
    }

    @Override
    public String getName() {
        return "Fools' Code";
    }

    @Override
    public String getDescription() {
        return "Commit on Apr 1";
    }

    @Override
    public Optional<AchievementUnlocked> unlock(List<GitCommit> commits) {
        return commits.stream()
                .filter(it -> it.timestamp().getMonth() == Month.APRIL
                        && it.timestamp().getDayOfMonth() == 1)
                .findAny()
                .map(commit -> new AchievementUnlocked(this, commit));
    }
}
