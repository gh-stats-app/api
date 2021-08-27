package app.ghstats.api.achievements.impl;

import app.ghstats.api.achievements.api.Achievement;
import app.ghstats.api.achievements.api.AchievementUnlocked;
import app.ghstats.api.achievements.api.GitCommit;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
class WreckingBall implements Achievement {

    @Override
    public String getName() {
        return "Wrecking Ball";
    }

    @Override
    public String getDescription() {
        return "Change more than 100 files in one commit";
    }

    @Override
    public Optional<AchievementUnlocked> check(List<GitCommit> commits) {
        return commits.stream()
                .filter(it -> it.added().size() + it.modified().size() + it.removed().size() >= 100)
                .findAny()
                .map(commit -> new AchievementUnlocked(commit.id(), commit.userName()));
    }
}
