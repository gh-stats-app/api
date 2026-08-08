package ghstats.api.achievements.impl;

import ghstats.api.achievements.api.AchievementUnlocked;
import ghstats.api.achievements.api.PullRequestContext;
import ghstats.api.achievements.api.UnlockableAchievement;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
class OCD implements UnlockableAchievement {

    @Override
    public String getId() {
        return "ocd";
    }

    @Override
    public String getName() {
        return "OCD";
    }

    @Override
    public String getDescription() {
        return "Remove only trailing spaces from a file";
    }

    @Override
    public Optional<AchievementUnlocked> unlock(PullRequestContext context) {
        boolean removesOnlyTrailingSpaces = context.files().stream()
                .map(GitHubFilePatch::parse)
                .flatMap(Optional::stream)
                .map(GitHubFilePatch.ParsedPatch::alignedChanges)
                .flatMap(Optional::stream)
                .anyMatch(changes -> !changes.isEmpty() && changes.stream().allMatch(this::removesTrailingSpaces));
        return removesOnlyTrailingSpaces
                ? Optional.of(new AchievementUnlocked(this, context.recipient().userName(), context.triggerCommit()))
                : Optional.empty();
    }

    private boolean removesTrailingSpaces(GitHubFilePatch.LineChange change) {
        int beforeSpaces = trailingSpaces(change.before());
        int afterSpaces = trailingSpaces(change.after());
        return beforeSpaces > afterSpaces
                && change.before().substring(0, change.before().length() - beforeSpaces)
                .equals(change.after().substring(0, change.after().length() - afterSpaces));
    }

    private int trailingSpaces(String value) {
        int count = 0;
        for (int index = value.length() - 1; index >= 0 && value.charAt(index) == ' '; index--) {
            count++;
        }
        return count;
    }
}
