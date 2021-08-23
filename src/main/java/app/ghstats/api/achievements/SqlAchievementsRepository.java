package app.ghstats.api.achievements;

import app.ghstats.api.achievements.api.AchievementUnlocked;
import org.springframework.r2dbc.core.DatabaseClient;
import reactor.core.publisher.Mono;

public class SqlAchievementsRepository implements AchievementsRepository {

    private final DatabaseClient databaseClient;

    SqlAchievementsRepository(DatabaseClient databaseClient) {
        this.databaseClient = databaseClient;
    }

    @Override
    public Mono<Integer> saveAchievement(String achievementId, AchievementUnlocked achievement) {
        return databaseClient.sql("INSERT INTO `achievements_unlocked` (`user`, `commit_id`, `achievement_id`) VALUES (?, ?, ?)")
                .bind(0, achievement.userName().value())
                .bind(1, achievement.commitId().value())
                .bind(2, achievementId)
                .fetch()
                .rowsUpdated();
    }
}
