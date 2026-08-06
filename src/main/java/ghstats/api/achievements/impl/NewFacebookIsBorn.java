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
class NewFacebookIsBorn implements UnlockableAchievement {

    @Override
    public String getId() {
        return "php";
    }

    @Override
    public String getName() {
        return "New Facebook is Born";
    }

    @Override
    public String getDescription() {
        return "Add PHP file to the repo";
    }

    @Override
    public Optional<AchievementUnlocked> unlock(List<GitCommit> commits) {
        return commits.stream()
                .filter(it -> Stream.of(it.added())
                        .flatMap(Collection::stream)
                        .anyMatch(s -> s.endsWith(".php")))
                .findAny()
                .map(commit -> new AchievementUnlocked(this, commit));
    }
}
