package app.ghstats.api.achievements;

import app.ghstats.api.achievements.api.Achievement;
import app.ghstats.api.notifications.NotificationsCommand;
import app.ghstats.api.services.slack.SlackClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.r2dbc.core.DatabaseClient;

import java.util.List;

@Configuration
class AchievementsConfiguration {

    @Bean
    AchievementsRepository achievementsRepository(DatabaseClient databaseClient) {
        return new SqlAchievementsRepository(databaseClient);
    }

    @Bean
    AchievementsCommand achievementsCommand(List<Achievement> achievements,
                                            AchievementsRepository achievementsRepository,
                                            NotificationsCommand notificationsCommand) {
        return new AchievementsCommand(achievements, achievementsRepository, notificationsCommand);
    }

    @Bean
    AchievementsQuery achievementsQuery(List<Achievement> achievements) {
        return new AchievementsQuery(achievements);
    }
}
