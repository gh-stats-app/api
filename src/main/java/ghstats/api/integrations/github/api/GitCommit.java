package ghstats.api.integrations.github.api;

import java.net.URI;
import java.time.ZonedDateTime;
import java.util.List;

public record GitCommit(
        CommitId id,
        CommitAuthor author,
        String message,
        ZonedDateTime timestamp,
        List<CommitId> parents,
        URI url,
        PushMetadata pushMetadata
) {
    public GitCommit {
        parents = List.copyOf(parents);
    }

    public record PushMetadata(
            Boolean forced,
            String ref
    ) {

    }
}
