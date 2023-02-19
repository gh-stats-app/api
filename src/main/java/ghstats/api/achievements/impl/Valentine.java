package ghstats.api.achievements.impl;

import ghstats.api.achievements.api.AchievementUnlocked;
import ghstats.api.integrations.github.api.GitCommit;
import ghstats.api.achievements.api.UnlockableAchievement;
import org.springframework.stereotype.Component;

import java.time.Month;
import java.util.List;
import java.util.Optional;

@Component
class Valentine implements UnlockableAchievement {

    @Override
    public String getId() {
        return "valentine";
    }

    @Override
    public String getName() {
        return "In Love with Work";
    }

    @Override
    public String getDescription() {
        return "Commit on Feb 14, in the evening";
    }

    @Override
    public Optional<AchievementUnlocked> unlock(List<GitCommit> commits) {
        return commits.stream()
                .filter(it -> it.timestamp().getMonth() == Month.FEBRUARY
                        && it.timestamp().getDayOfMonth() == 14
                        && it.timestamp().getHour() > 17
                )
                .findAny()
                .map(commit -> new AchievementUnlocked(this, commit));
    }
}
