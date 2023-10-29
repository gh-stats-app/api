package ghstats.api;

import ghstats.api.integrations.github.api.CommitAuthor;
import ghstats.api.integrations.github.api.CommitId;
import ghstats.api.integrations.github.api.GitCommit;
import ghstats.api.integrations.github.api.UserEmail;
import ghstats.api.integrations.github.api.UserName;

import java.net.URI;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

public class CommitBuilder {
    private String id = UUID.randomUUID().toString();
    private String authorUserId = UUID.randomUUID().toString();
    private String authorUserEmail = UUID.randomUUID() + "@example.com";
    private String message = UUID.randomUUID().toString();
    private ZonedDateTime timestamp = ZonedDateTime.now(ZoneId.systemDefault());
    private List<String> added = List.of();
    private List<String> removed = List.of();
    private List<String> modified = List.of();
    private String url = "https://github.com";
    private GitCommit.PushMetadata pushMetadata = new GitCommit.PushMetadata(false, "refs/heads/" + UUID.randomUUID());

    public CommitBuilder withId(String id) {
        this.id = id;
        return this;
    }

    public CommitBuilder withAuthorUserId(String authorUserId) {
        this.authorUserId = authorUserId;
        return this;
    }

    public CommitBuilder withAuthorUserEmail(String authorUserEmail) {
        this.authorUserEmail = authorUserEmail;
        return this;
    }

    public CommitBuilder withMessage(String message) {
        this.message = message;
        return this;
    }

    public CommitBuilder withTimestamp(ZonedDateTime timestamp) {
        this.timestamp = timestamp;
        return this;
    }

    public CommitBuilder withAdded(List<String> added) {
        this.added = added;
        return this;
    }

    public CommitBuilder withModified(List<String> modified) {
        this.modified = modified;
        return this;
    }

    public CommitBuilder withRemoved(List<String> removed) {
        this.removed = removed;
        return this;
    }


    public CommitBuilder withUrl(String url) {
        this.url = url;
        return this;
    }

    public CommitBuilder withPushMetadata(GitCommit.PushMetadata metadata) {
        this.pushMetadata = metadata;
        return this;
    }

    public GitCommit build() {
        return new GitCommit(
                CommitId.valueOf(this.id),
                new CommitAuthor(
                        UserName.valueOf(this.authorUserId),
                        UserEmail.valueOf(this.authorUserEmail)
                ),
                this.message,
                this.timestamp,
                this.added,
                this.removed,
                this.modified,
                URI.create(url),
                this.pushMetadata
        );
    }
}
