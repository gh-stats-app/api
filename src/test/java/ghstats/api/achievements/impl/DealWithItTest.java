package ghstats.api.achievements.impl;

import ghstats.api.CommitBuilder;
import ghstats.api.achievements.api.AchievementUnlocked;
import ghstats.api.achievements.api.UnlockableAchievement;
import ghstats.api.integrations.github.api.GitCommit;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

class DealWithItTest extends BaseAchievementTest {
    @Test
    void shouldUnlock() {
        //given
        UnlockableAchievement achievement = new DealWithIt();
        List<GitCommit> commits = List.of(
                new CommitBuilder().withId("on-master").withPushMetadata(new GitCommit.PushMetadata(true, "refs/heads/master")).build(),
                new CommitBuilder().withId("not-on-master").withPushMetadata(new GitCommit.PushMetadata(true, "refs/heads/other")).build()
        );

        //when
        Optional<AchievementUnlocked> check = achievement.unlock(commits);

        //expect
        Assertions.assertTrue(check.isPresent());
        Assertions.assertEquals("on-master", check.get().commit().id().value());
    }
}
