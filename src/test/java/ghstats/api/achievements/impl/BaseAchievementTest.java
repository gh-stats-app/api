package ghstats.api.achievements.impl;

import ghstats.api.integrations.github.api.CommitAuthor;
import ghstats.api.integrations.github.api.CommitId;
import ghstats.api.integrations.github.api.GitCommit;
import ghstats.api.integrations.github.api.OrganisationName;
import ghstats.api.integrations.github.api.RepositoryName;
import ghstats.api.integrations.github.api.UserEmail;
import ghstats.api.integrations.github.api.UserName;

import java.net.URI;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

abstract class BaseAchievementTest {
    GitCommit commit(String message) {
        return commit(UUID.randomUUID().toString(), message);
    }

    GitCommit commitWithModified(String id, List<String> modified) {
        return new GitCommit(
                CommitId.valueOf(id),
                new CommitAuthor(UserName.valueOf("bgalek"), UserEmail.valueOf("bgalek@github.com")),
                UUID.randomUUID().toString(),
                ZonedDateTime.now(ZoneId.systemDefault()),
                List.of(),
                List.of(),
                modified,
                URI.create("/")
        );
    }

    GitCommit commit(String id, String message) {
        return commit(id, message, ZonedDateTime.now(ZoneId.systemDefault()));
    }

    GitCommit commitWithRefAndForced(String id, String ref, Boolean forced) {
        return new GitCommit(
                CommitId.valueOf(id),
                new CommitAuthor(UserName.valueOf("bgalek"), UserEmail.valueOf("bgalek@github.com")),
                UUID.randomUUID().toString(),
                ZonedDateTime.now(ZoneId.systemDefault()),
                List.of(),
                List.of(),
                List.of(),
                URI.create("/"),
                new GitCommit.PushMetadata(forced, ref)
        );
    }

    GitCommit commit(String id, String message, ZonedDateTime timestamp) {
        return new GitCommit(
                CommitId.valueOf(id),
                new CommitAuthor(UserName.valueOf("bgalek"), UserEmail.valueOf("bgalek@github.com")),
                message,
                timestamp,
                List.of(),
                List.of(),
                List.of(),
                URI.create("/"),
                new GitCommit.PushMetadata(false, "ref/heads/master")
        );
    }
}
