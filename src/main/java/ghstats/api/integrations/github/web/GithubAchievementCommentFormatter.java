package ghstats.api.integrations.github.web;

import ghstats.api.achievements.api.AchievementUnlocked;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
class GithubAchievementCommentFormatter {

    static final String COMMENT_MARKER = "<!-- gh-stats-achievements -->";

    String formatPreview(String userName) {
        return """
                %s
                ## Achievement Pending

                @%s, this PR looks like it will unlock an achievement when merged.
                """.formatted(COMMENT_MARKER, userName);
    }

    String format(List<AchievementUnlocked> unlocks) {
        String rows = unlocks.stream()
                .map(u -> "| ![](https://api.gh-stats.app/img/%s.png) | **%s** - %s | @%s |".formatted(
                        u.achievement().getId(),
                        u.achievement().getName(),
                        u.achievement().getDescription(),
                        u.commit().author().userName().value()))
                .collect(Collectors.joining("\n"));

        return """
                %s
                ## Achievements Unlocked

                | | Achievement | Unlocked by |
                |---|---|---|
                %s
                """.formatted(COMMENT_MARKER, rows);
    }
}
