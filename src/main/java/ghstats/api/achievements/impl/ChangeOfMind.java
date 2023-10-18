package ghstats.api.achievements.impl;

import ghstats.api.achievements.api.AchievementUnlocked;
import ghstats.api.achievements.api.UnlockableAchievement;
import ghstats.api.integrations.github.api.GitCommit;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

@Component
class ChangeOfMind implements UnlockableAchievement {

    static final Pattern LICENSE_PATTERN = Pattern.compile("\\blicense?\\b", Pattern.CASE_INSENSITIVE);

    @Override
    public String getId() {
        return "change-of-mind";
    }

    @Override
    public String getName() {
        return "Change of mind";
    }

    @Override
    public String getDescription() {
        return "Change license type or edit license file";
    }

    @Override
    public Optional<AchievementUnlocked> unlock(List<GitCommit> commits) {
        return commits.stream()
                .filter(commit -> commit.modified().stream()
                        .anyMatch(filename -> LICENSE_PATTERN.matcher(filename).find())
                ).findAny()
                .map(commit -> new AchievementUnlocked(this, commit));
    }
}
