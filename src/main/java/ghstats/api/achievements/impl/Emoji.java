package ghstats.api.achievements.impl;

import com.vdurmont.emoji.EmojiManager;
import ghstats.api.achievements.api.Achievement;
import ghstats.api.achievements.api.AchievementUnlocked;
import ghstats.api.achievements.api.GitCommit;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Optional;

@Component
class Emoji implements Achievement {

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
