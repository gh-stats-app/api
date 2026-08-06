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
class Gitignore implements UnlockableAchievement {

    @Override
    public String getId() {
        return "for-stallman";
    }

    @Override
    public String getName() {
        return "Gitignore";
    }

    @Override
    public String getDescription() {
        return "Add .gitignore";
    }

    @Override
    public Optional<AchievementUnlocked> unlock(List<GitCommit> commits) {
        return commits.stream()
                .filter(it -> Stream.of(it.added())
                        .flatMap(Collection::stream)
                        .anyMatch(s -> s.endsWith(".gitignore")))
                .findAny()
                .map(commit -> new AchievementUnlocked(this, commit));
    }
}
