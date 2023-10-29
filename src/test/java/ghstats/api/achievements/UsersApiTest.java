package ghstats.api.achievements;

import ghstats.api.BaseIntegrationTest;
import ghstats.api.CommitBuilder;
import ghstats.api.achievements.api.AchievementUnlocked;
import ghstats.api.achievements.api.UnlockableAchievement;
import ghstats.api.integrations.github.api.GitCommit;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

class UsersApiTest extends BaseIntegrationTest {
    @Autowired
    private AchievementsRepository achievementsRepository;

    @Autowired
    List<UnlockableAchievement> achievements;

    @Test
    @DisplayName("should be able to return users unlocked achievements")
    void checkUser() {
        // given
        GitCommit commit = new CommitBuilder().build();
        UnlockableAchievement achievement = achievements.getFirst();
        achievementsRepository.saveAchievementUnlock(new AchievementUnlocked(achievement, commit)).block();

        // expect
        webClient.get()
                .uri("/users/" + commit.author().userName().value())
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .jsonPath("$[0].achievement.id").isEqualTo(achievement.getId())
                .jsonPath("$[0].commitId").isEqualTo(commit.id().value());
    }
}
