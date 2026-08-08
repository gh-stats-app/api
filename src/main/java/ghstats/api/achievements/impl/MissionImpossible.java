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
class MissionImpossible implements UnlockableAchievement {

    static final Pattern PATTERN = Pattern.compile("\\bimpossible\\b", Pattern.CASE_INSENSITIVE);

    @Override
    public String getId() {
        return "impossible";
    }

    @Override
    public String getName() {
        return "Mission Impossible";
    }

    @Override
    public String getDescription() {
        return "Use word \u201cimpossible\u201d in a commit message";
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
