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
class HappilyNeverAfter implements UnlockableAchievement {

    @Override
    public String getId() {
        return "javascript";
    }

    @Override
    public String getName() {
        return "Happily Never After";
    }

    @Override
    public String getDescription() {
        return "Add JS file to the repo";
    }

    @Override
    public Optional<AchievementUnlocked> unlock(List<GitCommit> commits) {
        return commits.stream()
                .filter(it -> Stream.of(it.added())
                        .flatMap(Collection::stream)
                        .anyMatch(s -> s.endsWith(".js") || s.endsWith(".mjs")))
                .findAny()
                .map(commit -> new AchievementUnlocked(this, commit));
    }
}
