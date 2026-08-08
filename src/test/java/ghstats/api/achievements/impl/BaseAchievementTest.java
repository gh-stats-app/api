package ghstats.api.achievements.impl;

import ghstats.api.achievements.api.PullRequestContext;
import ghstats.api.achievements.api.PullRequestSnapshot;
import ghstats.api.integrations.github.api.CommitAuthor;
import ghstats.api.integrations.github.api.CommitId;
import ghstats.api.integrations.github.api.GitCommit;
import ghstats.api.integrations.github.api.GithubUser;
import ghstats.api.integrations.github.api.OrganisationName;
import ghstats.api.integrations.github.api.PullRequestFile;
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

    GitCommit commit(String id, String message) {
        return commit(id, message, ZonedDateTime.now(ZoneId.systemDefault()));
    }

    GitCommit commit(String id, String message, ZonedDateTime timestamp) {
        return new GitCommit(
                CommitId.valueOf(id),
                new CommitAuthor(UserName.valueOf("bgalek"), UserEmail.valueOf("bgalek@github.com")),
                message,
                timestamp,
                List.of(),
                URI.create("/"),
                new GitCommit.PushMetadata(false, "refs/heads/master")
        );
    }

    PullRequestContext context(List<GitCommit> commits) {
        return context(commits, List.of());
    }

    PullRequestContext context(List<GitCommit> commits, List<PullRequestFile> files) {
        var recipient = new GithubUser(UserName.valueOf("bgalek"), "User");
        var pullRequest = new PullRequestSnapshot(
                OrganisationName.valueOf("gh-stats-app"),
                RepositoryName.valueOf("test-repository"),
                1,
                commits.getLast().id(),
                URI.create("https://github.com/gh-stats-app/test-repository/pull/1")
        );
        return new PullRequestContext(recipient, pullRequest, commits, files);
    }

    PullRequestFile file(String filename, PullRequestFile.Status status) {
        return new PullRequestFile(filename, status, 0, 0, 0, null, null);
    }

    PullRequestFile file(
            String filename,
            PullRequestFile.Status status,
            int additions,
            int deletions,
            String previousFilename
    ) {
        return new PullRequestFile(
                filename,
                status,
                additions,
                deletions,
                additions + deletions,
                previousFilename,
                null
        );
    }
}
