package ghstats.api.achievements.impl;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class HolyWarTest extends BaseAchievementTest {

    private final HolyWar achievement = new HolyWar();

    @Test
    void shouldUnlockWhenTabsAreChangedToSpaces() {
        var file = patchedFile("Example.java", 1, 1,
                "@@ -1,1 +1,1 @@\n-\treturn value;\n+    return value;");

        var result = achievement.unlock(context(List.of(commit("tabs to spaces")), List.of(file)));

        assertThat(result).isPresent();
    }

    @Test
    void shouldNotUnlockForOnlySpaceCountChange() {
        var file = patchedFile("Example.java", 1, 1,
                "@@ -1,1 +1,1 @@\n-return  value;\n+return value;");

        var result = achievement.unlock(context(List.of(commit("spaces")), List.of(file)));

        assertThat(result).isEmpty();
    }

    @Test
    void shouldNotUnlockWhenCodeAlsoChanges() {
        var file = patchedFile("Example.java", 1, 1,
                "@@ -1,1 +1,1 @@\n-\treturn first;\n+    return second;");

        var result = achievement.unlock(context(List.of(commit("indent and code")), List.of(file)));

        assertThat(result).isEmpty();
    }
}
