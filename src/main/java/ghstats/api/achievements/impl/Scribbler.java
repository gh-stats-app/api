package ghstats.api.achievements.impl;

import ghstats.api.achievements.api.AchievementUnlocked;
import ghstats.api.achievements.api.UnlockableAchievement;
import ghstats.api.integrations.github.api.GitCommit;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Component
class Scribbler implements UnlockableAchievement {

    @Override
    public String getId() {
        return "scribbler";
    }

    @Override
    public String getName() {
        return "Scribbler";
    }

    @Override
    public String getDescription() {
        return "Create a README";
    }

    @Override
    public Optional<AchievementUnlocked> unlock(List<GitCommit> commits) {
        return commits.stream()
                .filter(it -> Stream.of(it.added())
                        .flatMap(Collection::stream)
                        .anyMatch(s -> s.toUpperCase().contains("README")))
                .findAny()
                .map(commit -> new AchievementUnlocked(this, commit));
    }
}
