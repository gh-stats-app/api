package ghstats.api.achievements;

import ghstats.api.achievements.api.AchievementUnlocked;
import ghstats.api.integrations.github.api.UserName;
import org.springframework.data.util.Pair;
import org.springframework.r2dbc.core.DatabaseClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

class SqlAchievementsRepository implements AchievementsRepository {

    private final DatabaseClient databaseClient;

    SqlAchievementsRepository(DatabaseClient databaseClient) {
        this.databaseClient = databaseClient;
    }

    @Override
    public Mono<Long> saveAchievement(String achievementId, AchievementUnlocked achievementUnlocked) {
        return databaseClient.sql("INSERT IGNORE INTO `achievements_unlocked` (`user`, `commit_id`, `url`, `achievement_id`) VALUES (?, ?, ?, ?)")
                .bind(0, achievementUnlocked.commit().author().userName().value())
                .bind(1, achievementUnlocked.commit().id().value())
                .bind(2, achievementUnlocked.commit().url().toString())
                .bind(3, achievementId)
                .fetch()
                .rowsUpdated();
    }

    @Override
    public Flux<UnlockData> getLastUnlocked(int limit) {
        return databaseClient.sql("SELECT `user`, `achievement_id`, `created_at` FROM `achievements_unlocked` ORDER BY created_at DESC LIMIT ?")
                .bind(0, limit)
                .map(row -> new UnlockData(
                        UserName.valueOf(row.get("user", String.class)),
                        Objects.requireNonNull(row.get("achievement_id", String.class)),
                        row.get("created_at", LocalDateTime.class).atZone(ZoneId.of("Europe/Warsaw"))))
                .all();
    }

    @Override
    public Mono<Map<String, Long>> getUnlockedStats() {
        return databaseClient.sql("select achievement_id, count(achievement_id) as count from achievements_unlocked group by achievement_id order by count desc")
                .flatMap(result -> result.map(row -> Pair.of(
                        Objects.requireNonNull(row.get("achievement_id", String.class)),
                        Objects.requireNonNull(row.get("count", Long.class)))))
                .collect(Collectors.toMap(Pair::getFirst, Pair::getSecond));
    }

    @Override
    public Mono<Map<UserName, Long>> getScoreboard() {
        return databaseClient.sql("""
                        select `user`, count(achievement_id) as count
                        from achievements_unlocked
                        group by user
                        order by count desc
                        limit 10
                        """)
                .flatMap(result -> result.map(row -> Pair.of(
                        UserName.valueOf(row.get("user", String.class)),
                        Objects.requireNonNull(row.get("count", Long.class)))))
                .collect(Collectors.toMap(Pair::getFirst, Pair::getSecond));
    }

    @Override
    public Flux<String> getUnlockedAchievements(UserName userName) {
        return databaseClient.sql("SELECT * FROM `achievements_unlocked` WHERE `user` = ?")
                .bind(0, userName.value())
                .map(it -> it.get("achievement_id", String.class))
                .all();
    }
}