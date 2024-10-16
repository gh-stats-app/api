package ghstats.api.achievements.impl;

import ghstats.api.CommitBuilder;
import ghstats.api.achievements.api.AchievementUnlocked;
import ghstats.api.achievements.api.UnlockableAchievement;
import ghstats.api.integrations.github.api.GitCommit;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

class WhyNotRubyTest extends BaseAchievementTest {
    @Test
    void shouldUnlock() {
        //given
        UnlockableAchievement achievement = new WhyNotRuby();
        List<GitCommit> commits = List.of(
                new CommitBuilder().withId("commit-id").withAdded(List.of("file.py")).build(),
                new CommitBuilder().build()
        );

        //when
        Optional<AchievementUnlocked> check = achievement.unlock(commits);

        //expect
        Assertions.assertTrue(check.isPresent());
        Assertions.assertEquals("commit-id", check.get().commit().id().value());
    }
}
