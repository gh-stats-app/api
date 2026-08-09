package ghstats.api.achievements.impl;

import ghstats.api.CommitBuilder;
import ghstats.api.integrations.github.api.CommitId;
import ghstats.api.integrations.github.api.GitCommit;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

class WorkerBeeTest extends BaseAchievementTest {

    private final WorkerBee achievement = new WorkerBee();

    @Test
    void shouldUnlockOnOneHundredthNonMergeCommit() {
        List<GitCommit> commits = nonMergeCommits(100);

        var result = achievement.unlock(context(commits));

        assertThat(result).isPresent();
        assertThat(result.orElseThrow().commit()).isEqualTo(commits.getLast());
    }

    @Test
    void shouldNotUnlockForNinetyNineNonMergeCommits() {
        var result = achievement.unlock(context(nonMergeCommits(99)));

        assertThat(result).isEmpty();
    }

    @Test
    void shouldExcludeMergeCommitsFromCount() {
        List<GitCommit> commits = new ArrayList<>(nonMergeCommits(99));
        commits.add(new CommitBuilder()
                .withParents(List.of(CommitId.valueOf("first-parent"), CommitId.valueOf("second-parent")))
                .build());

        var result = achievement.unlock(context(commits));

        assertThat(result).isEmpty();
    }

    private List<GitCommit> nonMergeCommits(int count) {
        return IntStream.range(0, count)
                .mapToObj(index -> new CommitBuilder()
                        .withId("commit-" + index)
                        .withParents(List.of(CommitId.valueOf("parent-" + index)))
                        .build())
                .toList();
    }
}
