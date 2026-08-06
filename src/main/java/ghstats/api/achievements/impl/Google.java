package ghstats.api.achievements.impl;

import ghstats.api.achievements.api.AchievementUnlocked;
import ghstats.api.achievements.api.UnlockableAchievement;
import ghstats.api.integrations.github.api.GitCommit;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

@Component
class Google implements UnlockableAchievement {

    static final Pattern PATTERN = Pattern.compile("\\bgoogle\\b", Pattern.CASE_INSENSITIVE);

    @Override
    public String getId() {
        return "google";
    }

    @Override
    public String getName() {
        return "I Can Sort It out Myself";
    }

    @Override
    public String getDescription() {
        return "Use word \u201cgoogle\u201d in a commit message";
    }

    @Override
    public Optional<AchievementUnlocked> unlock(List<GitCommit> commits) {
        return commits.stream()
                .filter(it -> PATTERN.matcher(it.message()).find())
                .findFirst()
                .map(commit -> new AchievementUnlocked(this, commit));
    }
}
