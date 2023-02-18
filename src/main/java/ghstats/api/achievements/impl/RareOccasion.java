package ghstats.api.achievements.impl;

import ghstats.api.achievements.api.Achievement;
import ghstats.api.achievements.api.AchievementUnlocked;
import ghstats.api.achievements.api.GitCommit;
import org.springframework.stereotype.Component;

import java.time.Month;
import java.util.List;
import java.util.Optional;

@Component
class RareOccasion implements Achievement {

    @Override
    public String getId() {
        return "leap-day";
    }

    @Override
    public String getName() {
        return "Rare Occasion";
    }

    @Override
    public String getDescription() {
        return "Commit on Feb 29";
    }

    @Override
    public Optional<AchievementUnlocked> unlock(List<GitCommit> commits) {
        return commits.stream()
                .filter(it -> it.timestamp().getMonth() == Month.FEBRUARY
                        && it.timestamp().getDayOfMonth() == 29
                )
                .findAny()
                .map(commit -> new AchievementUnlocked(this, commit));
    }
}
