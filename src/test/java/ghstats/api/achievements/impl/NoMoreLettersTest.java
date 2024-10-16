package ghstats.api.achievements.impl;

import ghstats.api.achievements.api.AchievementUnlocked;
import ghstats.api.integrations.github.api.GitCommit;
import ghstats.api.achievements.api.UnlockableAchievement;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

class NoMoreLettersTest extends BaseAchievementTest {

    @Test
    void shouldUnlock() {
        //given
        UnlockableAchievement achievement = new NoMoreLetters();
        List<GitCommit> commits = List.of(
                commit("message!"),
                commit("commit-id", "1234567890"),
                commit("qwerty")
        );

        //when
        Optional<AchievementUnlocked> check = achievement.unlock(commits);

        //expect
        Assertions.assertTrue(check.isPresent());
        Assertions.assertEquals("commit-id", check.get().commit().id().value());
    }
}