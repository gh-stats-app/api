package ghstats.api.achievements;

import ghstats.api.achievements.api.AchievementUnlocked;
import org.springframework.r2dbc.core.DatabaseClient;
import reactor.core.publisher.Mono;

class SqlAchievementsRepository implements AchievementsRepository {

    private final DatabaseClient databaseClient;

    SqlAchievementsRepository(DatabaseClient databaseClient) {
        this.databaseClient = databaseClient;
    }

    @Override
    public Mono<Long> saveAchievement(String achievementId, AchievementUnlocked achievementUnlocked) {
        return databaseClient.sql("INSERT IGNORE INTO `achievements_unlocked` (`user`, `commit_id`, `url`, `achievement_id`) VALUES ('%s', '%s', '%s', '%s')".formatted(
                        achievementUnlocked.commit().author().userName().value(),
                        achievementUnlocked.commit().id().value(),
                        achievementUnlocked.commit().url().toString(),
                        achievementId
                ))
                .fetch()
                .rowsUpdated();
    }
}