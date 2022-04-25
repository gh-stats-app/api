package ghstats.api.achievements.impl;

import ghstats.api.achievements.api.CommitAuthor;
import ghstats.api.achievements.api.CommitId;
import ghstats.api.achievements.api.GitCommit;
import ghstats.api.achievements.api.UserEmail;
import ghstats.api.achievements.api.UserName;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

abstract class AchievementTest {
    GitCommit commit(String message) {
        return commit(UUID.randomUUID().toString(), message);
    }

    GitCommit commit(String id, String message) {
        return new GitCommit(CommitId.valueOf(id), new CommitAuthor(UserName.valueOf("bgalek"), UserEmail.valueOf("bgalek@github.com")), message, ZonedDateTime.now(), List.of(), List.of(), List.of());
    }
}
