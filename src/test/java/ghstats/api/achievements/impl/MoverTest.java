package ghstats.api.achievements.impl;

import ghstats.api.integrations.github.api.PullRequestFile;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class MoverTest extends BaseAchievementTest {

    private final Mover achievement = new Mover();

    @Test
    void shouldUnlockForPureFileRename() {
        var renamed = file("new-name.java", PullRequestFile.Status.RENAMED, 0, 0, "old-name.java");

        var result = achievement.unlock(context(List.of(commit("move")), List.of(renamed)));

        assertThat(result).isPresent();
    }

    @Test
    void shouldNotUnlockWhenRenamedFileContentsChanged() {
        var renamed = file("new-name.java", PullRequestFile.Status.RENAMED, 1, 1, "old-name.java");

        var result = achievement.unlock(context(List.of(commit("move and edit")), List.of(renamed)));

        assertThat(result).isEmpty();
    }

    @Test
    void shouldNotUnlockForUnchangedPath() {
        var modified = file("same-name.java", PullRequestFile.Status.MODIFIED, 0, 0, null);

        var result = achievement.unlock(context(List.of(commit("touch")), List.of(modified)));

        assertThat(result).isEmpty();
    }
}
