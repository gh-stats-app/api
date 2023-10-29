package ghstats.api.achievements.impl;

import ghstats.api.achievements.api.AchievementUnlocked;
import ghstats.api.achievements.api.UnlockableAchievement;
import ghstats.api.integrations.github.api.GitCommit;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

public class WeAreSafeNowTest extends BaseAchievementTest {

    @Test
    void shouldNotUnlockWhenNoSecureCommitPresent() {
        //given
        UnlockableAchievement achievement = new WeAreSafeNow();
        List<GitCommit> commits = List.of(
                commit("some commit"),
                commit("some other commit")
        );

        //when
        Optional<AchievementUnlocked> check = achievement.unlock(commits);

        //then
        Assertions.assertTrue(check.isEmpty());
    }

    @Test
    void shouldUnlockWhenSecureCommitPresent() {
        //given
        UnlockableAchievement achievement = new WeAreSafeNow();
        GitCommit secureCommit = commit("secure commit");
        List<GitCommit> commits = List.of(
                commit("some commit"),
                secureCommit,
                commit("some other commit")
        );

        //when
        Optional<AchievementUnlocked> check = achievement.unlock(commits);

        //then
        Assertions.assertTrue(check.isPresent());
        Assertions.assertEquals(secureCommit.id(), check.get().commit().id());
    }
}
