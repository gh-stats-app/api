package ghstats.api.integrations.github.api;

import java.net.URI;
import java.time.ZonedDateTime;
import java.util.List;

public record GitCommit(
        CommitId id,
        CommitAuthor author,
        String message,
        ZonedDateTime timestamp,
        List<String> added,
        List<String> removed,
        List<String> modified,
        URI url,
        PushMetadata pushMetadata
) {
    public record PushMetadata(
            Boolean forced,
            String ref
    ) {

    }
}
