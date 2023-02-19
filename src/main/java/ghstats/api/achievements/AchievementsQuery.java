package ghstats.api.achievements;

import ghstats.api.achievements.api.AchievementDefinition;

import java.util.List;

public class AchievementsQuery {

    private final List<AchievementDefinition> achievements;

    public AchievementsQuery(List<AchievementDefinition> achievements) {
        this.achievements = achievements;
    }

    public List<AchievementDefinition> getAll() {
        return achievements;
    }
}
