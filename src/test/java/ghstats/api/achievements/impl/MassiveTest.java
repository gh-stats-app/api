package ghstats.api.achievements.impl;

import ghstats.api.integrations.github.api.PullRequestFile;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class MassiveTest extends BaseAchievementTest {

    private final Massive achievement = new Massive();

    @Test
    void shouldUnlockWhenPullRequestAddsMoreThanOneThousandLines() {
        var files = List.of(
                file("first.java", PullRequestFile.Status.MODIFIED, 600, 10, null),
                file("second.java", PullRequestFile.Status.ADDED, 401, 0, null)
        );

        var result = achievement.unlock(context(List.of(commit("massive")), files));

        assertThat(result).isPresent();
    }

    @Test
    void shouldNotUnlockAtExactlyOneThousandAddedLines() {
        var files = List.of(file("large.java", PullRequestFile.Status.ADDED, 1_000, 0, null));

        var result = achievement.unlock(context(List.of(commit("not-massive")), files));

        assertThat(result).isEmpty();
    }
}
