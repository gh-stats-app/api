package ghstats.api.achievements.impl;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class OCDTest extends BaseAchievementTest {

    private final OCD achievement = new OCD();

    @Test
    void shouldUnlockWhenOnlyTrailingSpacesAreRemoved() {
        var file = patchedFile("Example.java", 1, 1,
                "@@ -1,1 +1,1 @@\n-value;   \n+value;");
        assertThat(GitHubFilePatch.parse(file)).isPresent();

        var result = achievement.unlock(context(List.of(commit("trailing spaces")), List.of(file)));

        assertThat(result).isPresent();
    }

    @Test
    void shouldUnlockWhenSomeTrailingSpacesRemain() {
        var file = patchedFile("Example.java", 1, 1,
                "@@ -1,1 +1,1 @@\n-value;   \n+value; ");

        var result = achievement.unlock(context(List.of(commit("fewer trailing spaces")), List.of(file)));

        assertThat(result).isPresent();
    }

    @Test
    void shouldNotUnlockWhenLeadingSpacesAreRemoved() {
        var file = patchedFile("Example.java", 1, 1,
                "@@ -1,1 +1,1 @@\n-    value;\n+value;");

        var result = achievement.unlock(context(List.of(commit("leading spaces")), List.of(file)));

        assertThat(result).isEmpty();
    }

    @Test
    void shouldNotUnlockWhenContentAlsoChanges() {
        var file = patchedFile("Example.java", 1, 1,
                "@@ -1,1 +1,1 @@\n-first;   \n+second;");

        var result = achievement.unlock(context(List.of(commit("content")), List.of(file)));

        assertThat(result).isEmpty();
    }
}
