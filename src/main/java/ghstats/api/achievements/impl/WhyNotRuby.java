package ghstats.api.achievements.impl;

import ghstats.api.achievements.api.AchievementUnlocked;
import ghstats.api.integrations.github.api.GitCommit;
import ghstats.api.achievements.api.UnlockableAchievement;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Component
class WhyNotRuby implements UnlockableAchievement {

    @Override
    public String getId() {
        return "python";
    }

    @Override
    public String getName() {
        return "Why not Ruby?";
    }

    @Override
    public String getDescription() {
        return "Add Python file to the repo";
    }

    @Override
    public Optional<AchievementUnlocked> unlock(List<GitCommit> commits) {
        return commits.stream()
                .filter(it -> Stream.of(it.modified(), it.removed(), it.added())
                        .flatMap(Collection::stream)
                        .anyMatch(s -> s.endsWith(".py")))
                .findAny()
                .map(commit -> new AchievementUnlocked(this, commit));
    }
}
