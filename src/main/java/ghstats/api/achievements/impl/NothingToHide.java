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
class NothingToHide implements UnlockableAchievement {

    static final Pattern SECRET_PATTERN = Pattern.compile(
            "(?i)(password|secret|api[_-]?key|access[_-]?token|private[_-]?key|credentials)" +
            "|\\.(env|pem|key|p12|pfx|keystore)$"
    );

    @Override
    public String getId() {
        return "nothing-to-hide";
    }

    @Override
    public String getName() {
        return "Nothing to Hide";
    }

    @Override
    public String getDescription() {
        return "Commit a secret";
    }

    @Override
    public Optional<AchievementUnlocked> unlock(PullRequestContext context) {
        boolean containsSecret = Stream.of(context.addedFiles(), context.modifiedFiles())
                .flatMap(Collection::stream)
                .anyMatch(filename -> SECRET_PATTERN.matcher(filename).find());
        return containsSecret
                ? Optional.of(new AchievementUnlocked(this, context.recipient().userName(), context.triggerCommit()))
                : Optional.empty();
    }
}
