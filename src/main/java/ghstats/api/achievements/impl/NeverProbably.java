package ghstats.api.achievements.impl;

import ghstats.api.achievements.api.Achievement;
import ghstats.api.achievements.api.AchievementUnlocked;
import ghstats.api.achievements.api.GitCommit;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

@Component
class NeverProbably implements Achievement {

    static final Pattern FIX_PATTERN = Pattern.compile("(\\blater?\\b)", Pattern.CASE_INSENSITIVE);

    @Override
    public String getId() {
        return "never-probably";
    }

    @Override
    public String getName() {
        return "Never, Probably";
    }

    @Override
    public String getDescription() {
        return "Use word “later” in a commit message";
    }

    @Override
    public Optional<AchievementUnlocked> unlock(List<GitCommit> commits) {
        return commits.stream()
                .filter(it -> FIX_PATTERN.matcher(it.message()).find())
                .findFirst()
                .map(commit -> new AchievementUnlocked(this, commit));
    }
}
