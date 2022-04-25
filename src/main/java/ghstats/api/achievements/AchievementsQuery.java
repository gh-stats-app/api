package ghstats.api.achievements;

import ghstats.api.achievements.api.Achievement;

import java.util.List;

public class AchievementsQuery {

    private final List<Achievement> achievements;

    public AchievementsQuery(List<Achievement> achievements) {
        this.achievements = achievements;
    }

    public List<Achievement> getAll() {
        return achievements;
    }
}
