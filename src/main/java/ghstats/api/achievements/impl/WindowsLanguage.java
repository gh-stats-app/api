package ghstats.api.achievements.impl;

import ghstats.api.achievements.api.AchievementUnlocked;
import ghstats.api.achievements.api.PullRequestContext;
import ghstats.api.integrations.github.api.GitCommit;
import ghstats.api.achievements.api.UnlockableAchievement;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Component
class WindowsLanguage implements UnlockableAchievement {

    @Override
    public String getId() {
        return "windows-language";
    }

    @Override
    public String getName() {
        return "You Can't Program on Windows, Can You?";
    }

    @Override
    public String getDescription() {
        return "Add Windows Shell file to the repo";
    }

    @Override
    public Optional<AchievementUnlocked> unlock(PullRequestContext context) {
        boolean containsWindowsScript = context.files().stream()
                .map(file -> file.filename())
                .anyMatch(filename -> filename.endsWith(".bat") || filename.endsWith(".ps1"));
        return containsWindowsScript
                ? Optional.of(new AchievementUnlocked(this, context.recipient().userName(), context.triggerCommit()))
                : Optional.empty();
    }
}
