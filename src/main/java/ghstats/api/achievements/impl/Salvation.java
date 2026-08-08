package ghstats.api.achievements.impl;

import ghstats.api.achievements.api.AchievementUnlocked;
import ghstats.api.achievements.api.PullRequestContext;
import ghstats.api.achievements.api.UnlockableAchievement;
import ghstats.api.integrations.github.api.GitCommit;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

@Component
class Salvation implements UnlockableAchievement {

    static final Pattern PATTERN = Pattern.compile("\\bsorry\\b", Pattern.CASE_INSENSITIVE);

    @Override
    public String getId() {
        return "salvation";
    }

    @Override
    public String getName() {
        return "Salvation";
    }

    @Override
    public String getDescription() {
        return "Use word “sorry” in a commit message";
    }

    @Override
    public Optional<AchievementUnlocked> unlock(PullRequestContext context) {
        List<GitCommit> commits = context.commits();
        return commits.stream()
                .filter(it -> PATTERN.matcher(it.message()).find())
                .findFirst()
                .map(commit -> new AchievementUnlocked(this, context.recipient().userName(), commit));
    }
}
