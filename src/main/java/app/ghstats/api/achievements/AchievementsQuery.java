package app.ghstats.api.achievements;

import app.ghstats.api.achievements.api.Achievement;

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
