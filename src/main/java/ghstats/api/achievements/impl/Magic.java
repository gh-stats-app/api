package ghstats.api.achievements.impl;

import ghstats.api.achievements.api.AchievementUnlocked;
import ghstats.api.integrations.github.api.GitCommit;
import ghstats.api.achievements.api.UnlockableAchievement;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

@Component
class Magic implements UnlockableAchievement {

    static final Pattern FIX_PATTERN = Pattern.compile("(\\bmagic?\\b)", Pattern.CASE_INSENSITIVE);

    @Override
    public String getId() {
        return "magic";
    }

    @Override
    public String getName() {
        return "The Colour of Magic";
    }

    @Override
    public String getDescription() {
        return "Use word “magic” in a commit message";
    }

    @Override
    public Optional<AchievementUnlocked> unlock(List<GitCommit> commits) {
        return commits.stream()
                .filter(it -> FIX_PATTERN.matcher(it.message()).find())
                .findFirst()
                .map(commit -> new AchievementUnlocked(this, commit));
    }
}
