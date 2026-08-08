package ghstats.api.achievements.impl;

import ghstats.api.achievements.api.AchievementUnlocked;
import ghstats.api.achievements.api.PullRequestContext;
import ghstats.api.integrations.github.api.GitCommit;
import ghstats.api.achievements.api.UnlockableAchievement;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
class WreckingBall implements UnlockableAchievement {

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
    public Optional<AchievementUnlocked> unlock(PullRequestContext context) {
        return context.files().size() >= 100
                ? Optional.of(new AchievementUnlocked(this, context.recipient().userName(), context.triggerCommit()))
                : Optional.empty();
    }
}
