package ghstats.api.achievements.web;

import ghstats.api.achievements.AchievementsQuery;
import ghstats.api.integrations.github.api.UserName;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/achievements")
class AchievementsController {
    private final AchievementsQuery achievementsQuery;

    AchievementsController(AchievementsQuery achievementsQuery) {
        this.achievementsQuery = achievementsQuery;
    }

    @GetMapping
    List<AchievementDefinitionResponse> listAchievements() {
        return achievementsQuery.getAllDefinitions()
                .stream()
                .map(it -> new AchievementDefinitionResponse(
                        it.getId(),
                        it.getDescription(),
                        it.getImage(),
                        it.getIcon()
                ))
                .collect(Collectors.toList());
    }

    @GetMapping("/top")
    Mono<Map<UserName, Long>> top() {
        return achievementsQuery.getScoreBoard();
    }

    @GetMapping("/last")
    Flux<Map<UserName, String>> last() {
        return achievementsQuery.getLastUnlocked();
    }

    @GetMapping("/stats")
    Mono<Map<String, Long>> stats() {
        return achievementsQuery.getUnlockedStats();
    }
}
