package ghstats.api.achievements.impl;

import ghstats.api.achievements.api.AchievementUnlocked;
import ghstats.api.achievements.api.PullRequestContext;
import ghstats.api.achievements.api.UnlockableAchievement;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
class EasyFix implements UnlockableAchievement {

    @Override
    public String getId() {
        return "easy-fix";
    }

    @Override
    public String getName() {
        return "Easy Fix";
    }

    @Override
    public String getDescription() {
        return "Swap two lines";
    }

    @Override
    public Optional<AchievementUnlocked> unlock(PullRequestContext context) {
        boolean swapsTwoLines = context.files().stream()
                .map(GitHubFilePatch::parse)
                .flatMap(Optional::stream)
                .map(GitHubFilePatch.ParsedPatch::alignedChanges)
                .flatMap(Optional::stream)
                .anyMatch(changes -> changes.size() == 2
                        && changes.get(0).before().equals(changes.get(1).after())
                        && changes.get(1).before().equals(changes.get(0).after()));
        return swapsTwoLines
                ? Optional.of(new AchievementUnlocked(this, context.recipient().userName(), context.triggerCommit()))
                : Optional.empty();
    }
}
