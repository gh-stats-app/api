package ghstats.api.achievements.impl;

import ghstats.api.integrations.github.api.GitCommit;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CatchphraseTest extends BaseAchievementTest {

    private final Catchphrase achievement = new Catchphrase();

    @Test
    void shouldUnlockOnTenthIdenticalMessage() {
        List<GitCommit> commits = repeatedCommits("same message", 10);

        var result = achievement.unlock(context(commits));

        assertThat(result).isPresent();
        assertThat(result.orElseThrow().commit()).isEqualTo(commits.getLast());
    }

    @Test
    void shouldNotUnlockForNineIdenticalMessages() {
        var result = achievement.unlock(context(repeatedCommits("same message", 9)));

        assertThat(result).isEmpty();
    }

    @Test
    void shouldCompareMessagesExactly() {
        List<GitCommit> commits = new ArrayList<>(repeatedCommits("same message", 5));
        commits.addAll(repeatedCommits("Same message", 5));

        var result = achievement.unlock(context(commits));

        assertThat(result).isEmpty();
    }

    private List<GitCommit> repeatedCommits(String message, int count) {
        return java.util.stream.IntStream.range(0, count)
                .mapToObj(ignored -> commit(message))
                .toList();
    }
}
