package ghstats.api.achievements.impl;

import ghstats.api.achievements.api.AchievementUnlocked;
import ghstats.api.achievements.api.PullRequestContext;
import ghstats.api.integrations.github.api.GitCommit;
import ghstats.api.achievements.api.UnlockableAchievement;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

@Component
class Beggar implements UnlockableAchievement {

    static final Pattern BEGGAR_PATTERN = Pattern.compile("\\bachievements?\\b", Pattern.CASE_INSENSITIVE);

    @Override
    public String getId() {
        return "beggar";
    }

    @Override
    public String getName() {
        return "Beggar";
    }

    @Override
    public String getDescription() {
        return "Ask for an achievement in a commit message";
    }

    @Override
    public Optional<AchievementUnlocked> unlock(PullRequestContext context) {
        List<GitCommit> commits = context.commits();
        return commits.stream()
                .filter(it -> BEGGAR_PATTERN.matcher(it.message()).find())
                .findFirst()
                .map(commit -> new AchievementUnlocked(this, context.recipient().userName(), commit));
    }
}
