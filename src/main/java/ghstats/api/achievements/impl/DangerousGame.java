package ghstats.api.achievements.impl;

import ghstats.api.achievements.api.Achievement;
import ghstats.api.achievements.api.AchievementUnlocked;
import ghstats.api.achievements.api.GitCommit;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.Month;
import java.util.List;
import java.util.Optional;

@Component
class DangerousGame implements Achievement {

    @Override
    public String getId() {
        return "dangerous-game";
    }

    @Override
    public String getName() {
        return "Dangerous Game";
    }

    @Override
    public String getDescription() {
        return "Commit after 18:00 on friday";
    }

    @Override
    public Optional<AchievementUnlocked> unlock(List<GitCommit> commits) {
        return commits.stream()
                .filter(it -> it.timestamp().getDayOfWeek() == DayOfWeek.FRIDAY && it.timestamp().getHour() > 18)
                .findAny()
                .map(commit -> new AchievementUnlocked(this, commit));
    }
}
