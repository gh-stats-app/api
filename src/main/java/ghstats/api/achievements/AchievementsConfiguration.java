package ghstats.api.achievements;

import ghstats.api.achievements.api.AchievementDefinition;
import ghstats.api.achievements.api.UnlockableAchievement;
import ghstats.api.infrastructure.DomainMetrics;
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
    AchievementsCommand achievementsCommand(
            List<UnlockableAchievement> achievements,
            AchievementsRepository achievementsRepository,
            DomainMetrics domainMetrics
    ) {
        return new AchievementsCommand(achievements, achievementsRepository, domainMetrics);
    }

    @Bean
    AchievementsQuery achievementsQuery(
            List<AchievementDefinition> achievements,
            AchievementsRepository achievementsRepository
    ) {
        return new AchievementsQuery(achievements, achievementsRepository);
    }
}
