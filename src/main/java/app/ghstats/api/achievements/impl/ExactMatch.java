package app.ghstats.api.achievements.impl;

import app.ghstats.api.achievements.api.Achievement;
import app.ghstats.api.achievements.api.AchievementUnlocked;
import app.ghstats.api.achievements.api.GitCommit;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
class ExactMatch implements Achievement {

    @Override
    public String getName() {
        return "Exact match";
    }

    @Override
    public String getDescription() {
        return "Commit exactly at 00:00";
    }

    @Override
    public Optional<AchievementUnlocked> check(List<GitCommit> commits) {
        return commits.stream()
                .filter(it -> it.timestamp().getHour() == 0 && it.timestamp().getMinute() == 0)
                .findAny()
                .map(commit -> new AchievementUnlocked(commit.id(), commit.userName()));
    }
}
