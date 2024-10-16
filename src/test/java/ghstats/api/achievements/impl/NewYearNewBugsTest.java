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

class NewYearNewBugsTest extends BaseAchievementTest {
    @Test
    void shouldUnlock() {
        //given
        UnlockableAchievement achievement = new NewYearNewBugs();
        List<GitCommit> commits = List.of(
                new CommitBuilder().withId("new-year-id").withTimestamp(ZonedDateTime.of(
                        LocalDateTime.of(2024, 1, 1, 21, 37),
                        ZoneId.systemDefault())).build()
        );

        //when
        Optional<AchievementUnlocked> check = achievement.unlock(commits);

        //expect
        Assertions.assertTrue(check.isPresent());
        Assertions.assertEquals("new-year-id", check.get().commit().id().value());
    }

    @Test
    void shouldNotUnlock() {
        //given
        UnlockableAchievement achievement = new NewYearNewBugs();
        List<GitCommit> commits = List.of(
                new CommitBuilder().withId("new-year-id").withTimestamp(ZonedDateTime.of(
                        LocalDateTime.of(2023, 12, 31, 23, 59),
                        ZoneId.systemDefault())).build(),
                new CommitBuilder().withId("new-year-id").withTimestamp(ZonedDateTime.of(
                        LocalDateTime.of(2024, 5, 5, 5, 5),
                        ZoneId.systemDefault())).build(),
                new CommitBuilder().withId("new-year-id").withTimestamp(ZonedDateTime.of(
                        LocalDateTime.of(2024, 1, 2, 0, 0),
                        ZoneId.systemDefault())).build()
                );

        //when
        Optional<AchievementUnlocked> check = achievement.unlock(commits);

        //expect
        Assertions.assertTrue(check.isEmpty());
    }
}
