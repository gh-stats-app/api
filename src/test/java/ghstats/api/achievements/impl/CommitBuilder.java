package ghstats.api.achievements.impl;

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

class CommitBuilder {
    private String id = UUID.randomUUID().toString();
    private String authorUserId = UUID.randomUUID().toString();
    private String authorUserEmail = UUID.randomUUID() + "@example.com";
    private String message = UUID.randomUUID().toString();
    private ZonedDateTime timestamp = ZonedDateTime.now(ZoneId.systemDefault());
    private List<String> added = List.of();
    private List<String> removed = List.of();
    private List<String> modified = List.of();
    private String url = "https://allegro.pl";

    CommitBuilder withId(String id) {
        this.id = id;
        return this;
    }

    CommitBuilder withAuthorUserId(String authorUserId) {
        this.authorUserId = authorUserId;
        return this;
    }

    CommitBuilder withAuthorUserEmail(String authorUserEmail) {
        this.authorUserEmail = authorUserEmail;
        return this;
    }

    CommitBuilder withMessage(String message) {
        this.message = message;
        return this;
    }

    CommitBuilder withTimestamp(ZonedDateTime timestamp) {
        this.timestamp = timestamp;
        return this;
    }

    CommitBuilder withAdded(List<String> added) {
        this.added = added;
        return this;
    }

    CommitBuilder withModified(List<String> modified) {
        this.modified = modified;
        return this;
    }

    CommitBuilder withRemoved(List<String> removed) {
        this.removed = removed;
        return this;
    }


    CommitBuilder withUrl(String url) {
        this.url = url;
        return this;
    }

    GitCommit build() {
        return new GitCommit(
                CommitId.valueOf(this.id),
                new CommitAuthor(UserName.valueOf(this.authorUserId), UserEmail.valueOf(this.authorUserEmail)),
                this.message,
                this.timestamp,
                this.added,
                this.removed,
                this.modified,
                URI.create(url)
        );
    }
}
