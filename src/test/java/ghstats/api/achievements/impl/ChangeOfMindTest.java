package ghstats.api.achievements.impl;

import ghstats.api.CommitBuilder;
import ghstats.api.achievements.api.AchievementUnlocked;
import ghstats.api.achievements.api.UnlockableAchievement;
import ghstats.api.integrations.github.api.GitCommit;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

class ChangeOfMindTest extends BaseAchievementTest {
    @Test
    void shouldUnlock() {
        //given
        UnlockableAchievement achievement = new ChangeOfMind();
        List<GitCommit> commits = List.of(
                new CommitBuilder().withModified(List.of("file.yaml")).build(),
                new CommitBuilder().withId("license-change").withModified(List.of("license.md")).build()
        );

        //when
        Optional<AchievementUnlocked> check = achievement.unlock(commits);

        //expect
        Assertions.assertTrue(check.isPresent());
        Assertions.assertEquals("license-change", check.get().commit().id().value());
    }
}
