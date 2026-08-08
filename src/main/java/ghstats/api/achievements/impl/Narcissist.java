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
class Narcissist implements UnlockableAchievement {

    @Override
    public String getId() {
        return "narcissist";
    }

    @Override
    public String getName() {
        return "Narcissist";
    }

    @Override
    public String getDescription() {
        return "Use your own name in a commit message";
    }

    @Override
    public Optional<AchievementUnlocked> unlock(PullRequestContext context) {
        List<GitCommit> commits = context.commits();
        return commits.stream()
                .filter(it -> {
                    String authorName = it.author().userName().value().toLowerCase();
                    String message = it.message().toLowerCase();
                    return authorName.length() >= 2 && message.contains(authorName);
                })
                .findFirst()
                .map(commit -> new AchievementUnlocked(this, context.recipient().userName(), commit));
    }
}
