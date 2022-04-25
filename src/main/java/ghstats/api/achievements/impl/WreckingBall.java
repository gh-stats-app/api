package ghstats.api.achievements.impl;

import ghstats.api.achievements.api.Achievement;
import ghstats.api.achievements.api.AchievementUnlocked;
import ghstats.api.achievements.api.GitCommit;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
class WreckingBall implements Achievement {

    @Override
    public String getId() {
        return "wrecking-ball";
    }

    @Override
    public String getName() {
        return "Wrecking Ball";
    }

    @Override
    public String getDescription() {
        return "Change more than 100 files in one commit";
    }

    @Override
    public Optional<AchievementUnlocked> unlock(List<GitCommit> commits) {
        return commits.stream()
                .filter(it -> it.added().size() + it.modified().size() + it.removed().size() >= 100)
                .findAny()
                .map(commit -> new AchievementUnlocked(this, commit));
    }
}
