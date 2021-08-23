package app.ghstats.api.achievements;

import org.springframework.r2dbc.core.DatabaseClient;

public class AchievementsQuery {
    private final DatabaseClient databaseClient;

    public AchievementsQuery(DatabaseClient databaseClient) {
        this.databaseClient = databaseClient;
    }
}
