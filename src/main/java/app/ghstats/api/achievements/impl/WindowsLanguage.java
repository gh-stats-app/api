package app.ghstats.api.achievements.impl;

import app.ghstats.api.achievements.api.Achievement;
import app.ghstats.api.achievements.api.AchievementUnlocked;
import app.ghstats.api.achievements.api.GitCommit;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Component
class WindowsLanguage implements Achievement {

    @Override
    public String getName() {
        return "You Can't Program on Windows, Can You?";
    }

    @Override
    public String getDescription() {
        return "Add Windows Shell file to the repo";
    }

    @Override
    public Optional<AchievementUnlocked> check(List<GitCommit> commits) {
        return commits.stream()
                .filter(it -> Stream.of(it.modified(), it.removed(), it.added())
                        .flatMap(Collection::stream)
                        .anyMatch(s -> s.endsWith(".bat") || s.endsWith(".ps1")))
                .findAny()
                .map(commit -> new AchievementUnlocked(commit.id(), commit.userName()));
    }
}
