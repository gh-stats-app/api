package ghstats.api.achievements.impl;

import ghstats.api.achievements.api.AchievementUnlocked;
import ghstats.api.integrations.github.api.GitCommit;
import ghstats.api.achievements.api.UnlockableAchievement;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

@Component
class Hack implements UnlockableAchievement {

    static final Pattern HACK_PATTERN = Pattern.compile("(\\bhack\\b)", Pattern.CASE_INSENSITIVE);

    @Override
    public String getId() {
        return "hack";
    }

    @Override
    public String getName() {
        return "Real Hacker";
    }

    @Override
    public String getDescription() {
        return "Use word “hack” in a commit message";
    }

    @Override
    public Optional<AchievementUnlocked> unlock(List<GitCommit> commits) {
        return commits.stream()
                .filter(it -> HACK_PATTERN.matcher(it.message()).find())
                .findFirst()
                .map(commit -> new AchievementUnlocked(this, commit));
    }
}
