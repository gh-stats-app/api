package ghstats.api.achievements.impl;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CommenterTest extends BaseAchievementTest {

    private final Commenter achievement = new Commenter();

    @Test
    void shouldUnlockWhenOnlyLineCommentIsAdded() {
        var file = patchedFile("Example.java", 1, 0, """
                @@ -1,1 +1,2 @@
                 class Example {}
                +// Explains the class.
                """);
        assertThat(GitHubFilePatch.parse(file)).isPresent();

        var result = achievement.unlock(context(List.of(commit("comment")), List.of(file)));

        assertThat(result).isPresent();
    }

    @Test
    void shouldUnlockWhenOnlyBlockCommentIsAdded() {
        var file = patchedFile("Example.java", 3, 0, """
                @@ -0,0 +1,3 @@
                +/*
                + * Explanation.
                + */
                """);

        var result = achievement.unlock(context(List.of(commit("block comment")), List.of(file)));

        assertThat(result).isPresent();
    }

    @Test
    void shouldNotUnlockWhenCommentAndCodeAreAdded() {
        var file = patchedFile("Example.java", 2, 0, """
                @@ -1,1 +1,3 @@
                 class Example {}
                +// Explanation.
                +class Other {}
                """);

        var result = achievement.unlock(context(List.of(commit("comment and code")), List.of(file)));

        assertThat(result).isEmpty();
    }

    @Test
    void shouldNotGuessCommentSyntaxForUnknownFileType() {
        var file = patchedFile("notes.txt", 1, 0, """
                @@ -0,0 +1,1 @@
                +# Maybe a comment.
                """);

        var result = achievement.unlock(context(List.of(commit("unknown")), List.of(file)));

        assertThat(result).isEmpty();
    }
}
