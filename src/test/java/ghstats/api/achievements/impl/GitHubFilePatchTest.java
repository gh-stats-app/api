package ghstats.api.achievements.impl;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class GitHubFilePatchTest extends BaseAchievementTest {

    @Test
    void shouldParseCompletePatchWithCompactHunkRangeAndSectionHeading() {
        var file = patchedFile("Example.java", 1, 1, """
                @@ -1 +1 @@ public class Example
                -old
                +new
                """);

        var result = GitHubFilePatch.parse(file);

        assertThat(result).isPresent();
        assertThat(result.orElseThrow().alignedChanges()).hasValueSatisfying(changes ->
                assertThat(changes).containsExactly(new GitHubFilePatch.LineChange("old", "new"))
        );
    }

    @Test
    void shouldRejectMissingPatch() {
        assertThat(GitHubFilePatch.parse(patchedFile("Example.java", 1, 0, null))).isEmpty();
    }

    @Test
    void shouldRejectTruncatedHunk() {
        var file = patchedFile("Example.java", 1, 1, """
                @@ -1,3 +1,3 @@
                -first
                 second
                +first
                """);

        assertThat(GitHubFilePatch.parse(file)).isEmpty();
    }

    @Test
    void shouldRejectPatchWhenMetadataReportsMoreChanges() {
        var file = patchedFile("Example.java", 2, 1, """
                @@ -1,1 +1,1 @@
                -old
                +new
                """);

        assertThat(GitHubFilePatch.parse(file)).isEmpty();
    }
}
