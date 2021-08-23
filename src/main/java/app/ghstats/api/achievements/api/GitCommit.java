package app.ghstats.api.achievements.api;

import java.time.ZonedDateTime;
import java.util.List;

public record GitCommit(
        CommitId id,
        UserName userName,
        String message,
        ZonedDateTime timestamp,
        List<String> added,
        List<String> removed,
        List<String> modified
) {
}
