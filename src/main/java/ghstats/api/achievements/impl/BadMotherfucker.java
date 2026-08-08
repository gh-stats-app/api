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
class BadMotherfucker implements UnlockableAchievement {

    private static final Set<String> SWEAR_WORDS = Set.of(
            "fuck", "shit", "damn", "ass", "bitch", "crap", "dick", "bastard",
            "hell", "piss", "bollocks", "bugger", "bloody", "arse", "wtf"
    );
    private static final Pattern SWEAR_PATTERN = Pattern.compile(
            "\\b(" + String.join("|", SWEAR_WORDS) + ")\\w*\\b",
            Pattern.CASE_INSENSITIVE
    );

    @Override
    public String getId() {
        return "bad-motherfucker";
    }

    @Override
    public String getName() {
        return "Bad Motherf*cker";
    }

    @Override
    public String getDescription() {
        return "Swear in a commit message";
    }

    @Override
    public Optional<AchievementUnlocked> unlock(PullRequestContext context) {
        List<GitCommit> commits = context.commits();
        return commits.stream()
                .filter(it -> SWEAR_PATTERN.matcher(it.message()).find())
                .findFirst()
                .map(commit -> new AchievementUnlocked(this, context.recipient().userName(), commit));
    }
}
