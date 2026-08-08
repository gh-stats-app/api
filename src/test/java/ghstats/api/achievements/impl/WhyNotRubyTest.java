package ghstats.api.achievements.impl;

import ghstats.api.CommitBuilder;
import ghstats.api.achievements.api.AchievementUnlocked;
import ghstats.api.achievements.api.UnlockableAchievement;
import ghstats.api.integrations.github.api.GitCommit;
import ghstats.api.integrations.github.api.PullRequestFile;
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
                new CommitBuilder().build(),
                new CommitBuilder().withId("commit-id").build()
        );

        //when
        Optional<AchievementUnlocked> check = achievement.unlock(context(
                commits,
                List.of(file("file.py", PullRequestFile.Status.ADDED))
        ));

        //expect
        Assertions.assertTrue(check.isPresent());
        Assertions.assertEquals("commit-id", check.get().commit().id().value());
    }
}
