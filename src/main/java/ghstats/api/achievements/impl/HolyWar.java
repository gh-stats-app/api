package ghstats.api.achievements.impl;

import ghstats.api.achievements.api.AchievementUnlocked;
import ghstats.api.achievements.api.PullRequestContext;
import ghstats.api.achievements.api.UnlockableAchievement;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
class HolyWar implements UnlockableAchievement {

    @Override
    public String getId() {
        return "holy-war";
    }

    @Override
    public String getName() {
        return "Holy War";
    }

    @Override
    public String getDescription() {
        return "Change tabs to spaces or spaces to tabs";
    }

    @Override
    public Optional<AchievementUnlocked> unlock(PullRequestContext context) {
        boolean convertsIndentation = context.files().stream()
                .map(GitHubFilePatch::parse)
                .flatMap(Optional::stream)
                .map(GitHubFilePatch.ParsedPatch::alignedChanges)
                .flatMap(Optional::stream)
                .anyMatch(changes -> !changes.isEmpty() && changes.stream().allMatch(this::onlyTabsAndSpacesChanged));
        return convertsIndentation
                ? Optional.of(new AchievementUnlocked(this, context.recipient().userName(), context.triggerCommit()))
                : Optional.empty();
    }

    private boolean onlyTabsAndSpacesChanged(GitHubFilePatch.LineChange change) {
        return withoutHorizontalWhitespace(change.before()).equals(withoutHorizontalWhitespace(change.after()))
                && count(change.before(), '\t') != count(change.after(), '\t')
                && count(change.before(), ' ') != count(change.after(), ' ');
    }

    private String withoutHorizontalWhitespace(String value) {
        return value.replace(" ", "").replace("\t", "");
    }

    private long count(String value, char character) {
        return value.chars().filter(candidate -> candidate == character).count();
    }
}
