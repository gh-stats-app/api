package ghstats.api.achievements.impl;

import ghstats.api.CommitBuilder;
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

class OwlTest extends BaseAchievementTest {
    @Test
    void shouldUnlock() {
        //given
        UnlockableAchievement achievement = new Owl();
        List<GitCommit> commits = List.of(
                new CommitBuilder().withId("night-id").withTimestamp(ZonedDateTime.of(
                        LocalDateTime.of(2023, 12, 25, 5, 0),
                        ZoneId.systemDefault())).build()
        );

        //when
        Optional<AchievementUnlocked> check = achievement.unlock(commits);

        //expect
        Assertions.assertTrue(check.isPresent());
        Assertions.assertEquals("night-id", check.get().commit().id().value());
    }

    @Test
    void shouldNotUnlock() {
        //given
        UnlockableAchievement achievement = new Owl();
        List<GitCommit> commits = List.of(
                new CommitBuilder().withId("night-id").withTimestamp(ZonedDateTime.of(
                        LocalDateTime.of(2023, 12, 25, 7, 0),
                        ZoneId.systemDefault())).build(),
                new CommitBuilder().withId("night-id").withTimestamp(ZonedDateTime.of(
                        LocalDateTime.of(2023, 12, 25, 3, 59),
                        ZoneId.systemDefault())).build()
                );

        //when
        Optional<AchievementUnlocked> check = achievement.unlock(commits);

        //expect
        Assertions.assertTrue(check.isEmpty());
    }
}
