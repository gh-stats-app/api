package ghstats.api.achievements.impl;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class EasyFixTest extends BaseAchievementTest {

    private final EasyFix achievement = new EasyFix();

    @Test
    void shouldUnlockWhenTwoLinesAreSwapped() {
        var file = patchedFile("Example.java", 1, 1, """
                @@ -1,3 +1,3 @@
                -alpha
                 beta
                +alpha
                 gamma
                """);
        assertThat(GitHubFilePatch.parse(file)).isPresent();

        var result = achievement.unlock(context(List.of(commit("swap")), List.of(file)));

        assertThat(result).isPresent();
    }

    @Test
    void shouldNotUnlockForOrdinaryLineReplacement() {
        var file = patchedFile("Example.java", 1, 1, """
                @@ -1,1 +1,1 @@
                -alpha
                +beta
                """);

        var result = achievement.unlock(context(List.of(commit("replace")), List.of(file)));

        assertThat(result).isEmpty();
    }
}
