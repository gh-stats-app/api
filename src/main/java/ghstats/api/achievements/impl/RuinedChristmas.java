package ghstats.api.achievements.impl;

import ghstats.api.achievements.api.AchievementUnlocked;
import ghstats.api.integrations.github.api.GitCommit;
import ghstats.api.achievements.api.UnlockableAchievement;
import org.springframework.stereotype.Component;

import java.time.Month;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

@Component
class RuinedChristmas implements UnlockableAchievement {

    @Override
    public String getId() {
        return "christmas";
    }

    @Override
    public String getName() {
        return "Ruined Christmas";
    }

    @Override
    public String getDescription() {
        return "Commit on 25th December";
    }

    @Override
    public Optional<AchievementUnlocked> unlock(List<GitCommit> commits) {
        return commits.stream()
                .filter(it ->isChristmasDay(it.timestamp()))
                .findFirst()
                .map(commit -> new AchievementUnlocked(this, commit));
    }

    private Boolean isChristmasDay(ZonedDateTime timestamp) {
        return Month.DECEMBER.equals(timestamp.getMonth()) && 25 == timestamp.getDayOfMonth();
    }
}
