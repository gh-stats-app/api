package app.ghstats.api.achievements.impl;

import app.ghstats.api.achievements.api.Achievement;
import app.ghstats.api.achievements.api.AchievementUnlocked;
import app.ghstats.api.achievements.api.GitCommit;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

@Component
class Fix implements Achievement {

    static final Pattern FIX_PATTERN = Pattern.compile("\\b(fix|fixes|fixed|fixing)?\\b", Pattern.CASE_INSENSITIVE);

    @Override
    public String getId() {
        return "beggar";
    }

    @Override
    public String getName() {
        return "Save the Day";
    }

    @Override
    public String getDescription() {
        return "Use word “fix” in a commit message";
    }

    @Override
    public Optional<AchievementUnlocked> unlock(List<GitCommit> commits) {
        return commits.stream()
                .filter(it -> FIX_PATTERN.matcher(it.message()).find())
                .findFirst()
                .map(commit -> new AchievementUnlocked(this, commit));
    }
}
