package ghstats.api.achievements.impl;

import ghstats.api.achievements.api.AchievementUnlocked;
import ghstats.api.integrations.github.api.GitCommit;
import ghstats.api.achievements.api.UnlockableAchievement;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

@Component
class Forgot implements UnlockableAchievement {

    static final Pattern FORGOT_PATTERN = Pattern.compile("(\\bforgot\\b)", Pattern.CASE_INSENSITIVE);

    @Override
    public String getId() {
        return "forgot";
    }

    @Override
    public String getName() {
        return "Second Thoughts";
    }

    @Override
    public String getDescription() {
        return "Use word “forgot” in a commit message";
    }

    @Override
    public Optional<AchievementUnlocked> unlock(List<GitCommit> commits) {
        return commits.stream()
                .filter(it -> FORGOT_PATTERN.matcher(it.message()).find())
                .findFirst()
                .map(commit -> new AchievementUnlocked(this, commit));
    }
}
