package ghstats.api.achievements.api;

import java.time.ZonedDateTime;
import java.util.List;

public record GitCommit(
        CommitId id,
        CommitAuthor author,
        String message,
        ZonedDateTime timestamp,
        List<String> added,
        List<String> removed,
        List<String> modified
) {
}
