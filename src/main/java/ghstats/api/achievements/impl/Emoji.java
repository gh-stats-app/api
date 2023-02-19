package ghstats.api.achievements.impl;

import com.vdurmont.emoji.EmojiManager;
import ghstats.api.achievements.api.AchievementUnlocked;
import ghstats.api.integrations.github.api.GitCommit;
import ghstats.api.achievements.api.UnlockableAchievement;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
class Emoji implements UnlockableAchievement {

    @Override
    public String getId() {
        return "emoji";
    }

    @Override
    public String getName() {
        return "C00l kid";
    }

    @Override
    public String getDescription() {
        return "Use emoji in a commit message";
    }

    @Override
    public Optional<AchievementUnlocked> unlock(List<GitCommit> commits) {
        return commits.stream()
                .filter(it -> EmojiManager.containsEmoji(it.message()))
                .findAny()
                .map(commit -> new AchievementUnlocked(this, commit));
    }
}
