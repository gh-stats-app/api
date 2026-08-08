package ghstats.api.achievements.impl;

import ghstats.api.CommitBuilder;
import ghstats.api.integrations.github.api.CommitId;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class HydraTest extends BaseAchievementTest {

    private final Hydra achievement = new Hydra();

    @Test
    void shouldUnlockForCommitWithThreeParents() {
        var hydraCommit = new CommitBuilder()
                .withId("hydra-commit")
                .withParents(List.of(
                        CommitId.valueOf("parent-1"),
                        CommitId.valueOf("parent-2"),
                        CommitId.valueOf("parent-3")
                ))
                .build();

        var result = achievement.unlock(context(List.of(hydraCommit)));

        assertThat(result).isPresent();
        assertThat(result.orElseThrow().commit()).isEqualTo(hydraCommit);
    }

    @Test
    void shouldNotUnlockForCommitWithTwoParents() {
        var mergeCommit = new CommitBuilder()
                .withParents(List.of(CommitId.valueOf("parent-1"), CommitId.valueOf("parent-2")))
                .build();

        var result = achievement.unlock(context(List.of(mergeCommit)));

        assertThat(result).isEmpty();
    }
}
