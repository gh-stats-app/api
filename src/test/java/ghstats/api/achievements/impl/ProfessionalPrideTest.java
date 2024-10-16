package ghstats.api.achievements.impl;

import ghstats.api.achievements.api.AchievementUnlocked;
import ghstats.api.achievements.api.UnlockableAchievement;
import ghstats.api.integrations.github.api.GitCommit;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

class ProfessionalPrideTest extends BaseAchievementTest {

    @Test
    void shouldUnlock() {
        //given
        UnlockableAchievement achievement = new ProfessionalPride();
        List<GitCommit> commits = List.of(
                commit("commit-id-1", "message-1", ZonedDateTime.of(
                        LocalDateTime.of(2022, 1, 1, 0, 0),
                        ZoneId.systemDefault())
                ),
                commit("commit-id-2", "message-2", ZonedDateTime.of(
                        LocalDateTime.of(2022, 9, 13, 0, 0),
                        ZoneId.systemDefault())
                ), // 256th day of 2022
                commit("commit-id-3", "message-3", ZonedDateTime.of(
                        LocalDateTime.of(2022, 12, 31, 0, 0),
                        ZoneId.systemDefault())
                )
        );

        //when
        Optional<AchievementUnlocked> check = achievement.unlock(commits);

        //expect
        Assertions.assertTrue(check.isPresent());
        Assertions.assertEquals("commit-id-2", check.get().commit().id().value());
    }
}
