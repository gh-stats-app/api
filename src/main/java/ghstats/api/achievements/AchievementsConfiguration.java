package ghstats.api.achievements;

import ghstats.api.achievements.api.AchievementDefinition;
import ghstats.api.achievements.api.UnlockableAchievement;
import ghstats.api.notifications.NotificationsCommand;
import io.micrometer.core.instrument.MeterRegistry;
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
    AchievementsCommand achievementsCommand(List<UnlockableAchievement> achievements,
                                            AchievementsRepository achievementsRepository,
                                            NotificationsCommand notificationsCommand,
                                            MeterRegistry meterRegistry) {
        return new AchievementsCommand(achievements, achievementsRepository, notificationsCommand, meterRegistry);
    }

    @Bean
    AchievementsQuery achievementsQuery(List<AchievementDefinition> achievements) {
        return new AchievementsQuery(achievements);
    }
}
