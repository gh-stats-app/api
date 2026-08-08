package ghstats.api.achievements.impl;

import ghstats.api.achievements.api.AchievementUnlocked;
import ghstats.api.achievements.api.PullRequestContext;
import ghstats.api.achievements.api.UnlockableAchievement;
import ghstats.api.integrations.github.api.GitCommit;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Stream;

@Component
class GoodBoy implements UnlockableAchievement {

    static final Pattern PATTERN = Pattern.compile("(^|/)(?:test|doc|docs|documentation)/", Pattern.CASE_INSENSITIVE);

    @Override
    public String getId() {
        return "good-boy";
    }

    @Override
    public String getName() {
        return "Good Boy";
    }

    @Override
    public String getDescription() {
        return "Create 'test' or 'doc' directory";
    }

    @Override
    public Optional<AchievementUnlocked> unlock(PullRequestContext context) {
        return context.addedFiles().stream().anyMatch(filename -> PATTERN.matcher(filename).find())
                ? Optional.of(new AchievementUnlocked(this, context.recipient().userName(), context.triggerCommit()))
                : Optional.empty();
    }
}
