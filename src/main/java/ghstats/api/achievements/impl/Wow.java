package ghstats.api.achievements.impl;

import ghstats.api.achievements.api.AchievementUnlocked;
import ghstats.api.integrations.github.api.GitCommit;
import ghstats.api.achievements.api.UnlockableAchievement;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

@Component
class Wow implements UnlockableAchievement {

    static final Pattern WOW_PATTERN = Pattern.compile("\\bwow\\b", Pattern.CASE_INSENSITIVE);

    @Override
    public String getId() {
        return "wow";
    }

    @Override
    public String getName() {
        return "Wow";
    }

    @Override
    public String getDescription() {
        return "use word “wow” in a commit message";
    }

    @Override
    public Optional<AchievementUnlocked> unlock(List<GitCommit> commits) {
        return commits.stream()
                .filter(it -> WOW_PATTERN.matcher(it.message()).find())
                .findFirst()
                .map(commit -> new AchievementUnlocked(this, commit));
    }
}
