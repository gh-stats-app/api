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

class RuinedChristmasTest extends BaseAchievementTest{

    @Test
    void shouldUnlock() {
        //given
        UnlockableAchievement achievement = new RuinedChristmas();
        List<GitCommit> commits = List.of(
                commit("message-1"),
                commit("christmas-commit", "message-2", ZonedDateTime.of(
                        LocalDateTime.of(2023, 12, 25, 12, 0),
                        ZoneId.systemDefault())
                ),
                commit("message-3")
        );

        //when
        Optional<AchievementUnlocked> check = achievement.unlock(commits);

        //expect
        Assertions.assertTrue(check.isPresent());
        Assertions.assertEquals("christmas-commit", check.get().commit().id().value());
    }
}
