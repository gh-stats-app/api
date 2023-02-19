package ghstats.api.achievements.web;

import ghstats.api.achievements.AchievementsQuery;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
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
        return achievementsQuery.getAll()
                .stream()
                .map(it -> new AchievementDefinitionResponse(
                        it.getId(),
                        it.getDescription(),
                        it.getImage(),
                        it.getIcon()
                ))
                .collect(Collectors.toList());
    }
}
