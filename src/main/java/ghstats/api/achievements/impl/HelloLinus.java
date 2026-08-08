package ghstats.api.achievements.impl;

import ghstats.api.achievements.api.AchievementUnlocked;
import ghstats.api.achievements.api.PullRequestContext;
import ghstats.api.achievements.api.UnlockableAchievement;
import ghstats.api.integrations.github.api.GitCommit;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;

@Component
class HelloLinus implements UnlockableAchievement {

    private static final Set<String> SWEAR_WORDS = Set.of(
            "fuck", "shit", "damn", "ass", "bitch", "crap", "dick", "bastard",
            "hell", "piss", "bollocks", "bugger", "bloody", "arse", "wtf"
    );
    static final Pattern SWEAR_PATTERN = Pattern.compile(
            "\\b(" + String.join("|", SWEAR_WORDS) + ")\\w*\\b",
            Pattern.CASE_INSENSITIVE
    );

    @Override
    public String getId() {
        return "hello-linus";
    }

    @Override
    public String getName() {
        return "Hello, Linus";
    }

    @Override
    public String getDescription() {
        return "5+ swear words in a commit message";
    }

    @Override
    public Optional<AchievementUnlocked> unlock(PullRequestContext context) {
        List<GitCommit> commits = context.commits();
        return commits.stream()
                .filter(it -> SWEAR_PATTERN.matcher(it.message()).results().count() >= 5)
                .findFirst()
                .map(commit -> new AchievementUnlocked(this, context.recipient().userName(), commit));
    }
}
