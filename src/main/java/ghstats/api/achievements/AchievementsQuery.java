package ghstats.api.achievements;

import ghstats.api.achievements.api.AchievementDefinition;
import ghstats.api.integrations.github.api.UserName;
import org.springframework.data.util.Pair;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

public class AchievementsQuery {

    private final List<AchievementDefinition> achievements;
    private final AchievementsRepository achievementsRepository;

    public AchievementsQuery(List<AchievementDefinition> achievements, AchievementsRepository achievementsRepository) {
        this.achievements = achievements;
        this.achievementsRepository = achievementsRepository;
    }

    public List<AchievementDefinition> getAllDefinitions() {
        return achievements;
    }

    public Flux<Map<UserName, String>> getLastUnlocked() {
        return achievementsRepository.getLastUnlocked(10);
    }

    public Mono<Map<UserName, Long>> getScoreBoard() {
        return achievementsRepository.getScoreboard();
    }
}
