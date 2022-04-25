package ghstats.api.achievements.impl;

import ghstats.api.achievements.api.Achievement;
import ghstats.api.achievements.api.AchievementUnlocked;
import ghstats.api.achievements.api.GitCommit;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

class WowTest extends AchievementTest {

    @Test
    void shouldUnlock() {
        //given
        Achievement achievement = new Wow();
        List<GitCommit> commits = List.of(
                commit("message!"),
                commit("commit-id", "that was awesome! wow!"),
                commit("our Wowmaster is the best!")
        );

        //when
        Optional<AchievementUnlocked> check = achievement.unlock(commits);

        //expect
        Assertions.assertTrue(check.isPresent());
        Assertions.assertEquals("commit-id", check.get().commit().id().value());
    }
}
