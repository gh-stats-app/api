package app.ghstats.api.achievements.impl;

import app.ghstats.api.achievements.api.Achievement;
import app.ghstats.api.achievements.api.AchievementUnlocked;
import app.ghstats.api.achievements.api.GitCommit;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
class WorldBalance implements Achievement {

    @Override
    public String getId() {
        return "world-balance";
    }

    @Override
    public String getName() {
        return "World Balance";
    }

    @Override
    public String getDescription() {
        return "Number of lines added == number of lines deleted";
    }

    @Override
    public Optional<AchievementUnlocked> unlock(List<GitCommit> commits) {
        return commits.stream()
                .filter(it -> it.added().size() + it.modified().size() + it.removed().size() >= 100)
                .findAny()
                .map(commit -> new AchievementUnlocked(this, commit));
    }
}
