package ghstats.api.achievements.impl;

import ghstats.api.achievements.api.AchievementUnlocked;
import ghstats.api.achievements.api.UnlockableAchievement;
import ghstats.api.integrations.github.api.GitCommit;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

@Component
class WeAreSafeNow implements UnlockableAchievement {

    static final Pattern PATTERN = Pattern.compile("(\\bsecure?\\b)", Pattern.CASE_INSENSITIVE);

    @Override
    public String getId() {
        return "we-are-safe-now";
    }

    @Override
    public String getName() {
        return "We’re Safe Now";
    }

    @Override
    public String getDescription() {
        return "Use the word \"secure\" in a commit message";
    }

    @Override
    public Optional<AchievementUnlocked> unlock(List<GitCommit> commits) {
        return commits.stream()
                .filter(it -> PATTERN.matcher(it.message()).find())
                .findFirst()
                .map(commit -> new AchievementUnlocked(this, commit));
    }
}
