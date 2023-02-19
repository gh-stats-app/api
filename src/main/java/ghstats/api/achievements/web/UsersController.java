package ghstats.api.achievements.web;

import ghstats.api.achievements.AchievementsQuery;
import ghstats.api.achievements.api.AchievementDefinition;
import ghstats.api.integrations.github.api.UserName;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/users")
class UsersController {
    private final AchievementsQuery achievementsQuery;

    UsersController(AchievementsQuery achievementsQuery) {
        this.achievementsQuery = achievementsQuery;
    }

    @GetMapping("/{username}")
    Mono<List<AchievementDefinition>> listAchievements(@PathVariable String username) {
        return achievementsQuery.getUnlockedAchievements(UserName.valueOf(username));
    }
}
