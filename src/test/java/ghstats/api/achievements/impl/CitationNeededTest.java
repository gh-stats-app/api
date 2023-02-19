package ghstats.api.achievements.impl;

import ghstats.api.achievements.api.AchievementUnlocked;
import ghstats.api.integrations.github.api.GitCommit;
import ghstats.api.achievements.api.UnlockableAchievement;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

class CitationNeededTest extends BaseAchievementTest {

    @Test
    void shouldUnlock() {
        //given
        UnlockableAchievement achievement = new CitationNeeded();
        List<GitCommit> commits = List.of(
                commit("message!"),
                commit("commit-id", "check that out! https://stackoverflow.com/questions/121212121212/very-important-question"),
                commit("stackoverflow.com is the best!")
        );

        //when
        Optional<AchievementUnlocked> check = achievement.unlock(commits);

        //expect
        Assertions.assertTrue(check.isPresent());
        Assertions.assertEquals("commit-id", check.get().commit().id().value());
    }
}
